package wat.tomasz.dsk.Sockets;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import wat.tomasz.dsk.Packets.Packet;

public abstract class Socket {
	
	protected int _port;
	protected InetAddress _ip;
	protected DatagramSocket _socket;
	
	public abstract void onReceiveData(String message, InetAddress reciver, int port);
	public abstract void onListenStart();
	public abstract void onStopped();
	public abstract void onTimeout();
	
	public Socket(int port) {
		_port = port;
		try {
			_socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void closeSocket() {
		
		if(_socket != null) {
			if(!_socket.isClosed()) {
				_socket.close();
				System.out.println("Gniazdo zamkniête.");
			}
		}
		_socket = null;
		onStopped();
	}
	
	public void listen() {
		if(_socket == null)
			return;
		
		onListenStart();
		
		byte [] reciveData = new byte[1024];
		
		System.out.println("Rozpoczêcie nas³uchiwania.");
		while(_socket != null && !_socket.isClosed()) {
			DatagramPacket receivePacket = new DatagramPacket(reciveData, reciveData.length);
			try {
				//System.out.println("Waiting for packet.");
				_socket.receive(receivePacket);
			} 
			catch (SocketTimeoutException e) {
				onTimeout();
				continue;
			}
			catch (IOException e) {
				System.out.println("Nas³uchiwanie przerwane.");
				continue;
			}
			
			//System.out.println("End of waiting.");
			if(receivePacket != null) {
				if(receivePacket.getData() != null && receivePacket.getLength() > 0) {
					//System.out.println("Packet received. Size " + receivePacket.getLength() + " bytes");
		
					onReceiveData( dataToString(receivePacket.getData(), receivePacket.getLength()), 
							receivePacket.getAddress(), receivePacket.getPort());
				}
			}
		}
	}
	
	public void sendData(Packet packet, InetAddress reciver, int port) {
		if(_socket != null) {
			try {
				byte[] data = packet.getData();
				_socket.send(new DatagramPacket(data, data.length, reciver, port));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setTimeout(float seconds) {
		if(_socket == null)
			return;
		
		try {
			_socket.setSoTimeout(
					(int) (seconds * 1000)
					);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isSocketActive() {
		if(_socket != null)
			return true;
		
		return false;
	}
	
	private String dataToString(byte[] data, int length) {
		byte[] newData = new byte [length];
		for(int i = 0; i < length; i++) {
			newData[i] = data[i];
		}
		return new String(newData);
	}
	
}
