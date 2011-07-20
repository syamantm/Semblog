package ac.uk.soton.ecs.sw.semblog.tstore.ir;

import java.util.List;

import ac.uk.soton.ecs.sw.semblog.tstore.common.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.common.ITerm;

public interface IIndexSearcher {

	public List<ILink> searchTags(String searchTerm);
	
	public List<ITerm> searchUrls(String searchUrl);
}
