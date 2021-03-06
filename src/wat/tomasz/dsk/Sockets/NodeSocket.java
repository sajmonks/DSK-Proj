package wat.tomasz.dsk.Sockets;

import java.net.InetAddress;
import java.security.PublicKey;
import java.util.ArrayList;

import Answers.Answer;
import Nodes.Node;
import Surveys.SurveyHolder;
import wat.tomasz.dsk.Survey;
import wat.tomasz.dsk.Packets.CustomPacket;
import wat.tomasz.dsk.Utils.Utils;

public class NodeSocket extends Socket implements Runnable {

	Survey survey;
	
	public NodeSocket(Survey survey, int port) {
		super(port);
		this.survey = survey;
	}

	@Override
	public void onReceiveData(String message, InetAddress receiver, int port) {
		String [] split = message.split(" ");
		
		System.out.println("Odebrano " + message);
		
		//NODE PING------------------------------------------------------------------
		if(split.length == 1) {
			if(split[0].equals("NODE_PING")) {
				this.sendData(new CustomPacket("NODE_PONG"), receiver, port);
			}
		}
		//----------------------------------------------------------------------------
		
		//NODE JOIN_REQUEST------------------------------------------------------------
		if(split.length == 2) {
			if(split[0].equals("NODE_JOIN_REQUEST")) {
				String myKey = Utils.getPublicKeyString(survey.getConfigManager().getPublicKey());
				
				PublicKey senderKey = Utils.getPublicKeyFromString(split[1]);
				if(survey.getNodesManager().nodeExists(senderKey))
					return;
				
				int recid = survey.getNodesManager().addNode(new Node(receiver, port, senderKey ), true );		
				this.sendData(new CustomPacket("NODE_JOIN_ACCEPT " + recid + " " + myKey), receiver, port);
				this.broadcastNode(survey.getNodesManager().getNode(recid));
			}
		}	
		//-----------------------------------------------------------------------------
		
		//NODE JOIN_REQUEST------------------------------------------------------------
		if(split.length == 7) {
			if(split[0].equals("NODE_JOINED")) {
				int target = Utils.getInt(split[1]);
				int id = Utils.getInt(split[2]);
				int idnew = Utils.getInt(split[3]);
				String address = split[4];
				int newport = Utils.getInt(split[5]);
				String pubkey = split[6];
				
				if(target != survey.getConfigManager().getSelfId())
					return;
				
				PublicKey key = Utils.getPublicKeyFromString(pubkey);
				
				if(survey.getNodesManager().nodeExists(key))
					return;
				
				if(!survey.getNodesManager().nodeExists(idnew)) {
					survey.getNodesManager().setNode(idnew, new Node(Utils.getAddress(address), newport, key), true );
					
					System.out.println("Do��czy� nowy w�ze� id=" + id);
				}
			}
		}	
		//-----------------------------------------------------------------------------
		
		//NODE USERS NUM --------------------------------------------------------------
		if(split.length == 3) {
			if(split[0].equals("NODE_USERS_NUM")) {
				int target = Utils.getInt(split[1]);
				int id = Utils.getInt(split[2]);
				
				if(target != survey.getConfigManager().getSelfId())
					return;
				
				this.sendData(new CustomPacket("NODE_USERS_NUM_RESPONSE " + id + " " + target +
						" " + survey.getNodesManager().getNodesSize()) 
						, receiver, port);
			}
		}
		//-----------------------------------------------------------------------------
		
		//NODE USERS NUM RESPONSE------------------------------------------------------
		if(split.length == 4) {
			if(split[0].equals("NODE_USERS_NUM_RESPONSE")) {
				int target = Utils.getInt(split[1]);
				int id = Utils.getInt(split[2]);
				int num = Utils.getInt(split[3]);
				
				if(target != survey.getConfigManager().getSelfId())
					return;
				
				if(survey.getNodesManager().getNodesSize() < num)
					this.sendData(new CustomPacket("NODE_USERS_REQUEST " + id + " " + target), receiver, port);
			}
		}
		//-----------------------------------------------------------------------------
		
		//NODE ANSWERS NUM ------------------------------------------------------------
		if(split.length == 3) {
			if(split[0].equals("NODE_ANSWERS_NUM")) {
				int target = Utils.getInt(split[1]);
				int id = Utils.getInt(split[2]);
				
				if(target != survey.getConfigManager().getSelfId())
					return;
				
				this.sendData(new CustomPacket("NODE_ANSWERS_NUM_RESPONSE " + id + " " + target +
						" " + survey.getNodesManager().getNodesSize()) 
						, receiver, port);
			}
		}
		//-----------------------------------------------------------------------------
		
		//NODE ANSWERS RESPONSE -------------------------------------------------------
		if(split.length == 4) {
			if(split[0].equals("NODE_ANSWERS_NUM_RESPONSE")) {
				int target = Utils.getInt(split[1]);
				int id = Utils.getInt(split[2]);
				int num = Utils.getInt(split[3]);
				
				if(target != survey.getConfigManager().getSelfId())
					return;
				
				if(survey.getAnswersManager().getAnwerersSize() < num)
					this.sendData(new CustomPacket("NODE_ANSWERS_REQUEST " + id + " " + target), receiver, port);
			}
		}
		//-----------------------------------------------------------------------------
		
		//NODE SURVEYS NUM ------------------------------------------------------------
		if(split.length == 3) {
			if(split[0].equals("NODE_SURVEYS_NUM")) {
				int target = Utils.getInt(split[1]);
				int id = Utils.getInt(split[2]);
				
				if(target != survey.getConfigManager().getSelfId())
					return;
				
				this.sendData(new CustomPacket("NODE_SURVEYS_NUM_RESPONSE " + id + " " + target +
						" " + survey.getNodesManager().getNodesSize()) 
						, receiver, port);
			}
		}
		//-----------------------------------------------------------------------------
		
		//NODE SURVEYS NUM RESPONSE ---------------------------------------------------
		if(split.length == 4) {
			if(split[0].equals("NODE_SURVEYS_NUM_RESPONSE")) {
				int target = Utils.getInt(split[1]);
				int id = Utils.getInt(split[2]);
				int num = Utils.getInt(split[3]);
				
				if(target != survey.getConfigManager().getSelfId())
					return;
				
				if(survey.getSurveysManager().getSurveysSize() < num)
					this.sendData(new CustomPacket("NODE_SURVEYS_REQUEST " + id + " " + target), receiver, port);
			}
		}
		//-----------------------------------------------------------------------------
		
		//NODE USERS REQUEST ---------------------------------------------------------
		if(split.length == 3) {
			if(split[0].equals("NODE_USERS_REQUEST")) {
				int target = Utils.getInt(split[1]);
				int id = Utils.getInt(split[2]);
				
				if(target != survey.getConfigManager().getSelfId())
					return;
				
				broadcastNodes(id);
			}
		}
		//-----------------------------------------------------------------------------
		
		//NODE USERS RESPONSE ---------------------------------------------------------
		if(split.length == 7) {
			if(split[0].equals("NODE_ANSWER_CREATED")) {
				int target = Utils.getInt(split[1]);
				int id = Utils.getInt(split[2]);
				int author = Utils.getInt(split[3]);
				int question = Utils.getInt(split[4]);
				int answer = Utils.getInt(split[5]);
				String signature = split[6];
				
				if(target != survey.getConfigManager().getSelfId())
					return;
				
				if(!survey.getAnswersManager().isValidAuthor(author, question))
					return;
				
				String signText =  "" + author + "" + question + "" + answer;
				System.out.println("Odebrano jako: " + signText);
				if(Utils.verifySignature(signText, signature, survey.getNodesManager().getNode(author).getKey())) {
					System.out.println("Pomy�lnie zweryfikowana odpowiedz");
					survey.getAnswersManager().addAnswer( new Answer(author, question, answer, signature), true);
				}
			}
		}
		//-----------------------------------------------------------------------------
		
		//NODE ANSWERS REQUEST --------------------------------------------------------
		if(split.length == 3) {
			if(split[0].equals("NODE_ANSWERS_REQUEST")) {
				int target = Utils.getInt(split[1]);
				int id = Utils.getInt(split[2]);
				
				if(target != survey.getConfigManager().getSelfId())
					return;
				
				broadcastAnswers();
			}
		}
		//-----------------------------------------------------------------------------
		
		//NODE ANSWERS RESPONSE -------------------------------------------------------
		if(split.length > 5) {
			if(split[0].equals("NODE_SURVEY_CREATED")) {
				int targetid = Utils.getInt(split[1]);
				int id = Utils.getInt(split[2]);
				int survid = Utils.getInt(split[3]);
				int author = Utils.getInt(split[4]);
				int type = Utils.getInt(split[5]);
				String signature = split[6];
				
				if(survey.getSurveysManager().surveyExists(survid))
					return;
				
				ArrayList<String> answers = new ArrayList<String>();
				String title = null;
				String [] ansSplit = message.split("/");
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
				
				String packet = title;
				for(String a : answers) packet += a;
					
				if(Utils.verifySignature(packet, signature, survey.getNodesManager().getNode(id).getKey())) {
					survey.getSurveysManager().setSurvey(survid, new SurveyHolder(author, type, title, answers, signature), true);
				}			
			}
		}
		//-----------------------------------------------------------------------------
		
		//NODE SURVEYS REQUEST --------------------------------------------------------
		if(split.length == 3) {
			if(split[0].equals("NODE_SURVEYS_REQUEST")) {
				int target = Utils.getInt(split[1]);
				int id = Utils.getInt(split[2]);
				
				if(target != survey.getConfigManager().getSelfId())
					return;
				
				broadcastSurveys();
			}
		}
		//-----------------------------------------------------------------------------
		
		//NODE SURVEYS RESPONSE --- ---------------------------------------------------
		
		//-----------------------------------------------------------------------------		
	}

