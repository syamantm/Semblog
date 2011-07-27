package ac.uk.soton.ecs.sw.semblog.tstore.common;

import org.apache.lucene.util.Version;

public class SemblogConstants {
	
	public static final String INDEX_DIRECTORY_PATH = "/home/syamantak/lucene/index";
	public static final String KMEANS_INPUT_VECTOR_PATH = "/home/syamantak/kmeans/vectors";
	public static final String KMEANS_INPUT_DICTIONARY_PATH = "/home/syamantak/kmeans/dictionary";
	public static final String KMEANS_OUTPUT_DIRECTORY_PATH = "/home/syamantak/kmeans/clusters";
	
	public static final Version LUCENE_VERSION = Version.LUCENE_31;
	
	public static final String NEW_FILE_PREFIX = "uri";
	public static final String NEW_FILE_SUFFIX = "new";
	public static final String HOT_FOLDER_PATH = "/home/syamantak/Work/java/semblog/hot"; 
	public static final String COLD_FOLDER_PATH = "/home/syamantak/Work/java/semblog/cold";
	public static final String ERR_FOLDER_PATH = "/home/syamantak/Work/java/semblog/error";
	public static final double ACCEPTABLE_DISTANCE = 2.5d;
	

}
