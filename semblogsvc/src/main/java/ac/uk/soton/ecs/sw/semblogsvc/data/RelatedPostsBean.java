package ac.uk.soton.ecs.sw.semblogsvc.data;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class RelatedPostsBean {
	
	private String relatedUri = null;
	private String title = null;
	
	public RelatedPostsBean(String uri, String title){
		this.relatedUri = uri;
		this.title = title;
	}

	public String getRelatedUri() {
		return relatedUri;
	}

	public void setRelatedUri(String relatedUri) {
		this.relatedUri = relatedUri;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}	

}
