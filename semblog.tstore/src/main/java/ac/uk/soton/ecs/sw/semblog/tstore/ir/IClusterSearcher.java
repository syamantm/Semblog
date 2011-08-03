package ac.uk.soton.ecs.sw.semblog.tstore.ir;

import java.util.Set;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;

public interface IClusterSearcher {
	
	public boolean searchCentroid();
	
	/**
	 * Find the  centroid of the cluster in which this url belongs
	 * and then return the centroid as String (Centroid should also
	 * be a url if the clustering is applied correctly)
	 * 
	 * @param url 
	 * @return 
	 */
	public Set<ILink> retrieveSimilarPages(String url);
	
	public double getDistanceFromCenter(String url);

}
