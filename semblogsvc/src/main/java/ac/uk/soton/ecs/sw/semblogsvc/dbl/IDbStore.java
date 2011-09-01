package ac.uk.soton.ecs.sw.semblogsvc.dbl;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.ModelRDB;

public interface IDbStore {
	
	public DBConnection getDBConnection();	
	
	public ModelRDB getModelRDB();
	
	public String getModelName();

}
