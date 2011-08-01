package ac.uk.soton.ecs.sw.semblog.tstore.ranking.factor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.common.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.impl.jena.JenaRdfRetriever;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IClusterSearcher;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.impl.KMeansClusterSearcher;
import ac.uk.soton.ecs.sw.semblog.tstore.ranking.AbstractScoreFactor;

@Component
public class VectorDistanceScoreFactor extends AbstractScoreFactor {
	
	private static final Logger logger = Logger
			.getLogger(VectorDistanceScoreFactor.class);

	@Autowired
	private IClusterSearcher clusterSearcher;
	
	public VectorDistanceScoreFactor(double weight) {
		super(weight);		
	}
	
	public VectorDistanceScoreFactor() {
		super();		
	}
	
	@Override
	public double calculateScore(ILink blog, ILink webpage) {
		if(clusterSearcher == null){
			logger.error("clusterSearcher is null!!");
			clusterSearcher = new KMeansClusterSearcher();
		}
		double diff = clusterSearcher.getDistanceFromCenter(blog.getUrlValue()) ;
		
		logger.info("Distance From Center = " + diff);
		
		//return the inverse of this difference. i.e. 
		//the longer the time difference smaller the score
		//avoid "divide by zero"
		return (diff != 0.0 ? weightage/ diff : weightage);
	}

}
