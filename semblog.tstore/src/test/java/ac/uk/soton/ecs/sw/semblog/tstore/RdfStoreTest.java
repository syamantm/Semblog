package ac.uk.soton.ecs.sw.semblog.tstore;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ac.uk.soton.ecs.sw.semblog.tstore.impl.jena.JenaRdfPersister;
import ac.uk.soton.ecs.sw.semblog.tstore.impl.jena.JenaRdfStore;
import ac.uk.soton.ecs.sw.semblog.tstore.impl.jena.JenaRdfStoreManager;
import ac.uk.soton.ecs.sw.semblog.tstore.impl.jena.LocalFolderScanner;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.ModelRDB;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class RdfStoreTest {

	/*
	 * @Test public void test1_JenaRdfStore() { JenaRdfStore store = new
	 * JenaRdfStore(); assertTrue(store.createInitModel()); JenaRdfPersister
	 * persister = new JenaRdfPersister(); boolean status =
	 * persister.persistRdf(
	 * "file:/home/syamantak/Downloads/IPAS_Components.rdf", store);
	 * assertTrue(status); status = testJenaRdfQuery(store); assertTrue(status);
	 * }
	 */

	/*
	 * @Test public void test2_JenaRdfStoreManager(){ JenaRdfStore store = new
	 * JenaRdfStore(); store.createInitModel(); JenaRdfPersister persister = new
	 * JenaRdfPersister(); LocalFolderScanner urlScanner = new
	 * LocalFolderScanner(); List<String> rdfUrls = urlScanner.getRdfUrlList();
	 * for(String url : rdfUrls){ persister.persistRdf(url, store); } boolean
	 * status = testJenaRdfStoreManagerQuery(store); assertTrue(status); }
	 */
	
	@Test
	public void test3_fullWorkflow() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/appContext.xml");
		BeanFactory factory = context;
		JenaRdfStoreManager storeMgr = (JenaRdfStoreManager) factory
				.getBean("jenaRdfStoreManager");
		boolean status = storeMgr.run();
		assertTrue(status);
	}

	@Test
	public void test4_query() {
		JenaRdfStore store = new JenaRdfStore();
		boolean status = testQueryByPredicate(store);
		assertTrue(status);

		status = testQueryByObject(store);
		assertTrue(status);
	}

	public static boolean testJenaRdfQuery(JenaRdfStore store) {

		boolean status = true;
		DBConnection connection = null;
		QueryExecution qexec = null;
		try {
			// The database backend initialization.
			System.out.println("Creating db connection");
			connection = store.getDBConnection();

			// Get hold of the existing wordnet model
			ModelRDB model = ModelRDB.open(connection, store.getModelName());
			System.out.println("Model opened db connection : "
					+ store.getModelName());
			// Potentially expensive query.
			String sparqlQueryString = "SELECT  ?p ?o WHERE { <http://localhost/drupal-7.4/?q=node/3> ?p ?o }";

			// String sparqlQueryString =
			// "SELECT ?s ?p ?o WHERE { ?s ?p <http://www.3worlds.org/v4.2/IPAS_components#Part> }";
			// See http://www.openjena.org/ARQ/app_api.html

			System.out.println("Creating query");
			System.out.println("------");
			System.out.println(sparqlQueryString);
			System.out.println("------");
			Query query = QueryFactory.create(sparqlQueryString);
			qexec = QueryExecutionFactory.create(query, model);

			System.out.println("Executing query");
			ResultSet results = qexec.execSelect();
			System.out.println("Generating results");
			ResultSetFormatter.out(results);
			qexec.close();
			model.close();
			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			status = false;
		}
		return status;

	}

	public static boolean testQueryByPredicate(JenaRdfStore store) {

		boolean status = true;
		DBConnection connection = null;
		QueryExecution qexec = null;
		try {
			// The database backend initialization.
			System.out.println("Creating db connection");
			connection = store.getDBConnection();

			// Get hold of the existing wordnet model
			ModelRDB model = ModelRDB.open(connection, store.getModelName());
			System.out.println("Model opened db connection : "
					+ store.getModelName());
			// Potentially expensive query.
			String sparqlQueryString = "SELECT   ?o WHERE { <http://localhost/drupal-7.4/?q=node/3> <http://rdfs.org/sioc/ns#links_to> ?o }";
			// String sparqlQueryString = "SELECT ?s ?o WHERE { ?s ?p ?o }";
			// See http://www.openjena.org/ARQ/app_api.html

			System.out.println("Creating query");
			System.out.println("------");
			System.out.println(sparqlQueryString);
			System.out.println("------");
			Query query = QueryFactory.create(sparqlQueryString);
			qexec = QueryExecutionFactory.create(query, model);

			System.out.println("Executing query");
			ResultSet results = qexec.execSelect();
			System.out.println("Generating results");
			ResultSetFormatter.out(results);
			qexec.close();
			model.close();
			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			status = false;
		}
		return status;

	}

	public static boolean testQueryByObject(JenaRdfStore store) {

		boolean status = true;
		DBConnection connection = null;
		QueryExecution qexec = null;
		try {
			// The database backend initialization.
			System.out.println("Creating db connection");
			connection = store.getDBConnection();

			// Get hold of the existing wordnet model
			ModelRDB model = ModelRDB.open(connection, store.getModelName());
			System.out.println("Model opened db connection : "
					+ store.getModelName());
			// Potentially expensive query.
			// Potentially expensive query.
			String sparqlQueryString = "SELECT ?s ?p WHERE { ?s ?p " + "\""
					+ "http://www.w3.org/2001/sw/Europe/" + "\""
					+ "^^<http://www.w3.org/2001/XMLSchema#anyURI> }";

			// String sparqlQueryString = "SELECT ?s ?o WHERE { ?s ?p ?o }";
			// See http://www.openjena.org/ARQ/app_api.html

			System.out.println("Creating query");
			System.out.println("------");
			System.out.println(sparqlQueryString);
			System.out.println("------");
			Query query = QueryFactory.create(sparqlQueryString);
			qexec = QueryExecutionFactory.create(query, model);

			System.out.println("Executing query");
			ResultSet results = qexec.execSelect();
			System.out.println("Generating results");
			ResultSetFormatter.out(results);
			qexec.close();
			model.close();
			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			status = false;
		}
		return status;

	}

}
