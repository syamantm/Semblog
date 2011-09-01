package ac.uk.soton.ecs.sw.semblog.tstore.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfRetriever;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IStatementConverter;
import ac.uk.soton.ecs.sw.semblog.tstore.vocabulary.DC;
import ac.uk.soton.ecs.sw.semblog.tstore.vocabulary.SEMBLOG;
import ac.uk.soton.ecs.sw.semblog.tstore.vocabulary.SIOC;

import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateType;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;

@Component
public class DefaultStatementConverter implements IStatementConverter<ILink> {

	@Autowired
	private IRdfRetriever rdfRetriever;

	private static final Logger logger = Logger
			.getLogger(DefaultStatementConverter.class);

	public List<Statement> convertLinks(Resource subject, List<ILink> list) {
		List<Statement> stmtList = new ArrayList<Statement>();
		for (ILink link : list) {
			logger.info("Creating triple : <" + subject.getURI() +
			">  sioc:links_to <" + link.getUrlValue()+">" );
			Property predicate = new PropertyImpl(SIOC.NS, SIOC.PROP_LINKS_TO);
			Node node = Node.createLiteral(link.getUrlValue(), null,
					XSDDateType.XSDanyURI);
			RDFNode object = new LiteralImpl(node, null);
			Statement statement = new StatementImpl(subject, predicate, object);
			stmtList.add(statement);
			// logger.info("Statement added" );

			// Create UUID for links as well if it already doesn't exist
			if (!rdfRetriever.isResourceExists(link)) {
				Resource subjectLink = new ResourceImpl(link.getUrlValue());
				Property predicateLink = new PropertyImpl(SEMBLOG.NS,
						SEMBLOG.PROP_NODE_ID);
				UUID uid = UUID.randomUUID();
				Node nodeLink = Node.createLiteral(uid.toString(), null,
						XSDDateType.XSDstring);
				RDFNode objectLink = new LiteralImpl(nodeLink, null);
				Statement statementLink = new StatementImpl(subjectLink,
						predicateLink, objectLink);
				stmtList.add(statementLink);
			}
		}
		return stmtList;
	}

	@Override
	public Statement convertTitle(Resource subject, String title) {
		logger.info("Creating triple : <" + subject.getURI() + ">  dc:title "
				+ title);
		Property predicate = new PropertyImpl(DC.NS, DC.PROP_TITLE);
		Node node = Node.createLiteral(title);
		RDFNode object = new LiteralImpl(node, null);
		Statement statement = new StatementImpl(subject, predicate, object);

		logger.info("Statement added");
		return statement;
	}

	@Override
	public Statement convertCreationDate(Resource subject, String date) {

		logger.info("Creating triple : <" + subject.getURI() + ">  dc:created "
				+ date);
		Property predicate = new PropertyImpl(DC.NS, DC.PROP_CREATED);
		Node node = Node.createLiteral(date, null, XSDDateType.XSDdateTime);
		RDFNode object = new LiteralImpl(node, null);
		Statement statement = new StatementImpl(subject, predicate, object);

		logger.info("Statement added");
		return statement;
	}

	@Override
	public Statement convertCreator(Resource subject, String author) {
		logger.info("Creating triple : <" + subject.getURI() + ">  dc:creator "
				+ author);
		Property predicate = new PropertyImpl(DC.NS, DC.PROP_CREATOR);
		Node node = Node.createLiteral(author);
		RDFNode object = new LiteralImpl(node, null);
		Statement statement = new StatementImpl(subject, predicate, object);

		logger.info("Statement added");
		return statement;
	}

}
