package ac.uk.soton.ecs.sw.semblog.tstore.pagerank;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfRetriever;
import ac.uk.soton.ecs.sw.semblog.tstore.common.GraphDataNode;
import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;

@Component
public class PageRankInput {
	private static final Logger logger = Logger
			.getLogger(PageRankInput.class);
	
	@Autowired
	private IRdfRetriever rdfRetriever;

	/**
	 * store the graph as line of string in the below format
	 * ***************************************************** SOURCE_ID TAB
	 * DESTINATION_ID1 TAB DESTINATION_ID2 ....
	 * ***************************************************** This is the
	 * required format for Cloud9 library SequentialPageRank class
	 * 
	 */
	private List<String> graphAsStr = null;
	private static Float jumpFactor = 0.15f;

	public boolean createPageRankInput() {
		boolean status = true;
		try {
			createGraph();
			createInputFile();
		} catch (Exception ex) {
			ex.printStackTrace();
			status = false;
		}
		return status;
	}

	public void createGraph() {
		graphAsStr = new ArrayList<String>();

		List<GraphDataNode> graph = new ArrayList<GraphDataNode>();		
		graph = rdfRetriever.getGraphAsList();
		if (graph != null) {
			for (GraphDataNode node : graph) {
				StringBuilder builder = new StringBuilder();
				UUID nodeId = node.getNodeId();
				builder.append(nodeId);
				builder.append("\t");
				//logger.info("node.getConnectedNodes() size : " + node.getConnectedNodes().size());
				for (UUID connectedNode : node.getConnectedNodes()) {
					builder.append(connectedNode);
					builder.append("\t");
				}
				logger.info(builder.toString());
				graphAsStr.add(builder.toString());
			}
		}
	}

	public void createInputFile() {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(
					SemblogConstants.PAGERANK_INPUT_FILE_NAME));
			for (String graphRow : graphAsStr) {
				pw.println(graphRow);
			}
			pw.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null)
				pw.close();
		}
	}

}
