package wat.tomasz.dsk.Sockets;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;

import wat.tomasz.dsk.Packets.CustomPacket;

public class RequestSocket extends Socket {

	String requestMessage;
	String responseMessage;
	long requestTime;
	
	int receiverPort;
	InetAddress receiverAddress;
	
	ArrayList<String> packets = new ArrayList<String>();
	
	RequestSocket(String requestMessage, InetAddress receiver, int port, int listenport, int timeout) {
		super(listenport);
		this.receiverPort = port;
		this.receiverAddress = receiver;
		
		this.requestTime = Calendar.getInstance().getTimeInMillis() + (timeout * 1000);
		this.requestMessage = requestMessage;
		this.setTimeout(1);
	}
	
	RequestSocket(String requestMessage, String responseMessage, InetAddress receiver, int port, int listenport, int timeout) {
		super(listenport);
		this.receiverPort = port;
		this.receiverAddress = receiver;
		
		this.requestTime = Calendar.getInstance().getTimeInMillis() + (timeout * 1000);
		this.requestMessage = requestMessage;
		this.responseMessage = responseMessage;
		this.setTimeout(1);
	}
	
	@Override
	public void onReceiveData(String message, InetAddress reciver, int port) {
		long now = Calendar.getInstance().getTimeInMillis();	
		//System.out.println("Pozostalo " + timeout);
		if(responseMessage != null && message.equals(responseMessage)) {
			packets.add(message);
			this.closeSocket();
		}
		else {
			packets.add(message);
		}
		
		if(requestTime <= now) {
			this.closeSocket();
		}
	}

	@Override
	public void onListenStart() {
		this.sendData(new CustomPacket(requestMessage), receiverAddress, receiverPort);;
	}

	@Override
	public void onStopped() {
		
	}

	@Override
	public void onTimeout() {
		long now = Calendar.getInstance().getTimeInMillis();	
		//System.out.println("Pozostalo " + timeout);
		if(requestTime <= now) {
			this.closeSocket();
		}
	}
	
	public ArrayList<String> getResponses() {
		return packets;
	}
	

}
