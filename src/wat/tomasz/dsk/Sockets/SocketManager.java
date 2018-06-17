package wat.tomasz.dsk.Sockets;

import java.net.InetAddress;
import java.security.PublicKey;
import Nodes.Node;
import wat.tomasz.dsk.Survey;
import wat.tomasz.dsk.Utils.Utils;

public class SocketManager {
	private Socket 				socket;
	private Survey				survey;
	private Thread 				socketThread = null;
	
	public SocketManager(Survey survey) {
		this.survey = survey;
	}
	
	public void startNode(int port) {
		socket = new NodeSocket(survey, port);
		socketThread = new Thread( ((NodeSocket) socket) );
		socketThread.start();
	}
	
	public boolean requestPing(InetAddress address, int port, int listenAddress) {
		if(socket == null) {
			System.out.println("Zadano NODE_PING");
			socket = new RequestSocket("NODE_PING", "NODE_PONG", address, port, listenAddress, 3);	
			while(socket.isSocketActive()) {
				socket.listen();
			}
			if( ( (RequestSocket) socket).getResponses().size() > 0 ) {
				socket = null;
				return true;
			}		
		}
		socket = null;
		System.out.println("Nie uzyskano odpowiedzi");
		return false;
	}
	
	public void closeThread() {
		if(socket != null) {
			if(socket.isSocketActive()) {
				socket.closeSocket();
			}
		}
	}
	
	public int requestId(PublicKey key, InetAddress address, int port, int listenAddress) {
		if(socket == null) {
			System.out.println("Zadano NODE_REQUEST");
			
			socket = new RequestSocket("NODE_JOIN_REQUEST " + 
			Utils.getPublicKeyString(key), address, port, listenAddress, 3);	
			
			while(socket.isSocketActive()) {
				socket.listen();
			}
			for(String pocket : ( (RequestSocket) socket).getResponses()) {
				String [] split = pocket.split(" ");
				if(split.length == 3) {
					if(split[0].equals("NODE_JOIN_ACCEPT") ) {
						int id = Utils.getInt(split[1]);
						PublicKey keyfile = Utils.getPublicKeyFromString(split[2]);
						if( id > 0 ) {
							getSurvey().getNodesManager().setNode(0, new Node(address, port, keyfile), true);
							return id;
						}	
						System.out.println("Odebrano " + pocket);
					}
				}
			}
		}
		socket = null;
		System.out.println("Nie uzyskano odpowiedzi");
		return 0;
	}
	
	public Survey getSurvey() {
		return survey;
	}
	
	public NodeSocket getNodeSocket() {
		if(socket.isSocketActive()) {
			return (NodeSocket) socket;
		}
		return null;
	}
	
}
