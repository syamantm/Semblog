package ac.uk.soton.ecs.sw.semblog.tstore.ranking.factor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.pagerank.IPageRankRetriever;
import ac.uk.soton.ecs.sw.semblog.tstore.pagerank.PageRankOutputRetriever;
import ac.uk.soton.ecs.sw.semblog.tstore.ranking.AbstractScoreFactor;

@Component
public class PageRankFactor extends AbstractScoreFactor {
	private static final Logger logger = Logger
			.getLogger(PageRankFactor.class);

	@Autowired
	private IPageRankRetriever pageRankRetriever;

	public PageRankFactor(double weight) {
		super(weight);
	}

	public PageRankFactor() {
		super();
	}

	@Override
	public double calculateScore(ILink blog, ILink webpage) {
		if (pageRankRetriever == null) {
			logger.error("rdfRetriever is null!!");
			pageRankRetriever = new PageRankOutputRetriever();
		}

		double pageRank = pageRankRetriever.getPageRank(blog);
		logger.info("pageRank = " + pageRank);
		if (pageRank != Double.NaN) {			
			return pageRank * weight;
		} else {
			return 0.01;
		}
	}

}
