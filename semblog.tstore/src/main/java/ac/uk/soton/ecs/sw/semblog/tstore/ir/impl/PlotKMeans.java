/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ac.uk.soton.ecs.sw.semblog.tstore.ir.impl;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;

import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.ClusterClassifier;
import org.apache.mahout.clustering.ClusterIterator;
import org.apache.mahout.clustering.ClusteringPolicy;
import org.apache.mahout.clustering.KMeansClusteringPolicy;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.ManhattanDistanceMeasure;
import org.apache.mahout.math.Vector;

public class PlotKMeans extends PlotClustering {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	PlotKMeans() {
		initialize();
		this.setTitle("k-Means Clusters (>" + (int) (significance * 100)
				+ "% of population)");
	}

	public static void main(String[] args) throws Exception {
		DistanceMeasure measure = new ManhattanDistanceMeasure();
		Path samples = new Path(SemblogConstants.KMEANS_INPUT_VECTOR_PATH + "/outputSequenceFile");
		Path output = new Path(SemblogConstants.KMEANS_OUTPUT_DIRECTORY_PATH);
		//Configuration conf = new Configuration();		

		RandomUtils.useTestSeed();
		/*
		 * DisplayClustering.generateSamples(); writeSampleData(samples);
		 * boolean runClusterer = false; if (runClusterer) { int numClusters =
		 * 3; runSequentialKMeansClusterer(conf, samples, output, measure,
		 * numClusters); } else { int maxIterations = 10;
		 * runSequentialKMeansClassifier(conf, samples, output, measure,
		 * maxIterations); }
		 */
		loadSamples(samples);
		loadClusters(output);
		new PlotKMeans();
	}
	


	// Override the paint() method
	@Override
	public void paint(Graphics g) {
		plotSampleData((Graphics2D) g);
		plotClusters((Graphics2D) g);
	}
}
