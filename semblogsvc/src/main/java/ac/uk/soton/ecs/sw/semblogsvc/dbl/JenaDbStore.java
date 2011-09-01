package ac.uk.soton.ecs.sw.semblogsvc.dbl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStore;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.ModelRDB;

@Component
public class JenaDbStore implements IDbStore {

	private static final Logger logger = Logger.getLogger(JenaDbStore.class);

	@Autowired
	private IRdfStore rdfStore;

	@Override
	public DBConnection getDBConnection() {
		return rdfStore.getDBConnection();	
	}
	

	@Override
	public ModelRDB getModelRDB() {
		DBConnection connection = getDBConnection();
		ModelRDB model = ModelRDB.open(connection, getModelName());
		return model;
	}

	@Override
	public String getModelName() {
		return rdfStore.getModelName();
	}

}
