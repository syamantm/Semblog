package ac.uk.soton.ecs.sw.semblog.tstore.ranking.factor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfRetriever;
import ac.uk.soton.ecs.sw.semblog.tstore.impl.jena.JenaRdfRetriever;
import ac.uk.soton.ecs.sw.semblog.tstore.ranking.AbstractScoreFactor;

@Component
public class PredicateScoreFactor extends AbstractScoreFactor  {
	
	private static final Logger logger = Logger
			.getLogger(PredicateScoreFactor.class);
	
	private static final double SIOC_TOPIC_PREDICATE = 0.5;
	
	private static final double SIOC_LINKS_TO_PREDICATE = 0.25;
	
	private static final double NULL_PREDICATE = 0.1;

	@Autowired
	private IRdfRetriever rdfRetriever;
	
	public PredicateScoreFactor(double weight) {
		super(weight);		
	}
	
	public PredicateScoreFactor() {
		super();		
	}

	@Override
	public double calculateScore(ILink blog, ILink webpage) {
		if(rdfRetriever == null){
			logger.error("rdfRetriever is null!!");
			rdfRetriever = new JenaRdfRetriever();
		}
		
		String predicate = rdfRetriever.getPredicate(blog, webpage);
		logger.info("predicate is : " + predicate);
		double predicateValue = convertPredicateToValue(predicate);
		
		logger.info("Predicate Value = " + predicateValue);
		
		return weightage * predicateValue;
	}
	
	protected double convertPredicateToValue(String predicate){
		if(null == predicate){
			return NULL_PREDICATE;
		}else if(predicate.contains("topic")){
			return SIOC_TOPIC_PREDICATE;
		}else{
			return SIOC_LINKS_TO_PREDICATE;
		}
	}

}
