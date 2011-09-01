package ac.uk.soton.ecs.sw.semblog.tstore.classifier;

import org.apache.log4j.Logger;
import org.apache.mahout.classifier.bayes.TestClassifier;
import org.springframework.stereotype.Service;

import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;

@Service
public class BayesSpamDetector implements ISpamDetector {

	private static final Logger logger = Logger.getLogger(BayesSpamDetector.class);	

	@Override
	public boolean detectSpam() {
		boolean status = true;
		try {
			String cmdLine = "-m " + SemblogConstants.BAYES_MODEL_DIRECTORY
					+ " -d " + SemblogConstants.BAYES_TEST_INPUT 
					+ " -type "	+ SemblogConstants.CLASSIFIER_TYPE
					+ " -ng 1 -source hdfs -method sequential";

			logger.info(cmdLine);
			// generate arguments array from command line
			String[] args = cmdLine.split(" ");
			TestClassifier.main(args);

		} catch (Exception ex) {
			ex.printStackTrace();
			status = false;
		}
		return status;
	}

}
