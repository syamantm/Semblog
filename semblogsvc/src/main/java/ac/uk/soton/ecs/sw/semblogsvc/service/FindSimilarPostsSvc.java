package ac.uk.soton.ecs.sw.semblogsvc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.api.ITerm;
import ac.uk.soton.ecs.sw.semblog.tstore.common.PageLink;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IClusterSearcher;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IIndexSearcher;
import ac.uk.soton.ecs.sw.semblog.tstore.ranking.AbstractBlogPost;
import ac.uk.soton.ecs.sw.semblog.tstore.ranking.SemBlogPost;
import ac.uk.soton.ecs.sw.semblogsvc.data.PostInfoBean;

@Service
public class FindSimilarPostsSvc implements ISimilarPostsService {

	private static final Logger logger = Logger
			.getLogger(FindSimilarPostsSvc.class);

	@Autowired
	private IRelatedPostsService relatedPostsSvc;
	
	@Autowired
	private IClusterSearcher clusterSearcher;

	@Override
	public PostInfoBean[] getSimilarPosts(String webpageUri) {

		PostInfoBean[] similarPosts = null;
		try {

			logger.info("relatedPostsSvc.getRelatedPosts");			
			PostInfoBean[] relatedPosts = relatedPostsSvc.getRelatedPosts(webpageUri);
			List<AbstractBlogPost> blogPostList = new ArrayList<AbstractBlogPost>();
			// We are interested in only unique links, hence set ADT			
			Set<ILink> linkSet = new HashSet<ILink>();

			for (PostInfoBean post : relatedPosts) {
				logger.info("clusterSearcher.retrieveSimilarPages : "
						+ post.getRelatedUri());
				Set<ILink> links = clusterSearcher.retrieveSimilarPages(post.getRelatedUri());				
				logger.info("Set<ILink> links size : "
						+ links.size());
				linkSet.addAll(links);
			}			
			
			if (linkSet.size() > 0) {
				Set<ILink> relatedSet = convertToILink(relatedPosts);
				// remove links that are already present in relatedPosts
				linkSet.removeAll(relatedSet);
				
				logger.info("found similar posts by tag : " + linkSet.size());				
				
				Iterator<ILink> linkItr = linkSet.iterator();
				List<PostInfoBean> tempList = new ArrayList<PostInfoBean>();
				
				while (linkItr.hasNext()) {
					String url = linkItr.next().getUrlValue();
					/*PostInfoBean bean = relatedPostsSvc.getPostInfo(url);
					tempList.add(bean);*/
					AbstractBlogPost post = new SemBlogPost(url, webpageUri);
					blogPostList.add(post);
					
				}
				Collections.sort(blogPostList);
				if(blogPostList.size() > 10 ){
					List<AbstractBlogPost> subList = blogPostList.subList(0, 9);
					for(AbstractBlogPost post : subList){
						PostInfoBean bean = relatedPostsSvc.getPostInfo(post.getBlogUrl().getUrlValue(), post.getScore());
						tempList.add(bean);
					}
				}else{
					for(AbstractBlogPost post : blogPostList){
						PostInfoBean bean = relatedPostsSvc.getPostInfo(post.getBlogUrl().getUrlValue(), post.getScore());
						tempList.add(bean);
					}
				}
				similarPosts = new PostInfoBean[tempList.size()];
				tempList.toArray(similarPosts);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return similarPosts;
	}
	
	private Set<ILink> convertToILink(PostInfoBean[] postInfoArr){
		Set<ILink> links = new HashSet<ILink>();
		for(PostInfoBean bean : postInfoArr){
			ILink link = new PageLink(bean.getRelatedUri());
			links.add(link);
		}
		return links;
	}

}
