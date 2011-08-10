package ac.uk.soton.ecs.sw.semblogsvc.service;

import ac.uk.soton.ecs.sw.semblogsvc.data.PostInfoBean;

public interface ISimilarPostsService {
	
	public PostInfoBean[] getSimilarPosts(String uri) ;
	
	public PostInfoBean[] getSimilarPostsByRank(String uri, 
			String pageRankWeightage,
			String dateWeightage,
			String tagWeightage,
			String linkWeightage) ;

}
