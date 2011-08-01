package ac.uk.soton.ecs.sw.semblog.tstore.impl.jena;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfRetriever;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStore;
import ac.uk.soton.ecs.sw.semblog.tstore.common.ILink;

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
			if(rdfStore == null){
				logger.error("rdfStore is null !!");
				rdfStore = new JenaRdfStore();
			}
			DBConnection connection = rdfStore.getDBConnection();
			ModelRDB dbModel = ModelRDB.open(connection, rdfStore.getModelName());

			// Potentially expensive query.
			String sparqlQueryString = "SELECT ?date WHERE { <"
					+ blog.getUrlValue() + "> <http://purl.org/dc/terms/created> ?date }";

			logger.info(sparqlQueryString);
			/* System.out.println("------"); */
			Query query = QueryFactory.create(sparqlQueryString);
			qexec = QueryExecutionFactory.create(query, dbModel);

			logger.info("Executing query");
			ResultSet results = qexec.execSelect();

			
			for (; results.hasNext();) {
				logger.info("Inside for loop");
				QuerySolution soln = results.nextSolution();				
				RDFNode n = soln.get("date"); // "date" is a variable in the query
				// If you need to test the thing returned
				if (n.isLiteral()){
					
					String stringDate = ((Literal) n).getLexicalForm();
					//2011-07-12T00:41:37+01:00
					logger.info("Date Literal found : " + stringDate);
					Calendar cal = javax.xml.bind.DatatypeConverter.parseDateTime(stringDate);
					
					java.sql.Date sqltDate= new java.sql.Date(cal.getTime().getTime());  
					logger.info("Date parsed : " + sqltDate);
					date = sqltDate;
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
			if(rdfStore == null){
				logger.error("rdfStore is null !!");
				rdfStore = new JenaRdfStore();
			}
			DBConnection connection = rdfStore.getDBConnection();
			ModelRDB dbModel = ModelRDB.open(connection, rdfStore.getModelName());

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
				if (n.isLiteral()){
					logger.info("Date Literal found");
					predicate = ((Literal) n).getLexicalForm();
					//date = Date.valueOf(dateStr);
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

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return predicate;
	}

}
