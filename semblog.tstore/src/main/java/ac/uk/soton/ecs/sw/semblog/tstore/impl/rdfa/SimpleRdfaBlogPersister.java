package ac.uk.soton.ecs.sw.semblog.tstore.impl.rdfa;

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
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfRetriever;
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
public class SimpleRdfaBlogPersister extends AbstractRdfPersister {

	private static final Logger logger = Logger
			.getLogger(SimpleRdfaBlogPersister.class);

	private HtmlCleaner cleaner = new HtmlCleaner();

	private final String[] RDFA_ATTRIBUTES = { "rel", "rev", "content",
			"about", "property", "typeof" };

	@Autowired
	private IStatementConverter<ILink> stmtConverter;

	@Autowired
	private ILinkParser linkParser;

	@Autowired
	private IRdfRetriever rdfRetriever;

	private TagNode rootNode;
	private String rootUrl;

	public boolean persistRdf(String url, IRdfStore rdfStore) {
		logger.info("Persisting : " + url);
		boolean status = true;
		DBConnection connection = null;
		ModelRDB model = null;
		try {
			rootUrl = url;
			// The database backend initialization.
			logger.info("Creating db connection");
			connection = rdfStore.getDBConnection();

			// Get hold of the existing wordnet model
			model = ModelRDB.open(connection, rdfStore.getModelName());
			logger.info("Model opened db connection");

			rootNode = cleaner.clean(new URL(rootUrl));
			String content = getContent();
			if (!isSpamPost(content)) {
				harvestLinksFromContent(model);

				harvestRdfaAttrib(model);
				if (!rdfRetriever.isResourceExists(new PageLink(url))) {
					addNodeUUID(url, model);
				}
			}

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

	private String getContent() {
		StringBuilder content = new StringBuilder();
		logger.info("Looking for content");
		List<TagNode> tagList = rootNode.getElementListByAttValue("property",
				"content:encoded", true, true);
		logger.info("tag count for property content:encoded : "
				+ tagList.size());
		for (TagNode node : tagList) {
			String data = node.getText().toString();
			content.append(data);
		}
		return content.toString();
	}

	protected void harvestLinksFromContent(Model linkedDataModel)
			throws MalformedURLException, IOException {
		logger.info("----- Reading Content Begin ----- ");

		List<ILink> links = new ArrayList<ILink>();
		TagNode[] tagArray = rootNode.getElementsByName("a", true);
		logger.info("link count : " + tagArray.length);
		for (TagNode node : tagArray) {
			String href = node.getAttributeByName("href");
			// logger.info("found link : " + href);
			if (href.startsWith("http")) {
				ILink link = new PageLink(href);
				links.add(link);
				Resource subject = new ResourceImpl(rootUrl);
				List<Statement> statements = stmtConverter.convertLinks(
						subject, links);
				linkedDataModel.add(statements);
			}
		}
		linkedDataModel.commit();
		logger.info("----- Reading Content End ----- ");
	}

	protected void harvestRdfaAttrib(Model linkedDataModel) {
		harvestTitle(linkedDataModel);
		harvestCreatedDate(linkedDataModel);
		harvestCreator(linkedDataModel);
	}

	protected void harvestTitle(Model linkedDataModel) {
		logger.info("Looking for Title");
		List<TagNode> tagList = rootNode.getElementListByAttValue("property",
				"dc:title", true, true);
		logger.info("tag count for property dc:title : " + tagList.size());
		for (TagNode node : tagList) {
			String title = node.getText().toString();
			logger.info("Title : " + title);
			Resource subject = new ResourceImpl(rootUrl);
			Statement stmt = stmtConverter.convertTitle(subject, title);
			linkedDataModel.add(stmt);
		}
	}

	protected void harvestCreatedDate(Model linkedDataModel) {
		logger.info("Looking for Date");
		List<TagNode> tagList = rootNode.getElementListByAttValue("property",
				"dc:created", true, true);
		logger.info("tag count for property dc:created : " + tagList.size());
		for (TagNode node : tagList) {
			String date = node.getText().toString();
			logger.info("Date : " + date);
			Resource subject = new ResourceImpl(rootUrl);
			Statement stmt = stmtConverter.convertCreationDate(subject, date);
			linkedDataModel.add(stmt);
		}
	}

	protected void harvestCreator(Model linkedDataModel) {
		logger.info("Looking for Author");
		List<TagNode> tagList = rootNode.getElementListByAttValue("property",
				"dc:creator", true, true);
		logger.info("tag count for property dc:creator : " + tagList.size());
		for (TagNode node : tagList) {
			String author = node.getText().toString();
			logger.info("Creator : " + author);
			Resource subject = new ResourceImpl(rootUrl);
			Statement stmt = stmtConverter.convertCreator(subject, author);
			linkedDataModel.add(stmt);
		}
	}

}
