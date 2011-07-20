package ac.uk.soton.ecs.sw.semblog.tstore.common.impl;

import ac.uk.soton.ecs.sw.semblog.tstore.common.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.common.ITerm;

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

	@Override
	public int compareTo(ILink o) {
		if(this.urlValue.equals(o.getUrlValue()))
			return 0;
		return 1;
	}
	
	@Override
	public boolean equals(Object obj) {
		return compareTo((ILink)obj) == 0;
	}
	
	@Override
	public int hashCode() {		
		return urlValue.hashCode();
	}

}
