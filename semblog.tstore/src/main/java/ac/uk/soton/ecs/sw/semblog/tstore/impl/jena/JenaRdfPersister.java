package ac.uk.soton.ecs.sw.semblog.tstore.impl.jena;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfPersister;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStore;
import ac.uk.soton.ecs.sw.semblog.tstore.common.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.common.ILinkParser;
import ac.uk.soton.ecs.sw.semblog.tstore.common.IStatementConverter;
import ac.uk.soton.ecs.sw.semblog.tstore.common.ITerm;
import ac.uk.soton.ecs.sw.semblog.tstore.common.impl.TagTerm;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IDocument;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IIndexCreator;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.impl.BlogDocument;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.ModelRDB;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.util.FileManager;

@Component
public class JenaRdfPersister implements IRdfPersister {

	private static final Logger logger = Logger
			.getLogger(JenaRdfPersister.class);

	@Autowired
	private ILinkParser linkParser;

	@Autowired
	private IStatementConverter<ILink> stmtConverter;

	@Autowired
	private IIndexCreator idxCreator;

	public boolean persistRdf(String url, IRdfStore rdfStore) {
		logger.info("Persisting : " + url);
		boolean status = true;
		DBConnection connection = null;
		Model linkedDataModel = null;
		ModelRDB model = null;
		try {
			// The database backend initialization.
			logger.info("Creating db connection");
			connection = rdfStore.getDBConnection();

			// Get hold of the existing wordnet model
			model = ModelRDB.open(connection, rdfStore.getModelName());
			logger.info("Model opened db connection");

			FileManager fManager = FileManager.get();
			fManager.addLocatorURL();
			linkedDataModel = fManager.loadModel(url);

			// harvest links from the blog content
			// and store them in in the model as rdf triples
			harvestLinksFromContent(url, linkedDataModel);

			// harvest tags from the blog and index them
			harvestTags(url, linkedDataModel);

			model.add(linkedDataModel);
			logger.info("Data added to model");
			connection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			status = false;
		} finally {
			model.close();
			linkedDataModel.close();
		}
		return status;

	}

	private void harvestLinksFromContent(String url, Model linkedDataModel) {
		logger.info("----- Reading Content Begin ----- ");
		// get the content from rdf
		Property propContent = new PropertyImpl(
				"http://purl.org/rss/1.0/modules/content/", "encoded");
		NodeIterator iterator = linkedDataModel
				.listObjectsOfProperty(propContent);

		while (iterator.hasNext()) {
			// get the content as literal
			RDFNode node = iterator.next();
			if (node.isLiteral()) {
				String content = ((Literal) node).getLexicalForm();
				// extracts links from the content.
				// create rdf triples for the links
				// add them to the model
				if (linkParser.parseContent(content)) {
					List<ILink> links = linkParser.getReferencedLinks();
					Resource subject = new ResourceImpl(url);
					List<Statement> statements = stmtConverter.convert(subject,
							links);
					linkedDataModel.add(statements);
				}
			}
		}
		logger.info("----- Reading Content End ----- ");
	}

	private void harvestTags(String url, Model linkedDataModel) {
		logger.info("----- Index blog Begin ----- ");
		
		Property propTitle = new PropertyImpl("http://purl.org/dc/terms/",
				"title");
		NodeIterator itrTitle = linkedDataModel
				.listObjectsOfProperty(propTitle);		
		String title = null;
		// extract the tags one by one
		while (itrTitle.hasNext()) {
			// get the content as literal
			
			RDFNode node = itrTitle.next();
			if (node.isLiteral()) {
				title = ((Literal) node).getLexicalForm();
				logger.info("Found Title : " + title);
			}
		}
			
		// get the content from rdf
		Property propTags = new PropertyImpl("http://purl.org/dc/terms/",
				"subject");
		NodeIterator iterator = linkedDataModel
				.listObjectsOfProperty(propTags);
		List<ITerm> termList = new ArrayList<ITerm>();

		// extract the tags one by one
		while (iterator.hasNext()) {
			// get the content as literal
			String tag = null;
			RDFNode node = iterator.next();
			if (node.isLiteral()) {
				tag = ((Literal) node).getLexicalForm();
				logger.info("Found TAG : " + tag);
			} else if (node.isResource()) {
				Resource res = node.asResource();
				String tagUrl = res.getURI();
				if(!tagUrl.endsWith(".rdf")){
					tagUrl = tagUrl + ".rdf";
				}
				FileManager fManager = FileManager.get();
				fManager.addLocatorURL();
				Model tagDataModel = fManager.loadModel(tagUrl);
				Property tagProp = new PropertyImpl(
						"http://www.w3.org/2004/02/skos/core#", "prefLabel");
				NodeIterator tagIterator = tagDataModel
						.listObjectsOfProperty(tagProp);
				while (tagIterator.hasNext()) {
					RDFNode tagNode = tagIterator.next();
					if (tagNode.isLiteral()) {
						tag = ((Literal) tagNode).getLexicalForm();
						logger.info("Found TAG : " + tag);
					}
				}
			}
			if (tag != null) {
				ITerm term = new TagTerm(tag);
				termList.add(term);
			}
		}

		IDocument doc = new BlogDocument(url, title, termList);
		idxCreator.createindex(doc);
		logger.info("----- Index blog End ----- ");
	}

}
