package ac.uk.soton.ecs.sw.semblog.tstore.impl.jena;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.common.AbstractRdfStoreManager;
import ac.uk.soton.ecs.sw.semblog.tstore.common.AppContextManager;
import ac.uk.soton.ecs.sw.semblog.tstore.common.DefaultPersisterController;

@Component("jenaRdfStoreManager")
public class JenaRdfStoreManager extends AbstractRdfStoreManager {

	private static final Logger logger = Logger
			.getLogger(JenaRdfStoreManager.class);
	
	@Override
	public void setRdfStore() {
		
		rdfStore = (JenaRdfStore)AppContextManager.getAppContext().
					getBean("jenaRdfStore"); 

	}

	@Override
	public void setRdfPersisterController() {
		persisterController = (DefaultPersisterController)AppContextManager.
				getAppContext().getBean("defaultPersisterController"); 

	}

	@Override
	public void setUrlScanner() {
		urlScanner = (LocalFolderScanner)AppContextManager.
				getAppContext().getBean("localFolderScanner"); 

	}

	@Override
	public void harvestRdf() {
		List<String> rdfUrls = urlScanner.getRdfUrlList();
		for (String url : rdfUrls) {
			boolean status = persisterController.persistRdf(url, rdfStore);
			if (!status) {
				// something went wrong, return with error status
				logger.error(" something went wrong while persisting url :"
						+ url);
				break;
			}
		}

	}

}
