package ac.uk.soton.ecs.sw.semblog.tstore.api;

import java.util.List;

import ac.uk.soton.ecs.sw.semblog.tstore.common.GraphDataNode;


public interface IRdfRetriever {
	
	public java.sql.Date getCreationDate(ILink blog);
	
	public String getPredicate(ILink blog, ILink webpage);
	
	public List<GraphDataNode> getGraphAsList();

	public boolean isResourceExists(ILink link);
	
	public String getNodeUUID(ILink link);
}
