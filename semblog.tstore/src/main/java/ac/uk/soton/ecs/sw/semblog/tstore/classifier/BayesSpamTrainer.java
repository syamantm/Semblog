package ac.uk.soton.ecs.sw.semblog.tstore.classifier;

import org.apache.log4j.Logger;
import org.apache.mahout.classifier.bayes.TrainClassifier;
import org.springframework.stereotype.Service;

import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;

@Service
public class BayesSpamTrainer implements ISpamTrainer {
	
	private static final Logger logger = Logger.getLogger(BayesSpamTrainer.class);	

	@Override
	public boolean trainSpam() {
		boolean status = true;
		try {
			String cmdLine = "-i " + SemblogConstants.BAYES_TRAIN_INPUT
					+ " -o " + SemblogConstants.BAYES_MODEL_DIRECTORY
					+ " -type " + SemblogConstants.CLASSIFIER_TYPE
					+ " -ng 1 -source hdfs";

			logger.info(cmdLine);
			// generate arguments array from command line
			String[] args = cmdLine.split(" ");
			TrainClassifier.main(args);

		} catch (Exception ex) {
			ex.printStackTrace();
			status = false;
		}
		return status;
	}

}
