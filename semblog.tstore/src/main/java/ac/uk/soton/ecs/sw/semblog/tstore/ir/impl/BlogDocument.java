package ac.uk.soton.ecs.sw.semblog.tstore.ir.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ITerm;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IDocument;

@Component
public class BlogDocument implements IDocument{
	
	private String docId;
	private String docTitle;
	private List<ITerm> termList;
	
	/**
	 * For spring only
	 */
	public BlogDocument(){
		
	}
	
	public BlogDocument(String id, String title, List<ITerm> termList){
		this.docId = id;
		this.docTitle = title;
		this.termList = termList;
	}

	public String getDocId() {		
		return this.docId;
	}

	public String getTitle() {		
		return this.docTitle;
	}

	public List<ITerm> getTermList() {
		return this.termList;
	}

}
