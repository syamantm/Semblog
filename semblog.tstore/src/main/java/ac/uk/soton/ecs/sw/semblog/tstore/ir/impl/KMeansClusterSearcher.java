package ac.uk.soton.ecs.sw.semblog.tstore.ir.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.clustering.AbstractCluster;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.common.iterator.sequencefile.PathFilters;
import org.apache.mahout.common.iterator.sequencefile.PathType;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileDirValueIterable;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.VectorWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IClusterSearcher;

@Service
public class KMeansClusterSearcher implements IClusterSearcher {

	private static final Logger logger = LoggerFactory
			.getLogger(KMeansClusterSearcher.class);
	protected static final List<List<Cluster>> clusterList = new ArrayList<List<Cluster>>();

	@Override
	public boolean searchCentroid() {
		boolean status = true;
		try {
			Path output = new Path(
					SemblogConstants.KMEANS_OUTPUT_DIRECTORY_PATH);
			loadClusters(output);

			//status = readSequenceFile();

		} catch (Exception ex) {
			ex.printStackTrace();
			status = false;
		}
		return status;
	}

	private boolean readSequenceFile() {
		logger.info("Reading sequencial file  -  begin");
		boolean status = true;
		try {
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(conf);
			String vectorsPath = SemblogConstants.KMEANS_OUTPUT_DIRECTORY_PATH
					+ "/clusters-1/part-r-00000";
			Path path = new Path(vectorsPath);

			SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf);
			LongWritable key = new LongWritable();
			VectorWritable value = new VectorWritable();
			while (reader.next(key, value)) {
				NamedVector namedVector = (NamedVector) value.get();
				RandomAccessSparseVector vect = (RandomAccessSparseVector) namedVector
						.getDelegate();

				for (Element e : vect) {
					logger.info("Token: " + e.index()
							+ ", TF-IDF weight: " + e.get());
				}
			}
			reader.close();
			logger.info("Reading sequencial file  -  end");
		} catch (Exception ex) {
			ex.printStackTrace();
			status = false;
		}
		return status;

	}

	protected static List<Cluster> readClusters(Path clustersIn) {
		List<Cluster> clusters = new ArrayList<Cluster>();
		Configuration conf = new Configuration();
		for (Cluster value : new SequenceFileDirValueIterable<Cluster>(
				clustersIn, PathType.LIST, PathFilters.logsCRCFilter(), conf)) {
			logger.info(
					"Reading Cluster:{} center:{} numPoints:{} radius:{}",
					new Object[] {
							value.getId(),
							AbstractCluster.formatVector(value.getCenter(),
									null),
							value.getNumPoints(),
							AbstractCluster.formatVector(value.getRadius(),
									null) });
			clusters.add(value);
		}
		return clusters;
	}

	protected static void loadClusters(Path output) throws IOException {
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(output.toUri(), conf);
		for (FileStatus s : fs.listStatus(output, new ClustersFilter())) {
			List<Cluster> clusters = readClusters(s.getPath());
			clusterList.add(clusters);
		}
	}

}
