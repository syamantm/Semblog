package ac.uk.soton.ecs.sw.semblog.tstore.pagerank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ac.uk.soton.ecs.sw.semblog.tstore.common.AppContextManager;
import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.importance.Ranking;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class PageRankJob {
	private static final Logger logger = Logger
			.getLogger(PageRankJob.class);


	private static Float jumpFactor = 0.15f;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/appContext.xml");
		BeanFactory factory = context;
		PageRankInput prInput = (PageRankInput) AppContextManager.getAppContext()
				.getBean("pageRankInput");		
		prInput.createPageRankInput();
		try{
			PageRankJob job = new PageRankJob();
			job.runPageRank();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void runPageRank() throws IOException{
		boolean inputExists = new File(SemblogConstants.PAGERANK_INPUT_FILE_NAME).exists();
		if (!inputExists) {
			logger.error("Input file doesn't exist!");
			System.exit(-1);
		}
		String infile = SemblogConstants.PAGERANK_INPUT_FILE_NAME;
		float alpha = jumpFactor;

		int edgeCnt = 0;
		DirectedSparseGraph<String, Integer> graph = new DirectedSparseGraph<String, Integer>();

		BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(infile)));

		String line;
		while ((line = data.readLine()) != null) {
			line.trim();
			String[] arr = line.split("\\t");

			for (int i = 1; i < arr.length; i++) {
				graph.addEdge(new Integer(edgeCnt++), arr[0], arr[i]);
			}
		}

		data.close();

		WeakComponentClusterer<String, Integer> clusterer = new WeakComponentClusterer<String, Integer>();

		Set<Set<String>> components = clusterer.transform(graph);
		int numComponents = components.size();
		logger.info("Number of components: " + numComponents);
		logger.info("Number of edges: " + graph.getEdgeCount());
		logger.info("Number of nodes: " + graph.getVertexCount());
		logger.info("Random jump factor: " + alpha);

		// Compute PageRank.
		PageRank<String, Integer> ranker = new PageRank<String, Integer>(graph, alpha);
		ranker.evaluate();

		// Use priority queue to sort vertices by PageRank values.
		PriorityQueue<Ranking<String>> q = new PriorityQueue<Ranking<String>>();
		int i = 0;
		for (String pmid : graph.getVertices()) {
			q.add(new Ranking<String>(i++, ranker.getVertexScore(pmid), pmid));
		}

		// Print PageRank values.
		logger.info("\nPageRank of nodes, in descending order:");
		
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, new Path(SemblogConstants.PAGERANK_OUTPUT_FILE_NAME),
		   Text.class,  DoubleWritable.class);
		Ranking<String> r = null;
		while ((r = q.poll()) != null) {
			logger.info(r.rankScore + "\t" + r.getRanked());
			Text key = new Text(r.getRanked());
			DoubleWritable value = new DoubleWritable(r.rankScore);
			writer.append(key, value);
		}
		writer.close();
	}

}
