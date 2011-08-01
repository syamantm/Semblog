package ac.uk.soton.ecs.sw.semblog.tstore.common.impl;

import ac.uk.soton.ecs.sw.semblog.tstore.common.ILink;

public class PageLink implements ILink{
	
	private String urlValue;
	
	public PageLink(String url){
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
		return this.urlValue.compareTo(o.getUrlValue());			
	}
	
	@Override
	public boolean equals(Object obj) {
		return compareTo((ILink)obj) == 0;
	}
	
	@Override
	public int hashCode() {		
		return urlValue.hashCode();
	}
	
	@Override
	public String toString() {
		return this.urlValue;
	}

}
