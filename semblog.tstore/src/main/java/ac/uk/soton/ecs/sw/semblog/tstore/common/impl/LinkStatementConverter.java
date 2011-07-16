package ac.uk.soton.ecs.sw.semblog.tstore.common.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.common.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.common.IStatementConverter;

import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateType;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;

@Component
public class LinkStatementConverter implements IStatementConverter<ILink>{

	private static final String SIOC_NS = "http://rdfs.org/sioc/ns#";
	private static final String SIOC_LINKS_TO = "links_to";
	
	public List<Statement> convert(Resource subject, List<ILink> list) {
		List<Statement> stmtList = new ArrayList<Statement>();
		for(ILink link : list){
			Property predicate = new PropertyImpl(SIOC_NS, SIOC_LINKS_TO);
			Node node = Node.createLiteral(link.getUrlValue(), null, XSDDateType.XSDanyURI);
			RDFNode object = new LiteralImpl(node, null);
			Statement statement = new StatementImpl(subject, predicate, object	);
			stmtList.add(statement);
		}
		return stmtList;
	}

}
