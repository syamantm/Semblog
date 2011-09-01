package ac.uk.soton.ecs.sw.semblog.tstore.common;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfPersister;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfPersisterController;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStore;
import ac.uk.soton.ecs.sw.semblog.tstore.impl.drupal.DrupalRdfPersister;

@Component
public class DefaultPersisterController implements IRdfPersisterController {

	private static final Logger logger = Logger
			.getLogger(DrupalRdfPersister.class);
	
	/**
	 * bind the persistence handlers together and then invoke the
	 * chain of responsibility.
	 */
	@Override
	public boolean persistRdf(String url, IRdfStore rdfStore) {
		boolean status = true;
		Map<String, String> rdfPersisterBeans = JaxbContextLoader.loadRdfpersisters();
		logger.info("number of persisters : " + rdfPersisterBeans.size());
		boolean first = false;
		AbstractRdfPersister firstPersister = null;
		for(String beanName : rdfPersisterBeans.keySet()){
			logger.info("Calling persister : " + beanName);
			AbstractRdfPersister persister = (AbstractRdfPersister) AppContextManager.getAppContext()
					.getBean(beanName);		
			String successor = rdfPersisterBeans.get(beanName);
			
			AbstractRdfPersister successorPersister = (AbstractRdfPersister) AppContextManager.getAppContext()
					.getBean(successor);
			persister.setSuccessor(successorPersister);		
			if(!first){
				first = true;
				firstPersister = persister;
			}
		}
		//invoke the chain of responsibility with the first handler
		if(firstPersister != null){
			firstPersister.persistRdf(url, rdfStore);
		}
		return status;
		
	}

}
