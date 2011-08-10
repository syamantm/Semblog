package ac.uk.soton.ecs.sw.semblog.tstore.ranking;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;

public abstract class AbstractScoreFactor {

	protected double weight;

	public abstract double calculateScore(ILink blog, ILink webpage);

	/**
	 * for Spring only
	 */
	public AbstractScoreFactor() {
		this.weight = 0.0;
	}

	public AbstractScoreFactor(double weight) {
		this.weight = weight;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

}
