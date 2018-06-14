package wat.tomasz.dsk.Packets;

public abstract class Packet {
	String packetString;
	
	public Packet(String data) {
		packetString = data;
	}
	
	public byte [] getData() {
		System.out.println("Wysy³anie" + packetString);
		return packetString.getBytes();
	}
}
