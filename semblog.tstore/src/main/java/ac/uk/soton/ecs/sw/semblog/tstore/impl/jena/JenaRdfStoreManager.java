package ac.uk.soton.ecs.sw.semblog.tstore.impl.jena;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfPersister;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStore;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStoreManager;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IUrlScanner;


@Component("jenaRdfStoreManager")
public class JenaRdfStoreManager implements IRdfStoreManager{
	
	private static final Logger logger = Logger.getLogger(JenaRdfStoreManager.class);
	
	@Autowired
	IRdfStore rdfStore;
	
	@Autowired
	IRdfPersister persister;
	
	@Autowired
	IUrlScanner urlScanner;

	public boolean run() {
		boolean status = true;
		rdfStore.createInitModel();
		List<String> rdfUrls = urlScanner.getRdfUrlList();
		for(String url : rdfUrls){
			status = persister.persistRdf(url, rdfStore);
			if(!status){
				//  something went wrong, return with error status 
				logger.error(" something went wrong while persisting url :" + url);
				break;
			}
		}		
		return status;
	}
	
	

}
