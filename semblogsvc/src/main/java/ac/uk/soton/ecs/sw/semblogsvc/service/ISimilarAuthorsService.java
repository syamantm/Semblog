package ac.uk.soton.ecs.sw.semblogsvc.service;

import ac.uk.soton.ecs.sw.semblogsvc.data.PostInfoBean;

public interface ISimilarAuthorsService {
	
	
	public PostInfoBean[] getSimilarPostsByAuthors(String uri) ;
	
	public PostInfoBean[] getSimilarPostsByRankAndAuthors(String uri, 
			String pageRankWeight,
			String dateWeight,
			String tagWeight,
			String linkWeight) ;

}
