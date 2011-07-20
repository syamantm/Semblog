package ac.uk.soton.ecs.sw.semblog.tstore.ir.impl;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

public class ClustersFilter implements PathFilter {
	  @Override
	  public boolean accept(Path path) {
	    return path.toString().contains("/clusters-");
	  }
	}
