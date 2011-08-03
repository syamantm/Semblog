package ac.uk.soton.ecs.sw.semblog.tstore.common;

import java.util.List;

import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfPersister;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfPersisterController;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStore;

@Component
public class DefaultPersisterController implements IRdfPersisterController {

	@Override
	public boolean persistRdf(String url, IRdfStore rdfStore) {
		boolean status = true;
		List<String> rdfPersisterBeans = JaxbContextLoader.loadRdfpersisters();
		for(String beanName : rdfPersisterBeans){
			IRdfPersister persister = (IRdfPersister) AppContextManager.getAppContext()
					.getBean(beanName);		
			status = persister.persistRdf(url, rdfStore);
			//if the status is true, that means the url content
			//is persisted as rdf, no need to process further 
			if(status){
				break;
			}
		}
		return status;
		
	}

}
