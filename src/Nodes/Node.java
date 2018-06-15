package Nodes;

import java.net.InetAddress;
import java.security.PublicKey;

import wat.tomasz.dsk.Utils.Utils;

public class Node {
	private int id;
	private InetAddress address;
	private int port;
	private PublicKey key;
	
	public Node(InetAddress address, int port, PublicKey key) {
		this.setAddress(address);
		this.setPort(port);
		this.setKey(key);
	}

	public String toPacket() {
		String packet = id + " ";
		packet += address.getHostAddress() + " ";
		packet += port + " ";
		packet += Utils.getPublicKeyString(key);
		
		return packet;	
	}
	
	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public PublicKey getKey() {
		return key;
	}

	public void setKey(PublicKey key) {
		this.key = key;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
