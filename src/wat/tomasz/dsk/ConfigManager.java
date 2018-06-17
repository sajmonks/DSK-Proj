package wat.tomasz.dsk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import Answers.Answer;
import Nodes.Node;
import Surveys.SurveyHolder;
import wat.tomasz.dsk.Files.FileManager;
import wat.tomasz.dsk.Utils.Utils;

public class ConfigManager {
	
	enum MissingFiles { None, Keys, Surveys, Answers, Local, Nodes }
	
	private PublicKey publicKey;
	private PrivateKey privateKey;
	
	private int selfId = -1;
	private int listenPort = 0;
	
	ConfigManager() {
		
	}
	
	public void generateKeys() {
		KeyPairGenerator keyGen = null;
		SecureRandom random;
		try {
			keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
			random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
			return;
		}
		
		keyGen.initialize(512, random);
		KeyPair pair = keyGen.generateKeyPair();
		PrivateKey privKey = pair.getPrivate();
		PublicKey pubKey = pair.getPublic();
		
		byte [] privData = privKey.getEncoded();
		byte [] pubData = pubKey.getEncoded();
		
		try {
			FileOutputStream keyStream = new FileOutputStream("public.key");
			keyStream.write(pubData);
			keyStream.close();
			keyStream = new FileOutputStream("private.key");
			keyStream.write(privData);
			keyStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		privateKey = privKey;
		setPublicKey(pubKey);
	}
	
	private boolean loadPublicKey() {
		byte [] key = null;	
		try {
			FileInputStream keyStream = new FileInputStream("public.key");
			key = new byte [keyStream.available()];
			keyStream.read(key);
			keyStream.close();
		} catch (IOException e) {
			System.out.println("Nie uda³o siê wczytywaæ klucza publicznego.");
			return false;
		}
		
		try {
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
			setPublicKey(keyFactory.generatePublic(pubKeySpec));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Wczytano klucz publiczny. " + Utils.getPublicKeyString(publicKey));
		return true;
	}
	
	private boolean loadPrivateKey() {
		byte [] key = null;	
		try {
			FileInputStream keyStream = new FileInputStream("private.key");
			key = new byte [keyStream.available()];
			keyStream.read(key);
			keyStream.close();
		} catch (IOException e) {
			System.out.println("Nie uda³o siê wczytywaæ klucza prywatnego.");
			return false;
		}
		
		try {
			PKCS8EncodedKeySpec pubKeySpec = new PKCS8EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
			privateKey = keyFactory.generatePrivate(pubKeySpec);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Wczytano klucz prywatny. " + Utils.getPrivateKeyString(privateKey));
		return true;
	}
	
	public void loadKeys() {
		loadPublicKey();
		loadPrivateKey();
	}
	
	public void loadParameters() {
		int[] parameters = FileManager.loadParameters();	
		if(parameters != null) {
			selfId = parameters[0];
			listenPort = parameters[1];
			System.out.println("Za³adowano id="+selfId + ", nasluchiwany port=" + listenPort);
		}
	}
	
	public List<MissingFiles> createMissingFiles() {
		
		List<MissingFiles> missing = new ArrayList<MissingFiles>();
		if( !(new File("private.key").exists()) || !(new File("public.key").exists()) ) {
			System.out.println("Brak pary kluczy. Generowanie nowych, usuniêcie z sieci.");
			missing.add(MissingFiles.Keys);
		}
		else {
			loadKeys();
		}
		
		if( !(new File("local.txt").exists()) ) {
			System.out.println("Brak pliku local.txt");
			missing.add(MissingFiles.Local);
		}
		
		if( !(new File("surveys.txt").exists()) ) {
			System.out.println("Tworzenie pliku do przechowywania ankiet.");
			try {
				new File("surveys.txt").createNewFile();
			} catch (IOException e) {
				System.out.println("Nie uda³o zapisaæ siê pliku docelowego surveys.txt");
			}
			
			missing.add(MissingFiles.Surveys);
		}
		
		if( !(new File("answers.txt").exists()) ) {
			System.out.println("Tworzenie pliku do przechowywania odpowiedzi.");
			try {
				new File("answers.txt").createNewFile();
			} catch (IOException e) {
				System.out.println("Nie uda³o zapisaæ siê pliku docelowego answers.txt");
			}
			missing.add(MissingFiles.Answers);
		}
		
		if( !(new File("nodes.txt").exists()) ) {
			System.out.println("Tworzenie pliku do przechowywani u¿ytkowników.");
			try {
				new File("nodes.txt").createNewFile();
			} catch (IOException e) {
				System.out.println("Nie uda³o zapisaæ siê pliku docelowego nodes.txt");
			}
			missing.add(MissingFiles.Nodes);
		}
		return missing;
	}
	
	public void loadNodes(Survey survey) {
		try (BufferedReader br = new BufferedReader(new FileReader("nodes.txt"))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				String [] split = line.split(" ");
				
				if(split.length != 4)
					continue;
				
				int id = Utils.getInt(split[0]);
				InetAddress address = Utils.getAddress(split[1]);
				int port = Utils.getInt(split[2]);
				PublicKey key = Utils.getPublicKeyFromString(split[3]);
				
				if(!survey.getNodesManager().nodeExists(id))
					survey.getNodesManager().setNode(id, new Node(address, port, key), false);
			}	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadAnswers(Survey survey) {
		try (BufferedReader br = new BufferedReader(new FileReader("answers.txt"))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				String [] split = line.split(" ");
				
				
				if(split.length != 4)
					continue;
				
				int author = Utils.getInt(split[0]);
				int question = Utils.getInt(split[1]);
				int answer = Utils.getInt(split[2]);
				
				if(survey.getAnswersManager().isValidAuthor(author, question))
					survey.getAnswersManager().addAnswer(new Answer(author, question, answer, split[3]), false);
			}	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadSurveys(Survey survey) {
		try (BufferedReader br = new BufferedReader(new FileReader("surveys.txt"))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				String [] split = line.split(" ");
				
				if(split.length < 5)
					continue;
				
				int id = Utils.getInt(split[0]);
				int author = Utils.getInt(split[1]);;
				int type = Utils.getInt(split[2]);;
				String signature = split[3];
				
				ArrayList<String> answers = new ArrayList<String>();
				String title = null;
				String [] ansSplit = line.split("/");
				boolean start = false;
				for(int i = 1; i < ansSplit.length - 1; i++) {
						if(ansSplit[i].equals("START_PACK")) {
							start = true;
							title = ansSplit[i+1];
							i += 2;
						}
						if(start) {
							if(ansSplit[i].equals("END_PACK"))
								break;
							else
								answers.add(ansSplit[i]);		
						}
				}
				
				if(!survey.getSurveysManager().surveyExists(id))
					survey.getSurveysManager().setSurvey(id, new SurveyHolder(author, type, title, answers, signature) , false);
				
			}	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}
	
	public int getListenPort() {
		return listenPort;
	}
	
	public int getSelfId() {
		return selfId;
	}
	
	public void setSelfId(int id) {
		selfId = id;
	}
	
	
}