	@Override
	public void onListenStart() {
		survey.getConfigManager().loadNodes(survey);
		survey.getConfigManager().loadSurveys(survey);
		survey.getConfigManager().loadAnswers(survey);
		updateSurveys();
		updateUsers();
		updateAnswers();
	}

	@Override
	public void onStopped() {
		
	}

	@Override
	public void onTimeout() {
		
	}

	@Override
	public void run() {
		while(isSocketActive()) {
			this.listen();
		}	
	}
	
	public void broadcast(String packetName, String packetData) {
		for(Node n : survey.getNodesManager().getNodes()) {
			if(n.getId() != survey.getConfigManager().getSelfId())
				this.sendData(new CustomPacket(packetName + " " + n.getId() + " " + survey.getConfigManager().getSelfId() + " " + packetData),
					n.getAddress(), n.getPort());
		}
	}
	
	
	public void broadcastSurveys() {
		for(SurveyHolder sur : survey.getSurveysManager().getSurveys()) {
			broadcastSurvey(sur);
		}
	}
	
	public void broadcastAnswers() {
		for(Answer ans : survey.getAnswersManager().getAnswers()) {
			broadcastAnswer(ans);
		}
	}
	
	public void broadcastNodes(int target) {
		for(Node n : survey.getNodesManager().getNodes()) {
			broadcastNode(n);
		}
	}
	
	public void broadcastSurvey(SurveyHolder sur) {
		broadcast("NODE_SURVEY_CREATED", sur.toPacket() );
	}
	
	public void broadcastAnswer(Answer ans) {
		broadcast("NODE_ANSWER_CREATED", ans.toPacket());
	}
	
	public void broadcastNode(Node n) {
		broadcast("NODE_JOINED", n.toPacket());
	}
	
	public void updateSurveys() {		
		broadcast("NODE_SURVEYS_NUM", "");
	}
	
	public void updateUsers() {
		broadcast("NODE_USERS_NUM", "");
	}
	
	public void updateAnswers() {
		broadcast("NODE_ANSWERS_NUM", "");
	}
	
}
