package ac.uk.soton.ecs.sw.semblog.tstore.pagerank;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;

public interface IPageRankRetriever {
	public double getPageRank(ILink link);

}
