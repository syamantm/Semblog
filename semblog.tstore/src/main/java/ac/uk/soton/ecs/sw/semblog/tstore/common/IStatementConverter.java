package ac.uk.soton.ecs.sw.semblog.tstore.common;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public interface IStatementConverter<T> {
	
	public List<Statement> convert(Resource subject, List<T> list);

}
