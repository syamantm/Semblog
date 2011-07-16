package ac.uk.soton.ecs.sw.semblog.tstore.common.impl;

import ac.uk.soton.ecs.sw.semblog.tstore.common.ITerm;

public class TagTerm implements ITerm{
	
	private String termValue;
	
	public TagTerm(String term){
		this.termValue = term;
	}

	public String getTermValue() {		
		return this.termValue;
	}
	
	public void setTermValue(String term){
		this.termValue = term;
	}

}
