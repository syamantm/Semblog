package ac.uk.soton.ecs.sw.semblog.tstore.ir.impl;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.utils.vectors.lucene.Driver;
import org.springframework.stereotype.Service;

import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IClusterCreator;

@Service
public class KMeansClusterCreator implements IClusterCreator {

	private static final Logger logger = Logger
			.getLogger(KMeansClusterCreator.class);
	
	private static final int NUM_CLUSTERS = 2;

	@Override
	public boolean createClusters() {
		boolean status = true;
		try {
			logger.info("KMeansClusterCreator - begin");

			generateTfIdf();
			logger.info("IT-IDF generated from lucene index");

			runKMeans();
			logger.info("KMeans clustering finished!");

		} catch (Exception ex) {
			ex.printStackTrace();
			status = false;
		}
		return status;

	}

	/**
	 * Run kmeans algorithm. create parameters for the algorithm
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 */
	protected void runKMeans() throws IOException, InterruptedException,
			ClassNotFoundException {
		DistanceMeasure measure = new EuclideanDistanceMeasure();
		Path input = new Path(SemblogConstants.KMEANS_INPUT_VECTOR_PATH);
		Path output = new Path(SemblogConstants.KMEANS_OUTPUT_DIRECTORY_PATH);
		Configuration conf = new Configuration();

		int numClusters = NUM_CLUSTERS;
		runSequentialKMeansClusterer(conf, input, output, measure, numClusters);
	}

	/**
	 * Generate TF-IDF from lucene term vectors( by passing lucene index
	 * directory)
	 * 
	 * @throws IOException
	 */
	protected void generateTfIdf() throws IOException {
		logger.info("Generating TF-IDF");
		// arguments to run Lucene Driver
		String cmdLine = "--dir " + SemblogConstants.INDEX_DIRECTORY_PATH + " "
				+ "--output " + SemblogConstants.KMEANS_INPUT_VECTOR_PATH
				+ "/outputSequenceFile" + " " + "--field tag " + "--dictOut "
				+ SemblogConstants.KMEANS_INPUT_DICTIONARY_PATH + "/dictOutSequenceFile"+ " "
				+ "--idField id " + "--weight TFIDF";
		logger.info(cmdLine);
		// generate arguments array from command line
		String[] args = cmdLine.split(" ");

		// TODO - ugly way of creating it-idf, change to a proper api call.
		// unfortunately in Mahout version 0.5 Lucene driver class can only
		// be called from commandline. wait for version 0.6 ?
		Driver.main(args);
	}

	/**
	 * Run kmeans clustering algorithm for the specified input vector.
	 * 
	 * @param conf
	 *            Hadoop configuration parameters
	 * @param input
	 *            the directory pathname for input points
	 * @param output
	 *            the directory pathname for output points
	 * @param measure
	 *            the DistanceMeasure to use
	 * @param maxIterations
	 *            the maximum number of iterations
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 */
	private static void runSequentialKMeansClusterer(Configuration conf,
			Path input, Path output, DistanceMeasure measure, int maxIterations)
			throws IOException, InterruptedException, ClassNotFoundException {
		logger.info("KMeans - creating clusters");
		Path clusters = RandomSeedGenerator.buildRandom(conf, input, new Path(
				output, "clusters-0"), NUM_CLUSTERS, measure);
		double distanceThreshold = 0.54;
		KMeansDriver.run(input, clusters, output, measure, distanceThreshold,
				maxIterations, true, true);

	}

}
