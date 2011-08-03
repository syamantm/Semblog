package ac.uk.soton.ecs.sw.semblog.tstore.ranking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.common.AppContextManager;
import ac.uk.soton.ecs.sw.semblog.tstore.common.JaxbContextLoader;
import ac.uk.soton.ecs.sw.semblog.tstore.impl.jena.JenaRdfStoreManager1;

public final class DefaultScoreCalculator implements IScoreCalculator {

	private static final Logger logger = Logger
			.getLogger(DefaultScoreCalculator.class);

	List<AbstractScoreFactor> scoreFactorList = new ArrayList<AbstractScoreFactor>();

	private static DefaultScoreCalculator instance = null;

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
			Map<String, Double> scoreFactors = JaxbContextLoader
					.loadScoreFactors();
			for (String beanName : scoreFactors.keySet()) {
				
				AbstractScoreFactor factor = (AbstractScoreFactor) AppContextManager.getAppContext()
						.getBean(beanName);				
				if (factor != null) {

					double weightage = scoreFactors.get(beanName);
					factor.setWeightage(weightage);
					logger.info("Bean Name : " + beanName + " weightage : "
							+ weightage);
				}
				DefaultScoreCalculator.getInstance()
						.registerScoreFactor(factor);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void registerScoreFactor(AbstractScoreFactor factor) {
		scoreFactorList.add(factor);
	}

	@Override
	public double calculateScore(ILink blog, ILink webpage) {
		double score = 0.0;
		for (AbstractScoreFactor factor : scoreFactorList) {
			score += factor.calculateScore(blog, webpage);
		}
		return score;
	}

}
