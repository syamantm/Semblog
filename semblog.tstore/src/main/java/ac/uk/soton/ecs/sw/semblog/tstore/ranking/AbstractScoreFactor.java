package ac.uk.soton.ecs.sw.semblog.tstore.ranking;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;

public abstract class AbstractScoreFactor {
	
	protected double weightage;
	
	public abstract double calculateScore(ILink blog, ILink webpage);
	
	/**
	 * for Spring only
	 */
	public AbstractScoreFactor(){
		this.weightage = 0.0;
	}
	
	public AbstractScoreFactor(double weight){
		this.weightage = weight;
	}

	/**
	 * @return the weightage
	 */
	public double getWeightage() {
		return weightage;
	}

	/**
	 * @param weightage the weightage to set
	 */
	public void setWeightage(double weightage) {
		this.weightage = weightage;
	}

}
