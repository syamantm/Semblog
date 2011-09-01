package ac.uk.soton.ecs.sw.semblog.tstore.ranking;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;

public interface IScoreCalculator {
	
	public void registerScoreFactor(String beanName, AbstractScoreFactor factor);
	
	public void changeWeight(String beanName, double weightage);
	
	public void restoreDefault();
	
	public double calculateScore(ILink blog, ILink webpage); 

}
