package wat.tomasz.dsk.Sockets;

import java.net.InetAddress;
import java.security.PublicKey;

import Nodes.Node;
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
		if(split.length == 1) {
			if(split[0].equals("NODE_PING")) {
				this.sendData(new CustomPacket("NODE_PONG"), receiver, port);
			}
		}
		
		if(split.length == 2) {
			if(split[0].equals("NODE_JOIN_REQUEST")) {
				System.out.println("Odebrano " + message);
				String myKey = Utils.getPublicKeyString(survey.getConfigManager().getPublicKey());
				
				PublicKey senderKey = Utils.getPublicKeyFromString(split[1]);
				
				if(survey.getNodesManager().nodeExists(senderKey))
					return;
				
				int recid = survey.getNodesManager().addNode(new Node(receiver, port, senderKey ) );
				this.sendData(new CustomPacket("NODE_JOIN_ACCEPT " + recid + " " + myKey), receiver, port);
			}
		}	
	}

	@Override
	public void onListenStart() {
		
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

}
