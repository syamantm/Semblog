package ac.uk.soton.ecs.sw.semblog.tstore.ir.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.clustering.AbstractCluster;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.WeightedVectorWritable;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.common.iterator.sequencefile.PathFilters;
import org.apache.mahout.common.iterator.sequencefile.PathType;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileDirValueIterable;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.common.PageLink;
import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IClusterSearcher;

@Service
public class KMeansClusterSearcher implements IClusterSearcher {

	private static final Logger logger = LoggerFactory
			.getLogger(KMeansClusterSearcher.class);
	protected static final List<List<Cluster>> clusterList = new ArrayList<List<Cluster>>();

	Map<String, List<Vector>> clusterVectorMap = new HashMap<String, List<Vector>>();
	
	private Vector currentPageVector = null;

	@Override
	public Set<ILink> retrieveSimilarPages(String url) {
		Set<ILink> similarPages = new HashSet<ILink>();
		try {

			String idStr = getClusterId(url);
			int clusterId = Integer.parseInt(idStr);
			Path output = new Path(
					SemblogConstants.KMEANS_OUTPUT_DIRECTORY_PATH);
			Cluster cluster = KMeansClusterSearcher.getCluster(clusterId,
					output);
			List<Vector> clusterVectors = clusterVectorMap.get(idStr);
			Vector center = cluster.getCenter();
			Vector radius = cluster.getRadius();
			if (radius != null) {
				ILink link = new PageLink(((NamedVector) radius).getName());
				similarPages.add(link);
			}
			DistanceMeasure measure = new EuclideanDistanceMeasure();

			for (Vector vec : clusterVectors) {
				NamedVector named = (NamedVector) vec;
				double newDistance = measure.distance(center, vec);
				logger.info("Distance between center and " + named.getName()
						+ " is  : " + newDistance);
				if (newDistance < SemblogConstants.ACCEPTABLE_DISTANCE) {
					ILink link = new PageLink(((NamedVector) named).getName());
					similarPages.add(link);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return similarPages;
	}

	@Override
	public boolean searchCentroid() {
		boolean status = true;
		try {
			Path output = new Path(
					SemblogConstants.KMEANS_OUTPUT_DIRECTORY_PATH);
			loadClusters(output);

		} catch (Exception ex) {
			ex.printStackTrace();
			status = false;
		}
		return status;
	}

	private String getClusterId(String url) {
		logger.info("Reading sequencial file  -  begin");
		String clusterId = null;
		clusterVectorMap.clear();
		try {
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(conf);
			String vectorsPath = SemblogConstants.KMEANS_OUTPUT_DIRECTORY_PATH
					+ "/clusteredPoints/part-m-0";
			Path path = new Path(vectorsPath);

			SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf);
			IntWritable key = new IntWritable();
			WeightedVectorWritable value = new WeightedVectorWritable();
			while (reader.next(key, value)) {
				NamedVector namedVector = (NamedVector) value.getVector();
				clusterId = key.toString();
				logger.info("Cluster ID : " + clusterId);
				logger.info("Vector name : " + namedVector.getName());
				if (clusterVectorMap.containsKey(clusterId)) {
					List<Vector> list = clusterVectorMap.get(clusterId);
					list.add(namedVector);
					logger.info("Adding Key : " + clusterId + " value : "
							+ namedVector.getName());

				} else {
					List<Vector> list = new ArrayList<Vector>();
					list.add(namedVector);
					clusterVectorMap.put(clusterId, list);
					logger.info("Adding Key : " + clusterId + " value : "
							+ namedVector.getName());
				}

				if (namedVector.getName().equals(url)) {
					currentPageVector = namedVector;
					clusterId = key.toString();
				}
			}
			reader.close();
			logger.info("Reading sequencial file  -  end");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return clusterId;

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

	protected static Cluster getCluster(int clusterId, Path output)
			throws IOException {
		Configuration conf = new Configuration();
		Cluster cluster = null;
		boolean found = false;
		FileSystem fs = FileSystem.get(output.toUri(), conf);
		for (FileStatus s : fs.listStatus(output, new ClustersFilter())) {
			Path clustersIn = s.getPath();
			for (Cluster value : new SequenceFileDirValueIterable<Cluster>(
					clustersIn, PathType.LIST, PathFilters.logsCRCFilter(),
					conf)) {
				logger.info(
						"Reading Cluster:{} center:{} numPoints:{} radius:{}",
						new Object[] {
								value.getId(),
								AbstractCluster.formatVector(value.getCenter(),
										null),
								value.getNumPoints(),
								AbstractCluster.formatVector(value.getRadius(),
										null) });

				if (value.getId() == clusterId) {
					cluster = value;
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
		}
		return cluster;
	}

	protected static void loadClusters(Path output) throws IOException {
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(output.toUri(), conf);
		for (FileStatus s : fs.listStatus(output, new ClustersFilter())) {
			List<Cluster> clusters = readClusters(s.getPath());
			clusterList.add(clusters);
		}
	}

	@Override
	public double getDistanceFromCenter(String url) {
		double newDistance = 0.0d;
		try {

			String idStr = getClusterId(url);
			int clusterId = Integer.parseInt(idStr);
			Path output = new Path(
					SemblogConstants.KMEANS_OUTPUT_DIRECTORY_PATH);
			Cluster cluster = KMeansClusterSearcher.getCluster(clusterId,
					output);

			Vector center = cluster.getCenter();
			DistanceMeasure measure = new EuclideanDistanceMeasure();
			newDistance = measure.distance(center, currentPageVector);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return newDistance;
	}

}
