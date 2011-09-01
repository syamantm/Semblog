package ac.uk.soton.ecs.sw.semblog.tstore.api;

import com.hp.hpl.jena.db.DBConnection;

/**
 * Abstracts the RDF store functionality such DB Connection etc.
 *  
 * @author syamantak
 *
 */
public interface IRdfStore {
	
	/**
	 * get Jena DBConnection object for the current connection
	 * @return DBConnection object
	 */
	public DBConnection getDBConnection();
	
	/**
	 * get database model name
	 * @return model name
	 */
	public String getModelName();
	
	/**
	 * Create the initial database model
	 */
	public boolean createInitModel();

}
