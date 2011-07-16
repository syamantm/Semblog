package ac.uk.soton.ecs.sw.semblogsvc.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStore;
import ac.uk.soton.ecs.sw.semblogsvc.data.RelatedPostsBean;
import ac.uk.soton.ecs.sw.semblogsvc.dbl.IDbStore;
import ac.uk.soton.ecs.sw.semblogsvc.dbl.JenaDbStore;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.ModelRDB;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

@Service
public class FindRelatedPostsSvc implements IRelatedPostsService{
	
	private static final Logger logger = Logger.getLogger(FindRelatedPostsSvc.class);	
	
	@Autowired
	private IRdfStore rdfStore;

	
	@Override
	public RelatedPostsBean[] getRelatedPosts(String uri) {		
	
		ModelRDB dbModel = null;
		QueryExecution qexec = null;
		List<RelatedPostsBean> resultStrings = null;
		RelatedPostsBean[] relatedPosts = null;
		try {
			DBConnection connection = rdfStore.getDBConnection();
			dbModel = ModelRDB.open(connection, rdfStore.getModelName());			

			// Potentially expensive query.
			String sparqlQueryString = "SELECT ?s ?p WHERE { ?s ?p " + "\""
					+ uri + "\""
					+ "^^<http://www.w3.org/2001/XMLSchema#anyURI> }";
			
			logger.info(sparqlQueryString);
			/*System.out.println("------");*/
			Query query = QueryFactory.create(sparqlQueryString);
			qexec = QueryExecutionFactory.create(query, dbModel);

			logger.info("Executing query");
			ResultSet results = qexec.execSelect();
			
			resultStrings = new ArrayList<RelatedPostsBean>();
			/*System.out.println("Generating results as Resource");*/
			for (; results.hasNext();) {
				logger.info("Inside for loop");
				QuerySolution soln = results.nextSolution();
				// String urlLit = soln.getLiteral("s").getString() ;
				RDFNode n = soln.get("s"); // "s" is a variable in the query
				// If you need to test the thing returned
				if (n.isLiteral())
					((Literal) n).getLexicalForm();
				if (n.isResource()) {
					Resource r = (Resource) n;
					if (!r.isAnon()) {
						String strUri = r.getURI();
						String sparqlTitleString = "SELECT ?title WHERE { <" + strUri +"> <http://purl.org/dc/terms/title> ?title }";
						Query queryT = QueryFactory.create(sparqlTitleString);
						qexec = QueryExecutionFactory.create(queryT, dbModel);
						String title = null;
						logger.info(sparqlTitleString);
						ResultSet resultsT = qexec.execSelect();
						for (; resultsT.hasNext();) {
							/*System.out.println("Title found");*/
							QuerySolution solnT = resultsT.nextSolution();
							// String urlLit = soln.getLiteral("s").getString() ;
							RDFNode nT = solnT.get("title"); // "s" is a variable in the query
							// If you need to test the thing returned
							if (nT.isLiteral()){
							title = ((Literal) nT).getLexicalForm();
							
							}
						}
						resultStrings.add(new RelatedPostsBean(strUri, title));
						logger.info("Found related URI : " + strUri + " With Title : " + title);
						
					}
				}
			}		
			relatedPosts = new RelatedPostsBean[resultStrings.size()];
			resultStrings.toArray(relatedPosts);

			qexec.close();
			
			dbModel.close();
			

		} catch (Exception ex) {
			ex.printStackTrace();			
		}
		return relatedPosts;
	}


}
