package ac.uk.soton.ecs.sw.semblog.tstore.pagerank;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfRetriever;
import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;

@Component
public class PageRankOutputRetriever implements IPageRankRetriever {

	@Autowired
	private IRdfRetriever rdfRetriever;

	@Override
	public double getPageRank(ILink link) {
		double pageRank = 0.10d;
		try {
			String nodeId = rdfRetriever.getNodeUUID(link);
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(conf);
			String outputPath = SemblogConstants.PAGERANK_OUTPUT_FILE_NAME;
			Path path = new Path(outputPath);
			SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf);
			DoubleWritable value = new DoubleWritable();
			Text key = new Text();
			while (reader.next(key, value)) {
				if (key.toString().endsWith(nodeId)) {
					pageRank = value.get();
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return pageRank;

	}

}
