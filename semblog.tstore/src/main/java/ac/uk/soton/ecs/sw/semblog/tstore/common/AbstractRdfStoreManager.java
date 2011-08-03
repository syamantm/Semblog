package ac.uk.soton.ecs.sw.semblog.tstore.common;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfPersisterController;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStore;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStoreManager;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IUrlScanner;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IClusterCreator;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IClusterSearcher;
import ac.uk.soton.ecs.sw.semblog.tstore.ranking.AbstractBlogPost;
import ac.uk.soton.ecs.sw.semblog.tstore.ranking.SemBlogPost;

@Component("abstractRdfStoreManager")
public abstract class AbstractRdfStoreManager implements IRdfStoreManager {

	private static final Logger logger = Logger
			.getLogger(AbstractRdfStoreManager.class);

	
	protected IRdfStore rdfStore;
	
	
	protected IRdfPersisterController persisterController;
	
	
	protected IUrlScanner urlScanner;
	
	
	@Autowired
	private IClusterCreator clusterCreator;

	@Autowired
	private IClusterSearcher clusterSearcher;
	
	public abstract void setRdfStore();
	
	public abstract void setRdfPersisterController();
	
	public abstract void setUrlScanner();
	
	public abstract void harvestRdf();
	
	public void createClusters(){
		boolean status = clusterCreator.createClusters();
		if (status) {
			//test clustering 
			Set<ILink>  similarPages = clusterSearcher
					.retrieveSimilarPages("http://localhost/drupal-7.4/?q=node/3");
			for (ILink url : similarPages) {
				logger.info("Similar Page : " + url);
			}
		}
	}
	

	public boolean run() {
		boolean status = true;
		setRdfStore();
		setRdfPersisterController();
		setUrlScanner();
		rdfStore.createInitModel();
		//DefaultScoreCalculator.init();
		harvestRdf();
		List<String> rdfUrls = urlScanner.getRdfUrlList();
		for (String url : rdfUrls) {
			status = persisterController.persistRdf(url, rdfStore);
			if (!status) {
				// something went wrong, return with error status
				logger.error(" something went wrong while persisting url :"
						+ url);
				break;
			}
		}
		// if previous steps have failed, no need to run clustering
		if (status) {
			createClusters();
		}
		//test ranking 
		ILink blog = new PageLink("http://localhost/drupal-7.4/?q=node/5");
		ILink webpage = new PageLink("http://www.w3.org/2001/sw/Europe/reports/demo_1_report/");
		AbstractBlogPost post = new SemBlogPost(blog, webpage);
		System.out.println("Score : " + post.getScore());
		return status;
	}

}
