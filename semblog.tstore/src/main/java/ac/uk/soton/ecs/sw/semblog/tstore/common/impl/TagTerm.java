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

	@Override
	public int compareTo(ITerm o) {
		if(this.termValue.equals(o.getTermValue()))
			return 0;
		return 1;
	}
	
	@Override
	public boolean equals(Object obj) {
		return compareTo((ITerm)obj) == 0;
	}
	
	@Override
	public int hashCode() {		
		return termValue.hashCode();
	}

}
