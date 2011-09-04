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
	 * bind the persistence handlers together and then invoke the chain of
	 * responsibility.
	 */
	@Override
	public boolean persistRdf(String url, IRdfStore rdfStore) {
		boolean status = true;
		List<String> rdfPersisterBeans = JaxbContextLoader.loadRdfpersisters();
		logger.info("number of persisters : " + rdfPersisterBeans.size());
		for (String beanName : rdfPersisterBeans) {
			logger.info("Calling persister : " + beanName);
			AbstractRdfPersister persister = (AbstractRdfPersister) AppContextManager
					.getAppContext().getBean(beanName);
			status = persister.persistRdf(url, rdfStore);
			logger.info("status : " + status);
			// if the status is true, that means the url content
			// is persisted as rdf, no need to process further
			if (status) {
				break;
			}
		}
		return status;
	}

}
