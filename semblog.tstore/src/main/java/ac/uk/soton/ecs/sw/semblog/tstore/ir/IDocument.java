package ac.uk.soton.ecs.sw.semblog.tstore.ir;

import java.util.List;

import ac.uk.soton.ecs.sw.semblog.tstore.common.ITerm;

public interface IDocument {

	public String getDocId();

	public String getTitle();

	public List<ITerm> getTermList();

}
