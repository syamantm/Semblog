package ac.uk.soton.ecs.sw.semblog.tstore.impl.jena;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfRetriever;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStore;
import ac.uk.soton.ecs.sw.semblog.tstore.common.GraphDataNode;
import ac.uk.soton.ecs.sw.semblog.tstore.vocabulary.SEMBLOG;
import ac.uk.soton.ecs.sw.semblog.tstore.vocabulary.SIOC;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.ModelRDB;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

@Component
public class JenaRdfRetriever implements IRdfRetriever {

	private static final Logger logger = Logger
			.getLogger(JenaRdfRetriever.class);

	@Autowired
	private IRdfStore rdfStore;

	@Override
	public Date getCreationDate(ILink blog) {

		QueryExecution qexec = null;
		Date date = null;
		try {
			if (rdfStore == null) {
				logger.error("rdfStore is null !!");
				rdfStore = new JenaRdfStore();
			}
			DBConnection connection = rdfStore.getDBConnection();
			ModelRDB dbModel = ModelRDB.open(connection,
					rdfStore.getModelName());

			// Potentially expensive query.
			String sparqlQueryString = "SELECT ?date WHERE { <"
					+ blog.getUrlValue()
					+ "> <http://purl.org/dc/terms/created> ?date }";

			logger.info(sparqlQueryString);
			/* System.out.println("------"); */
			Query query = QueryFactory.create(sparqlQueryString);
			qexec = QueryExecutionFactory.create(query, dbModel);

			logger.info("Executing query");
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				logger.info("Inside for loop");
				QuerySolution soln = results.nextSolution();
				RDFNode n = soln.get("date"); // "date" is a variable in the
												// query
				// If you need to test the thing returned
				if (n.isLiteral()) {

					String stringDate = ((Literal) n).getLexicalForm();
					// 2011-07-12T00:41:37+01:00
					logger.info("Date Literal found : " + stringDate);
					try{
					Calendar cal = javax.xml.bind.DatatypeConverter
							.parseDateTime(stringDate);

					java.sql.Date sqltDate = new java.sql.Date(cal.getTime()
							.getTime());
					logger.info("Date parsed : " + sqltDate);
					date = sqltDate;
					}catch(IllegalArgumentException argumentException){
						SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
						java.util.Date parsedUtilDate = dateFormat.parse(stringDate);
						java.sql.Date sqltDate= new java.sql.Date(parsedUtilDate.getTime());
						date = sqltDate;
						logger.info("Date parsed : " + sqltDate);
					}
				}
				if (n.isResource()) {
					logger.info("Date Resource found");
					Resource r = (Resource) n;
					if (!r.isAnon()) {
						String strUri = r.getURI();

					}
				}
			}

			qexec.close();

			dbModel.close();
			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return date;
	}

	@Override
	public String getPredicate(ILink blog, ILink webpage) {
		QueryExecution qexec = null;
		String predicate = null;
		try {
			if (rdfStore == null) {
				logger.error("rdfStore is null !!");
				rdfStore = new JenaRdfStore();
			}
			DBConnection connection = rdfStore.getDBConnection();
			ModelRDB dbModel = ModelRDB.open(connection,
					rdfStore.getModelName());

			// Potentially expensive query.
			String sparqlQueryString = "SELECT ?p WHERE { <"
					+ blog.getUrlValue() + "> ?p " + "\""
					+ webpage.getUrlValue() + "\""
					+ "^^<http://www.w3.org/2001/XMLSchema#anyURI> }";

			logger.info(sparqlQueryString);
			/* System.out.println("------"); */
			Query query = QueryFactory.create(sparqlQueryString);
			qexec = QueryExecutionFactory.create(query, dbModel);

			logger.info("Executing query");
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				logger.info("Inside for loop");
				QuerySolution soln = results.nextSolution();
				RDFNode n = soln.get("p"); // "p" is a variable in the query
				// If you need to test the thing returned
				if (n.isLiteral()) {
					logger.info("Date Literal found");
					predicate = ((Literal) n).getLexicalForm();
					// date = Date.valueOf(dateStr);
				}
				if (n.isResource()) {
					logger.info("Date Resource found");
					Resource r = (Resource) n;
					if (!r.isAnon()) {
						predicate = r.getURI();

					}
				}
			}

			qexec.close();

			dbModel.close();
			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return predicate;
	}

	@Override
	public List<GraphDataNode> getGraphAsList() {
		
		List<GraphDataNode> graphData = new ArrayList<GraphDataNode>();
		QueryExecution qexec = null;
		String nodeId = null;
		try {
			if (rdfStore == null) {
				logger.error("rdfStore is null !!");
				rdfStore = new JenaRdfStore();
			}
			DBConnection connection = rdfStore.getDBConnection();
			ModelRDB dbModel = ModelRDB.open(connection,
					rdfStore.getModelName());

			// Potentially expensive query.
			String queryNodeId = "SELECT ?p ?nodeId WHERE { " + "?p <"
					+ SEMBLOG.NS + SEMBLOG.PROP_NODE_ID + "> ?nodeId }";

			logger.info(queryNodeId);
			/* System.out.println("------"); */
			Query query = QueryFactory.create(queryNodeId);
			qexec = QueryExecutionFactory.create(query, dbModel);

			logger.info("Executing query");
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				RDFNode n = soln.get("nodeId"); // "nodeId" is a variable in the
				String subjectUrl = null;								// query
				// If you need to test the thing returned
				if (n.isLiteral()) {
					logger.info("nodeId Literal found");
					nodeId = ((Literal) n).getLexicalForm();
					// date = Date.valueOf(dateStr);
				}
				if (n.isResource()) {
					logger.info("Date Resource found");
					Resource r = (Resource) n;
					if (!r.isAnon()) {
						nodeId = r.getURI();

					}
				}
				
				
				RDFNode subject = soln.get("p"); // "nodeId" is a variable in the
												// query
				// If you need to test the thing returned
				if (subject.isLiteral()) {
					logger.info("nodeId Literal found");
					subjectUrl = ((Literal) subject).getLexicalForm();
					// date = Date.valueOf(dateStr);
				}
				if (subject.isResource()) {
					logger.info("Date Resource found");
					Resource r = (Resource) subject;
					if (!r.isAnon()) {
						subjectUrl = r.getURI();

					}
				}
				GraphDataNode node = new GraphDataNode(UUID.fromString(nodeId));

				List<String> connectedGraph = getConnectedGraph(subjectUrl, nodeId, dbModel);
				for(String uid : connectedGraph){
					UUID connected = UUID.fromString(uid);
					node.addConnectedNode(connected);
				}
				graphData.add(node);
			}

			qexec.close();

			dbModel.close();
			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return graphData;
	}

