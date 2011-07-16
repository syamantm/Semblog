package ac.uk.soton.ecs.sw.semblog.tstore.ir;

import java.util.List;

import ac.uk.soton.ecs.sw.semblog.tstore.common.ILink;

public interface IIndexSearcher {

	public List<ILink> searchTags(String[] searchTerms);
	
	public List<ILink> searchUrls(String searchUrl);
}
