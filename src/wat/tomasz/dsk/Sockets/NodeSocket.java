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
				
				int recid = survey.getNodesManager().addNode(new Node(receiver, port, senderKey ) );		
				this.sendData(new CustomPacket("NODE_JOIN_ACCEPT " + recid + " " + myKey), receiver, port);
				this.broadcastNewNode(survey.getNodesManager().getNode(recid));
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
				
				if(!survey.getNodesManager().nodeExists(idnew)) {
					survey.getNodesManager().setNode(idnew, new Node(Utils.getAddress(address), newport, 
							Utils.getPublicKeyFromString(pubkey) ) );
					
					System.out.println("Do³¹czy³ nowy wêze³ id=" + id);
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
				
				this.sendData(new CustomPacket("NODE_USERS_NUM_RESPONSE " + target + " " + id +
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
				
				if(survey.getNodesManager().getNodesSize() <= num)
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
				
				this.sendData(new CustomPacket("NODE_ANSWERS_NUM_RESPONSE " + target + " " + id +
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
				
				if(survey.getAnswersManager().getAnwerersSize() <= num)
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
				
				this.sendData(new CustomPacket("NODE_SURVEY_NUM_RESPONSE " + target + " " + id +
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
				
				
				if(survey.getSurveysManager().getSurveysSize() <= num)
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
		
		//-----------------------------------------------------------------------------
		
		//NODE ANSWERS REQUEST --------------------------------------------------------
		if(split.length == 3) {
			if(split[0].equals("NODE_ANSWERS_REQUEST")) {
				int target = Utils.getInt(split[1]);
				int id = Utils.getInt(split[2]);
				
				if(target != survey.getConfigManager().getSelfId())
					return;
				
				broadcastAnswers(id);
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
				
				ArrayList<String> answers = new ArrayList<String>();
				String title = null;
				String [] ansSplit = message.split("/");
				if(ansSplit[1].equals("START_PACK") 
						&& ansSplit[ansSplit.length - 1].equals("END_PACK")) {
					for(int i = 1; i < ansSplit.length - 1; i++) {
						if(i == 0) {
							title = ansSplit[1];
						} else {
							answers.add(ansSplit[i]);
						}
					}
				}	
				
				String packet = title;
				for(String a : answers) packet += a;
				
				if(survey.getNodesManager().getNode(id) == null)
					System.out.println("A");
				
				if(survey.getNodesManager().getNode(id).getKey() == null)
					System.out.println("B");
					
				if(Utils.verifySignature(packet, signature, survey.getNodesManager().getNode(id).getKey())) {
					System.out.print("Pozytywnie zweryfikowano");
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
			this.sendData(new CustomPacket(packetName + " " + n.getId() + " " + survey.getConfigManager().getSelfId() + " " + packetData),
					n.getAddress(), n.getPort());
		}
	}
	
	public void broadcastNewNode(Node n) {				
			broadcast("NODE JOINED", n.toPacket());
	}
	
	public void broadcastSurveys() {
		for(SurveyHolder sur : survey.getSurveysManager().getSurveys()) {
			broadcastSurvey(sur);
		}
	}
	
	public void broadcastSurvey(SurveyHolder sur) {
		System.out.println("NODE_SURVEY_CREATED" + sur.toPacket());
		broadcast( "NODE_SURVEY_CREATED", sur.toPacket() );
	}
	
	public void broadcastAnswers(int target) {
		if(survey.getNodesManager().nodeExists(target)) {
			Node n = survey.getNodesManager().getNode(target);
			for(Answer ans : survey.getAnswersManager().getAnswers()) {
				String info = ans.toPacket();
				this.sendData(new CustomPacket("NODE_ANSWERS_RESPONSE " + target + " " + info), n.getAddress(), n.getPort());
			}
		}
	}
	
	public void broadcastNodes(int target) {
		if(survey.getNodesManager().nodeExists(target)) {
			Node n = survey.getNodesManager().getNode(target);
			for(Node nod : survey.getNodesManager().getNodes()) {
				String info = nod.toPacket();
				this.sendData(new CustomPacket("NODE_USERS_RESPONSE " + target + " " + info), n.getAddress(), n.getPort());
			}
		}
	}
	
	public void updateSurveys() {
		for (Node n : survey.getNodesManager().getNodes()) {
			this.sendData(new CustomPacket("NODE_SURVEYS_NUM " + n.getId() + " " + survey.getConfigManager().getSelfId()), 
					n.getAddress(), n.getPort());
		}
	}
	
	public void updateUsers() {
		for (Node n : survey.getNodesManager().getNodes()) {
			this.sendData(new CustomPacket("NODE_USERS_NUM " + n.getId() + " " + survey.getConfigManager().getSelfId()), 
					n.getAddress(), n.getPort());
		}
	}
	
	public void updateAnswers() {
		for (Node n : survey.getNodesManager().getNodes()) {
			this.sendData(new CustomPacket("NODE_ANSWERS_NUM " + n.getId() + " " + survey.getConfigManager().getSelfId()), 
					n.getAddress(), n.getPort());
		}
	}
	
}
