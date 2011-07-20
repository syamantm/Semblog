package ac.uk.soton.ecs.sw.semblogsvc.service;

import ac.uk.soton.ecs.sw.semblogsvc.data.PostInfoBean;

public interface ISimilarPostsService {
	
	public PostInfoBean[] getSimilarPosts(String uri) ;

}
