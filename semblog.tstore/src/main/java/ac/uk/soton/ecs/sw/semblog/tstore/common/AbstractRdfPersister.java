package ac.uk.soton.ecs.sw.semblog.tstore.common;

import java.util.UUID;

import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStore;
import ac.uk.soton.ecs.sw.semblog.tstore.vocabulary.SEMBLOG;

import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateType;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;


public abstract class AbstractRdfPersister{
	
	public abstract  boolean persistRdf(String url, IRdfStore rdfStore) ;
	
	public void addNodeUUID(String url, Model linkedDataModel){
		Resource subject = new ResourceImpl(url);
		Property predicate = new PropertyImpl(SEMBLOG.NS, SEMBLOG.PROP_NODE_ID);
		UUID uid = UUID.randomUUID();
		Node node = Node.createLiteral(uid.toString(), null, XSDDateType.XSDstring);
		RDFNode object = new LiteralImpl(node, null);
		Statement statement = new StatementImpl(subject, predicate, object	);
		linkedDataModel.add(statement);		
	}

}
