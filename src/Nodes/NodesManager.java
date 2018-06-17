package Nodes;

import java.nio.file.StandardOpenOption;
import java.security.PublicKey;
import java.util.ArrayList;

import wat.tomasz.dsk.SurveyCheckController;
import wat.tomasz.dsk.SurveyController;
import wat.tomasz.dsk.Files.FileManager;

public class NodesManager {
	ArrayList< Node > nodeList = new ArrayList < Node > ();
	
	public NodesManager() {
		
	}
	
	public int addNode(Node node, boolean save) {
		nodeList.add(node);
		int id = nodeList.size();
		node.setId( id );
		SurveyController.getSurvey().getController().updateMainWindow();
		
		if(save)
			FileManager.writeText("nodes.txt", node.toPacket() + String.format("%n"), StandardOpenOption.APPEND);
		
		return id;
	}
	
	public void setNode(int id, Node node, boolean save) {
		nodeList.add(node);
		node.setId( id );
		SurveyController.getSurvey().getController().updateMainWindow();
		
		if(save)
			FileManager.writeText("nodes.txt", node.toPacket() + String.format("%n"), StandardOpenOption.APPEND);
	}
	
	public void clear() {
		nodeList.clear();
	}
	
	public boolean nodeExists(int id) {
		for (Node n : nodeList) {
			if(n.getId() == id)
				return true;
		}
		return false;
	}
	
	public boolean nodeExists(PublicKey key) {
		for (Node n : nodeList) {
			if(n.getKey().equals(key))
				return true;
		}
		return false;
	}
	
	public Node getNode(int id) {
		for(Node n : nodeList) {
			if(n.getId() == id)
				return n;
		}
		return null;
	}
	
	public int getNodesSize() {
		return nodeList.size();
	}
	
	public ArrayList<Node> getNodes() {
		return nodeList;
	}
}
