package ac.uk.soton.ecs.sw.semblog.tstore.common;

import java.util.List;

public interface ILinkParser {
	
	public List<ILink> getReferencedLinks();	
	
	public boolean parseContent(String content);

}
