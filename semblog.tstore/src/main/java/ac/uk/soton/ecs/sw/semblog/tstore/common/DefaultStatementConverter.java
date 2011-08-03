package ac.uk.soton.ecs.sw.semblog.tstore.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IStatementConverter;

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
public class DefaultStatementConverter implements IStatementConverter<ILink>{

	private static final Logger logger = Logger
			.getLogger(DefaultStatementConverter.class);
	
	private static final String SIOC_NS = "http://rdfs.org/sioc/ns#";
	private static final String SIOC_LINKS_TO = "links_to";
	
	private static final String DC_NS = "http://purl.org/dc/terms/";
	private static final String DC_TITLE = "title";
	private static final String DC_CREATED = "created";
	private static final String DC_CREATOR= "creator";
	
	public List<Statement> convertLinks(Resource subject, List<ILink> list) {
		List<Statement> stmtList = new ArrayList<Statement>();
		for(ILink link : list){
			//logger.info("Creating triple : <" + subject.getURI() + ">  sioc:links_to <" + link.getUrlValue()+">" );
			Property predicate = new PropertyImpl(SIOC_NS, SIOC_LINKS_TO);
			Node node = Node.createLiteral(link.getUrlValue(), null, XSDDateType.XSDanyURI);
			RDFNode object = new LiteralImpl(node, null);
			Statement statement = new StatementImpl(subject, predicate, object	);
			stmtList.add(statement);
			//logger.info("Statement added" );
		}
		return stmtList;
	}

	@Override
	public Statement convertTitle(Resource subject, String title) {
		logger.info("Creating triple : <" + subject.getURI() + ">  dc:title " + title );
		Property predicate = new PropertyImpl(DC_NS, DC_TITLE);
		Node node = Node.createLiteral(title);
		RDFNode object = new LiteralImpl(node, null);
		Statement statement = new StatementImpl(subject, predicate, object	);
		
		logger.info("Statement added" );
		return statement;
	}

	@Override
	public Statement convertCreationDate(Resource subject, String date) {		
		
		logger.info("Creating triple : <" + subject.getURI() + ">  dc:created " + date );
		Property predicate = new PropertyImpl(DC_NS, DC_CREATED);
		Node node = Node.createLiteral(date, null, XSDDateType.XSDdateTime);
		RDFNode object = new LiteralImpl(node, null);
		Statement statement = new StatementImpl(subject, predicate, object	);
		
		logger.info("Statement added" );
		return statement;
	}

	@Override
	public Statement convertCreator(Resource subject, String author) {
		logger.info("Creating triple : <" + subject.getURI() + ">  dc:creator " + author );
		Property predicate = new PropertyImpl(DC_NS, DC_CREATOR);
		Node node = Node.createLiteral(author);
		RDFNode object = new LiteralImpl(node, null);
		Statement statement = new StatementImpl(subject, predicate, object	);
		
		logger.info("Statement added" );
		return statement;
	}

}
