package Nodes;

import java.security.PublicKey;
import java.util.ArrayList;

public class NodesManager {
	ArrayList< Node > nodeList = new ArrayList < Node > ();
	
	public NodesManager() {
		
	}
	
	public int addNode(Node node) {
		nodeList.add(node);
		int id = nodeList.size();
		node.setId( id );
		return id;
	}
	
	public void setNode(int id, Node node) {
		nodeList.add(node);
		node.setId( id );
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
				return nodeList.get(id);
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
