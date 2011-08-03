package ac.uk.soton.ecs.sw.semblog.tstore.common;

import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStore;


public abstract class AbstractRdfPersister{
	
	public abstract  boolean persistRdf(String url, IRdfStore rdfStore) ;

}
