package ac.uk.soton.ecs.sw.semblog.tstore.classifier;

public interface ISpamDetector {
	public boolean testSpamDetection();
	public boolean isSpamPost(String content);

}
