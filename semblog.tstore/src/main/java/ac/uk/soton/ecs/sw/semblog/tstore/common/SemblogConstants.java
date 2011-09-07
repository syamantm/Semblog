package ac.uk.soton.ecs.sw.semblog.tstore.common;

import org.apache.lucene.util.Version;

public class SemblogConstants {

	public static final String BASE_WORK_DIRECTORY = "/home/syamantak/semblog";
	
	public static final String INDEX_DIRECTORY_PATH = BASE_WORK_DIRECTORY
			+ "/lucene/index";
	public static final String KMEANS_INPUT_VECTOR_PATH = BASE_WORK_DIRECTORY
			+ "/kmeans/vectors";
	public static final String KMEANS_INPUT_DICTIONARY_PATH = BASE_WORK_DIRECTORY
			+ "/kmeans/dictionary";
	public static final String KMEANS_OUTPUT_DIRECTORY_PATH = BASE_WORK_DIRECTORY
			+ "/kmeans/clusters";

	public static final Version LUCENE_VERSION = Version.LUCENE_31;

	public static final String NEW_FILE_PREFIX = "uri";
	public static final String NEW_FILE_SUFFIX = "new";
	public static final String HOT_FOLDER_PATH = "/home/syamantak/Work/java/semblog/hot";
	public static final String COLD_FOLDER_PATH = "/home/syamantak/Work/java/semblog/cold";
	public static final String ERR_FOLDER_PATH = "/home/syamantak/Work/java/semblog/error";
	public static final double ACCEPTABLE_DISTANCE = 4.5d;

	public static final String CLASSIFIER_TYPE = "bayes";
	
	public static final String BAYES_MODEL_DIRECTORY = BASE_WORK_DIRECTORY
			+ "/bayes/model";
	public static final String BAYES_TRAIN_INPUT = BASE_WORK_DIRECTORY
			+ "/bayes/train_input";
	public static final String BAYES_TEST_INPUT = BASE_WORK_DIRECTORY
			+ "/bayes/test_input";
	
	public static final String BAYES_TEST_DATA = BASE_WORK_DIRECTORY
			+ "/bayes/test_data";

	public static final String PAGERANK_DIRECTORY = BASE_WORK_DIRECTORY + "/pagerank";
	public static final String PAGERANK_INPUT_FILE_NAME = PAGERANK_DIRECTORY + "/prInputGraph.txt";
	public static final String PAGERANK_OUTPUT_FILE_NAME = PAGERANK_DIRECTORY + "/pageRankOutput";
	
	public static final String NO_AUTHOR = "anonymous";
	
	public static final String NEW_BAYES_FILE_PREFIX = "post";
	public static final String NEW_BAYES_FILE_SUFFIX = "txt";
}
