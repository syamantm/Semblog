package ac.uk.soton.ecs.sw.semblog.tstore.impl.jena;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStore;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;


@Component
public class JenaRdfStore implements IRdfStore{
	
	private static final Logger logger = Logger.getLogger(JenaRdfStore.class);
	
	private final static String RESOURCE_NAME="jenadb";
	private final static String MODEL_NAME="Semblog_Tstore";

	/**
	 * Get the Jena DBConnection object for current database
	 */
	public DBConnection getDBConnection() {
		DBConnection connection = null;
		ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_NAME);
		if(bundle != null){
			String dbUrl = bundle.getString("dburl");
			String user = bundle.getString("username");
			String pass = bundle.getString("password");
			String dbType = bundle.getString("dbtype");
			connection = new DBConnection(dbUrl, user, pass, dbType);
		}
		
		return connection;
	}

	/**
	 * Get the Jena model name
	 */
	public String getModelName() {		
		return MODEL_NAME;
	}

	/**
	 * Create the initial db model to work with Jena if it doesn't exist.
	 */
	public boolean createInitModel() {
		DBConnection connection = null;
		Model dbModel = null;
		ModelMaker dbMaker = null;
		boolean status = true;
		try{
			connection = getDBConnection();			
			dbMaker = ModelFactory.createModelRDBMaker(connection);
			String modelName = getModelName();
			// If the model already exist, don't overwrite it!
			if(!dbMaker.hasModel(modelName)){
				logger.info("Creating initial with name : " + modelName);
				dbModel = dbMaker.createModel(modelName);
				dbModel.close();
			}else{
				logger.info("Model with name : " + modelName + " already exists. Skeaping model creating");
			}
			dbMaker.close();
			connection.close();
		}catch(Exception ex){
			ex.printStackTrace();
			status = false;
		}
		return status;
	}

}
