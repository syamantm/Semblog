package ac.uk.soton.ecs.sw.semblog.tstore.classifier;

import org.springframework.beans.factory.annotation.Autowired;

public class MainClassifier {

	@Autowired
	private ISpamTrainer spamTrainer = new BayesSpamTrainer();

	@Autowired
	private ISpamDetector spamDetector = new BayesSpamDetector();

	@Autowired
	private IPrepareData prepareData = new PrepareData();

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		MainClassifier classifier = new MainClassifier();
		boolean status = classifier.runJob();
		if (status) {
			System.exit(0);
		} else {
			System.exit(1);
		}
	}

	public boolean runJob() {		
		boolean status = prepareData.generateData();
		if (status) {
			status = spamTrainer.trainSpam();
			if (status) {
				status = spamDetector.detectSpam();
			}
		}
		return status;
	}

}
