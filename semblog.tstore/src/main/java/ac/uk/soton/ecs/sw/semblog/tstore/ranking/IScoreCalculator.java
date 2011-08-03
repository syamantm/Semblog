package ac.uk.soton.ecs.sw.semblog.tstore.ranking;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;

public interface IScoreCalculator {
	
	public void registerScoreFactor(AbstractScoreFactor factor);
	
	public double calculateScore(ILink blog, ILink webpage); 

}
