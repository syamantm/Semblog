package ac.uk.soton.ecs.sw.semblog.tstore.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GraphDataNode {
	
	private UUID nodeId;
	private List<UUID> connectedNodes = new ArrayList<UUID>();
	
	public GraphDataNode(UUID id){
		this.nodeId = id;
	}
	
	public void addConnectedNode(UUID id){
		connectedNodes.add(id);
	}

	public UUID getNodeId() {
		return nodeId;
	}	

	public List<UUID> getConnectedNodes() {
		return connectedNodes;
	}	
}
