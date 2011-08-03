package ac.uk.soton.ecs.sw.semblog.tstore.ranking.factor;

import java.sql.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfRetriever;
import ac.uk.soton.ecs.sw.semblog.tstore.impl.jena.JenaRdfRetriever;
import ac.uk.soton.ecs.sw.semblog.tstore.ranking.AbstractScoreFactor;

@Component
public class DateScoreFactor extends AbstractScoreFactor {
	private static final Logger logger = Logger
			.getLogger(DateScoreFactor.class);

	@Autowired
	private IRdfRetriever rdfRetriever;
	
	public DateScoreFactor(double weight) {
		super(weight);		
	}
	
	public DateScoreFactor() {
		super();		
	}
	
	@Override
	public double calculateScore(ILink blog, ILink webpage) {
		if(rdfRetriever == null){
			logger.error("rdfRetriever is null!!");
			rdfRetriever = new JenaRdfRetriever();
		}
		
		Date blogDate = rdfRetriever.getCreationDate(blog);
		java.util.Date today = new java.util.Date();
		Date sqlToday = new Date(today.getTime());
		double diff = (sqlToday.getTime() - blogDate.getTime())/86400000 ;
		
		logger.info("Date differance = " + diff);
		
		//return the inverse of this difference. i.e. 
		//the longer the time difference smaller the score
		//avoid "divide by zero"
		return (diff != 0.0 ? weightage/ diff : weightage);  
	}

}
