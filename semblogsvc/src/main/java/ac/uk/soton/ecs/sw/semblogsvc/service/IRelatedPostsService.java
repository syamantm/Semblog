package ac.uk.soton.ecs.sw.semblogsvc.service;

import ac.uk.soton.ecs.sw.semblogsvc.data.PostInfoBean;

public interface IRelatedPostsService {
	
	public PostInfoBean[] getRelatedPosts(String uri) ;
	
	public PostInfoBean[] getRelatedPostsByRank(String uri, 
			String pageRankWeightage,
			String dateWeightage,
			String tagWeightage,
			String linkWeightage) ;
	
	public PostInfoBean getPostInfo(String strUri, Double score) ;

}
