package ac.uk.soton.ecs.sw.semblog.tstore.api;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public interface IStatementConverter<T> {
	
	public List<Statement> convertLinks(Resource subject, List<T> list);
	
	public Statement convertTitle(Resource subject, String title);
	
	public Statement convertCreationDate(Resource subject, String date);
	
	public Statement convertCreator(Resource subject, String author);

}
