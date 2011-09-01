package ac.uk.soton.ecs.sw.semblog.tstore.impl.html;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.api.ILinkParser;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfPersister;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStore;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IStatementConverter;
import ac.uk.soton.ecs.sw.semblog.tstore.api.ITerm;
import ac.uk.soton.ecs.sw.semblog.tstore.common.AbstractRdfPersister;
import ac.uk.soton.ecs.sw.semblog.tstore.common.PageLink;
import ac.uk.soton.ecs.sw.semblog.tstore.common.TagTerm;
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
public class SimpleHtmlBlogPersister extends AbstractRdfPersister {

	private static final Logger logger = Logger
			.getLogger(SimpleHtmlBlogPersister.class);

	private HtmlCleaner cleaner = new HtmlCleaner();

	@Autowired
	private IStatementConverter<ILink> stmtConverter;

	public boolean persistRdfImpl(String url, IRdfStore rdfStore) {
		logger.info("Persisting : " + url);
		boolean status = true;
		DBConnection connection = null;
		ModelRDB model = null;
		try {
			// The database backend initialization.
			logger.info("Creating db connection");
			connection = rdfStore.getDBConnection();

			// Get hold of the existing wordnet model
			model = ModelRDB.open(connection, rdfStore.getModelName());
			logger.info("Model opened db connection");

			harvestLinksFromContent(url, model);

			logger.info("Data added to model");
			connection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			status = false;
		} finally {
			if (!model.isClosed()) {
				model.close();
			}
		}
		return status;

	}

	private void harvestLinksFromContent(String url, Model linkedDataModel)
			throws MalformedURLException, IOException {
		logger.info("----- Reading Content Begin ----- ");
		TagNode rootNode = cleaner.clean(new URL(url));
		List<ILink> links = new ArrayList<ILink>();
		TagNode[] tagArray = rootNode.getElementsByName("a", true);
		logger.info("link count : " + tagArray.length);
		for (TagNode node : tagArray) {
			String href = node.getAttributeByName("href");
			logger.info("found link : " + href);
			if (href.startsWith("http")) {
				ILink link = new PageLink(href);
				links.add(link);
				Resource subject = new ResourceImpl(url);
				List<Statement> statements = stmtConverter.convertLinks(subject,
						links);
				linkedDataModel.add(statements);
			}
		}
		linkedDataModel.commit();
		logger.info("----- Reading Content End ----- ");
	}

}