	protected List<String> getConnectedGraph(String subjectUrl,String nodeId, ModelRDB dbModel) {
		
		 List<String> connectedGraph = new ArrayList<String>();
		// Potentially expensive query.
		String queryConnectedNodes = "SELECT ?topic ?connected  WHERE { <" + subjectUrl +"> "
				+ " <" + SIOC.NS + SIOC.PROP_LINKS_TO + "> ?connected }";

		logger.info(queryConnectedNodes);
		/* System.out.println("------"); */
		Query queryConnected = QueryFactory.create(queryConnectedNodes);
		QueryExecution qexecConnected = QueryExecutionFactory.create(
				queryConnected, dbModel);

		logger.info("Executing query");
		ResultSet resultConnectedNodes = qexecConnected.execSelect();
		for (; resultConnectedNodes.hasNext();) {
			String connectedUrl;
			QuerySolution solNodes = resultConnectedNodes.nextSolution();
			logger.info("Connected node found");
			// If you need to test the thing returned
			connectedUrl = getConnectedUrl(solNodes);
			
			if (connectedUrl != null) {
				String connectedId = getNodeId(connectedUrl, dbModel);
				if(connectedId != null){
					logger.info(nodeId + " connected to " + connectedId);
					connectedGraph.add(connectedId);
				}
			}
		}
		qexecConnected.close();
		return connectedGraph;
	}

	private String getConnectedUrl(QuerySolution solNodes) {
		String connectedUrl = null;
		RDFNode connected = solNodes.get("connected");
		if (connected != null) {
			if (connected.isLiteral()) {
				logger.info("connected Literal found");
				connectedUrl = ((Literal) connected).getLexicalForm();
				// date = Date.valueOf(dateStr);
			}
			if (connected.isResource()) {
				logger.info("connected Resource found");
				Resource r = (Resource) connected;
				if (!r.isAnon()) {
					connectedUrl = r.getURI();

				}
			}
		} else {
			RDFNode topic = solNodes.get("topic");
			if (topic != null) {
				if (topic.isLiteral()) {
					logger.info("topic Literal found");
					connectedUrl = ((Literal) topic).getLexicalForm();
					// date = Date.valueOf(dateStr);
				}
				if (topic.isResource()) {
					logger.info("topic Resource found");
					Resource r = (Resource) topic;
					if (!r.isAnon()) {
						connectedUrl = r.getURI();

					}
				}
			}
		}
		return connectedUrl;
	}

	public String getNodeId(String url, ModelRDB dbModel) {
		// Potentially expensive query.
		QueryExecution qexec = null;
		String nodeId = null;
		String queryNodeId = "SELECT ?nodeId WHERE { <" + url + "> <"
				+ SEMBLOG.NS + SEMBLOG.PROP_NODE_ID + "> ?nodeId }";

		logger.info(queryNodeId);
		/* System.out.println("------"); */
		Query query = QueryFactory.create(queryNodeId);
		qexec = QueryExecutionFactory.create(query, dbModel);

		logger.info("Executing query");
		ResultSet results = qexec.execSelect();

		for (; results.hasNext();) {
			QuerySolution soln = results.nextSolution();
			RDFNode n = soln.get("nodeId"); // "nodeId" is a variable in the
											// query
			// If you need to test the thing returned
			if (n.isLiteral()) {
				logger.info("nodeId Literal found");
				nodeId = ((Literal) n).getLexicalForm();
				// date = Date.valueOf(dateStr);
			}
			if (n.isResource()) {
				logger.info("nodeId Resource found");
				Resource r = (Resource) n;
				if (!r.isAnon()) {
					nodeId = r.getURI();

				}
			}
		}
		qexec.close();
		return nodeId;
	}

	@Override
	public boolean isResourceExists(ILink link) {
		boolean exists = true;
		try {
			if (rdfStore == null) {
				logger.error("rdfStore is null !!");
				rdfStore = new JenaRdfStore();
			}
			DBConnection connection = rdfStore.getDBConnection();
			ModelRDB dbModel = ModelRDB.open(connection,
					rdfStore.getModelName());
			
			String connectedId = getNodeId(link.getUrlValue(), dbModel);
			if(connectedId == null){
				exists = false;
			}

			dbModel.close();
			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return exists;
	}

	@Override
	public String getNodeUUID(ILink link) {
		String nodeId = null;
		try {
			if (rdfStore == null) {
				logger.error("rdfStore is null !!");
				rdfStore = new JenaRdfStore();
			}
			DBConnection connection = rdfStore.getDBConnection();
			ModelRDB dbModel = ModelRDB.open(connection,
					rdfStore.getModelName());
			
			nodeId = getNodeId(link.getUrlValue(), dbModel);			

			dbModel.close();
			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return nodeId;
	}

}
