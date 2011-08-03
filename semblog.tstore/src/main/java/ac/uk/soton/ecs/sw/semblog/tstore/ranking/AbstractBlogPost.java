package ac.uk.soton.ecs.sw.semblog.tstore.ranking;

import org.apache.log4j.Logger;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.common.PageLink;

public abstract class AbstractBlogPost implements Comparable<AbstractBlogPost>{
	
	private static final Logger logger = Logger
			.getLogger(AbstractBlogPost.class);
	
	private ILink blogUrl;
	
	private ILink webpageUrl; 
	
	private Double score = null;
	
	public AbstractBlogPost(){
		this.blogUrl = null;
		this.webpageUrl = null;
	}	
	
	public AbstractBlogPost(ILink blog, ILink webpage){
		this.blogUrl = blog;
		this.webpageUrl = webpage;
	}	
	
	public AbstractBlogPost(String blog, String webpage){
		this.blogUrl = new PageLink(blog);
		this.webpageUrl = new PageLink(webpage);
	}	
	
	public abstract double calculateScore();
	
	/**
	 * Reverse implementation of compareTo because we want posts with higher score
	 * to appear first in the list. i.e. this will sort post in the descending 
	 * order of score
	 */
	@Override
	public int compareTo(AbstractBlogPost blog) {		
		if(this.getScore() == blog.getScore()){
			return 0;
		}else if(this.getScore() > blog.getScore()){
			return -1;
		}else{
			return 1;
		}		
	}

	/**
	 * @return the score
	 */
	public Double getScore() {		
		if(score == null){
			logger.info("getScore()");
			score = calculateScore();
		}
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(Double score) {
		this.score = score;
	}

	/**
	 * @return the blogUrl
	 */
	public ILink getBlogUrl() {
		return blogUrl;
	}

	/**
	 * @param blogUrl the blogUrl to set
	 */
	public void setBlogUrl(ILink blogUrl) {
		this.blogUrl = blogUrl;
	}

	/**
	 * @return the webpageUrl
	 */
	public ILink getWebpageUrl() {
		return webpageUrl;
	}

	/**
	 * @param webpageUrl the webpageUrl to set
	 */
	public void setWebpageUrl(ILink webpageUrl) {
		this.webpageUrl = webpageUrl;
	}

}
