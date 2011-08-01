package ac.uk.soton.ecs.sw.semblog.tstore.ranking;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ac.uk.soton.ecs.sw.semblog.tstore.common.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.ranking.factor.DateScoreFactor;
import ac.uk.soton.ecs.sw.semblog.tstore.ranking.factor.PredicateScoreFactor;
import ac.uk.soton.ecs.sw.semblog.tstore.ranking.factor.VectorDistanceScoreFactor;


public final class DefaultScoreCalculator implements IScoreCalculator{
	
	List<AbstractScoreFactor> scoreFactorList = new ArrayList<AbstractScoreFactor>();

	private static DefaultScoreCalculator instance = null;
	
	public DefaultScoreCalculator(){
		
	}
	
	public static DefaultScoreCalculator getInstance(){
		if(instance == null){
			instance = new DefaultScoreCalculator();
			init();
		}
		return instance;
	}		
	
	public static void init(){
		AbstractScoreFactor dateFactor = new DateScoreFactor(0.4);
		AbstractScoreFactor prediacteFactor = new PredicateScoreFactor(0.3);
		AbstractScoreFactor vectorFactor = new VectorDistanceScoreFactor(0.3);
		
		DefaultScoreCalculator.getInstance().registerScoreFactor(dateFactor);
		DefaultScoreCalculator.getInstance().registerScoreFactor(prediacteFactor);
		DefaultScoreCalculator.getInstance().registerScoreFactor(vectorFactor);
	}
	
	
	
	@Override
	public void registerScoreFactor(AbstractScoreFactor factor) {
		scoreFactorList.add(factor);		
	}

	@Override
	public double calculateScore(ILink blog, ILink webpage) {
		double score = 0.0;
		for(AbstractScoreFactor factor : scoreFactorList){
			score += factor.calculateScore(blog, webpage);
		}
		return score;
	}

}
