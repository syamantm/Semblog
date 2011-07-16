package ac.uk.soton.ecs.sw.semblogsvc.service;

import ac.uk.soton.ecs.sw.semblogsvc.data.RelatedPostsBean;

public interface IRelatedPostsService {
	
	public RelatedPostsBean[] getRelatedPosts(String uri) ;

}
