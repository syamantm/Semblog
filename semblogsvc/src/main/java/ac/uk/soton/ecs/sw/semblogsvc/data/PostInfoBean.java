package ac.uk.soton.ecs.sw.semblogsvc.data;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class PostInfoBean {
	
	private String relatedUri = null;
	private String title = null;
	private String score = null;
	
	
	public PostInfoBean(String uri, String title){
		this.relatedUri = uri;
		this.title = title;
	}

	/**
	 * @return the relatedUri
	 */
	public String getRelatedUri() {
		return relatedUri;
	}

	/**
	 * @param relatedUri the relatedUri to set
	 */
	public void setRelatedUri(String relatedUri) {
		this.relatedUri = relatedUri;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the score
	 */
	public String getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(String score) {
		this.score = score;
	}

}
