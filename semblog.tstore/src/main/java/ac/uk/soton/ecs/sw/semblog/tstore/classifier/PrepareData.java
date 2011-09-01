package ac.uk.soton.ecs.sw.semblog.tstore.classifier;

import org.apache.log4j.Logger;
import org.apache.mahout.classifier.bayes.PrepareTwentyNewsgroups;
import org.springframework.stereotype.Service;

import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;

@Service
public class PrepareData implements IPrepareData {

	private static final Logger logger = Logger.getLogger(PrepareData.class);

	public boolean generateData() {

		boolean status = true;
		try {

			generateTrainData();

			generateTestData();

		} catch (Exception ex) {
			ex.printStackTrace();
			status = false;
		}
		return status;
	}

	public void generateTestData() throws Exception {
		String cmdLineTest = "-p " + SemblogConstants.BAYES_TEST_DATA
				+ "/20news-bydate-test" + " -o "
				+ SemblogConstants.BAYES_TEST_INPUT
				+ " -a org.apache.mahout.vectorizer.DefaultAnalyzer"
				+ " -c UTF-8";

		logger.info(cmdLineTest);
		// generate arguments array from command line
		String[] argsTest = cmdLineTest.split(" ");
		PrepareTwentyNewsgroups.main(argsTest);
	}

	public void generateTrainData() throws Exception {
		String cmdLineTrain = "-p " + SemblogConstants.BAYES_TEST_DATA
				+ "/20news-bydate-train" + " -o "
				+ SemblogConstants.BAYES_TRAIN_INPUT
				+ " -a org.apache.mahout.vectorizer.DefaultAnalyzer"
				+ " -c UTF-8";

		logger.info(cmdLineTrain);
		// generate arguments array from command line
		String[] argsTrain = cmdLineTrain.split(" ");
		PrepareTwentyNewsgroups.main(argsTrain);
	}

}
