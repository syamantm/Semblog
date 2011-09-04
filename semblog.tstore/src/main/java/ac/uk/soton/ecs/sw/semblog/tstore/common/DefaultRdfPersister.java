package ac.uk.soton.ecs.sw.semblog.tstore.common;

import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStore;

/**
 * Default Rdf persister. Should be the last one in the 
 * responsibility chain
 * 
 * @author syamantak
 *
 */
@Component
public class DefaultRdfPersister extends AbstractRdfPersister {

	
	@Override
	public boolean persistRdf(String url, IRdfStore rdfStore) {
		// TODO Auto-generated method stub
		return true;
	}

}
