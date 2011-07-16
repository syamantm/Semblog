package ac.uk.soton.ecs.sw.semblog.tstore.common.impl;

import ac.uk.soton.ecs.sw.semblog.tstore.common.ILink;

public class BlogLink implements ILink{
	
	private String urlValue;
	
	public BlogLink(String url){
		this.urlValue = url;
	}

	public String getUrlValue() {		
		return this.urlValue;
	}
	
	public void setUrlValue(String url){
		this.urlValue = url;
	}

}
