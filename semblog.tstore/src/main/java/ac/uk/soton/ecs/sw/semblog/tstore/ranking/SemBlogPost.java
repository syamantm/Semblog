package ac.uk.soton.ecs.sw.semblog.tstore.ranking;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.common.ILink;

@Component
public class SemBlogPost extends AbstractBlogPost {
	

	private static final Logger logger = Logger
			.getLogger(SemBlogPost.class);
	
	public SemBlogPost() {
		super();		
	}
	
	public SemBlogPost(ILink blog, ILink webpage) {
		super(blog, webpage);		
	}
	
	public SemBlogPost(String blog, String webpage){
		super(blog, webpage);		
	}	

	@Override
	public double calculateScore() {		
		logger.info("calculateScore");
		return DefaultScoreCalculator.getInstance().calculateScore(
				this.getBlogUrl(), this.getWebpageUrl());
	}


}
