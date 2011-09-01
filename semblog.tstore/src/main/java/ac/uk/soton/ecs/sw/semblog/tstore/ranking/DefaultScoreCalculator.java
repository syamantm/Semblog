package ac.uk.soton.ecs.sw.semblog.tstore.ranking;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.common.AppContextManager;
import ac.uk.soton.ecs.sw.semblog.tstore.common.JaxbContextLoader;

public final class DefaultScoreCalculator implements IScoreCalculator {

	private static final Logger logger = Logger
			.getLogger(DefaultScoreCalculator.class);

	Map<String, AbstractScoreFactor> scoreFactorMap = new HashMap<String, AbstractScoreFactor>();

	private static DefaultScoreCalculator instance = null;
	
	private static Map<String, Double> scoreFactorConfig = null; 

	public DefaultScoreCalculator() {

	}

	public static DefaultScoreCalculator getInstance() {
		if (instance == null) {
			instance = new DefaultScoreCalculator();
			init();
		}
		return instance;
	}

	/**
	 * Load all the Score Factors from scoreFactor.xml configuration file using
	 * ac.uk.soton.ecs.sw.semblog.tstore.common.JaxbContextLoader
	 */
	public static void init() {
		/*
		 * AbstractScoreFactor dateFactor = new DateScoreFactor(0.4);
		 * AbstractScoreFactor prediacteFactor = new PredicateScoreFactor(0.3);
		 * AbstractScoreFactor vectorFactor = new
		 * VectorDistanceScoreFactor(0.3);
		 * 
		 * DefaultScoreCalculator.getInstance().registerScoreFactor(dateFactor);
		 * DefaultScoreCalculator
		 * .getInstance().registerScoreFactor(prediacteFactor);
		 * DefaultScoreCalculator
		 * .getInstance().registerScoreFactor(vectorFactor);
		 */
		try {
			scoreFactorConfig = JaxbContextLoader
					.loadScoreFactors();
			for (String beanName : scoreFactorConfig.keySet()) {
				
				AbstractScoreFactor factor = (AbstractScoreFactor) AppContextManager.getAppContext()
						.getBean(beanName);				
				if (factor != null) {

					double weight = scoreFactorConfig.get(beanName);
					factor.setWeight(weight);
					logger.info("Bean Name : " + beanName + " weight : "
							+ weight);
				}
				DefaultScoreCalculator.getInstance()
						.registerScoreFactor(beanName, factor);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	

	@Override
	public void registerScoreFactor(String beanName, AbstractScoreFactor factor) {
		scoreFactorMap.put(beanName, factor);
	}

	@Override
	public double calculateScore(ILink blog, ILink webpage) {
		double score = 0.0;
		for (String beanName : scoreFactorMap.keySet()) {
			AbstractScoreFactor factor = scoreFactorMap.get(beanName);
			double weight = factor.calculateScore(blog, webpage);
			logger.info("calculateScore Bean Name : " + beanName + " weight : "
					+ weight);
			score += weight;
			
		}
		return score;
	}

	@Override
	public void changeWeight(String beanName, double weight) {
		
		if(scoreFactorMap.containsKey(beanName)){			
			AbstractScoreFactor factor = scoreFactorMap.get(beanName);
			logger.info("changeWeight Bean Name : " + beanName + " weight : "
					+ weight);
			factor.setWeight(weight);			
		}
	}

	@Override
	public void restoreDefault() {
		for (String key: scoreFactorMap.keySet()) {
			AbstractScoreFactor factor = scoreFactorMap.get(key);
			if(scoreFactorConfig.containsKey(key))
			{
				double weight = scoreFactorConfig.get(key);
				factor.setWeight(weight);	
			}
		}
		
	}

}
