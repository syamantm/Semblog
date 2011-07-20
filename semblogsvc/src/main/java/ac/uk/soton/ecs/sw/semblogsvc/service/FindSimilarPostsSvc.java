package ac.uk.soton.ecs.sw.semblogsvc.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ac.uk.soton.ecs.sw.semblog.tstore.common.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.common.ITerm;
import ac.uk.soton.ecs.sw.semblog.tstore.common.impl.BlogLink;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IIndexSearcher;
import ac.uk.soton.ecs.sw.semblogsvc.data.PostInfoBean;

@Service
public class FindSimilarPostsSvc implements ISimilarPostsService {

	private static final Logger logger = Logger
			.getLogger(FindSimilarPostsSvc.class);

	@Autowired
	private IRelatedPostsService relatedPostsSvc;

	@Autowired
	private IIndexSearcher indexSearcher;

	@Override
	public PostInfoBean[] getSimilarPosts(String uri) {

		PostInfoBean[] similarPosts = null;
		try {

			logger.info("relatedPostsSvc.getRelatedPosts");
			System.out.println("relatedPostsSvc.getRelatedPost");
			PostInfoBean[] relatedPosts = relatedPostsSvc.getRelatedPosts(uri);

			// We are interested in only unique tags and links, hence set ADT
			Set<ITerm> termSet = new HashSet<ITerm>();
			Set<ILink> linkSet = new HashSet<ILink>();

			for (PostInfoBean post : relatedPosts) {
				logger.info("indexSearcher.searchUrls : "
						+ post.getRelatedUri());
				System.out.println("indexSearcher.searchUrls : "
						+ post.getRelatedUri());
				List<ITerm> tags = indexSearcher.searchUrls(post
						.getRelatedUri());
				termSet.addAll(tags);
			}
			
			Iterator<ITerm> itr = termSet.iterator();
			while (itr.hasNext()) {
				String term = itr.next().getTermValue();
				List<ILink> links = indexSearcher.searchTags(term);
				linkSet.addAll(links);				
			}

			if (linkSet.size() > 0) {
				Set<ILink> relatedSet = convertToILink(relatedPosts);
				// remove links that are already present in relatedPosts
				linkSet.removeAll(relatedSet);
				
				logger.info("found similar posts by tag ");
				System.out.println("found similar posts by tag ");
				
				Iterator<ILink> linkItr = linkSet.iterator();
				List<PostInfoBean> tempList = new ArrayList<PostInfoBean>();
				
				while (linkItr.hasNext()) {
					String url = linkItr.next().getUrlValue();
					PostInfoBean bean = relatedPostsSvc.getPostInfo(url);
					tempList.add(bean);
					
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
			ILink link = new BlogLink(bean.getRelatedUri());
			links.add(link);
		}
		return links;
	}

}
