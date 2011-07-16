package ac.uk.soton.ecs.sw.semblog.tstore.api;

/**
 * Set of APIs to persist RDF data that a 
 * tsore needs to implement
 * @author syamantak
 *
 */
public interface IRdfPersister {
	
	/**
	 * Persist RDF data from a url into model/database
	 * 
	 * @param url url of the rdf file
	 * @param rdfStore IRdfStore object
	 * @return true if successful, false otherwise 
	 */
	public boolean persistRdf(String url, IRdfStore rdfStore);

}
