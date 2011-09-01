package ac.uk.soton.ecs.sw.semblogsvc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.api.IRdfStore;
import ac.uk.soton.ecs.sw.semblog.tstore.api.ITerm;
import ac.uk.soton.ecs.sw.semblog.tstore.common.PageLink;
import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IClusterSearcher;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IIndexSearcher;
import ac.uk.soton.ecs.sw.semblog.tstore.ranking.AbstractBlogPost;
import ac.uk.soton.ecs.sw.semblog.tstore.ranking.DefaultScoreCalculator;
import ac.uk.soton.ecs.sw.semblog.tstore.ranking.SemBlogPost;
import ac.uk.soton.ecs.sw.semblogsvc.data.PostInfoBean;

@Service
public class FindPostsBySimilarAuthorsSvc implements ISimilarAuthorsService {

	private static final Logger logger = Logger
			.getLogger(FindPostsBySimilarAuthorsSvc.class);

	@Autowired
	private IRelatedPostsService relatedPostsSvc;

	@Autowired
	private IClusterSearcher clusterSearcher;

	@Autowired
	private IRdfStore rdfStore;

	private ModelRDB dbModel = null;

	private PostInfoBean[] retrievePostsByAuthor(String webpageUri) {
		PostInfoBean[] similarPosts = null;

		try {

			logger.info("relatedPostsSvc.retrievePostsByAuthor");
			PostInfoBean[] relatedPosts = relatedPostsSvc
					.getRelatedPosts(webpageUri);
			List<AbstractBlogPost> blogPostList = new ArrayList<AbstractBlogPost>();
			// We are interested in only unique links, hence set ADT
			Set<ILink> linkSet = new HashSet<ILink>();

			for (PostInfoBean post : relatedPosts) {
				if (post.getAuthor() != null
						&& post.getAuthor() != SemblogConstants.NO_AUTHOR) {
					logger.info("Retrieve blog posts for author : "
							+ post.getAuthor());
					Set<ILink> links = getPostsByAuthor(post.getAuthor());
					logger.info("Set<ILink> links size : " + links.size());
					linkSet.addAll(links);
				}
			}

			if (linkSet.size() > 0) {
				Set<ILink> relatedSet = convertToILink(relatedPosts);
				// remove links that are already present in relatedPosts
				linkSet.removeAll(relatedSet);

				logger.info("found similar posts by tag : " + linkSet.size());

				Iterator<ILink> linkItr = linkSet.iterator();
				List<PostInfoBean> tempList = new ArrayList<PostInfoBean>();

				while (linkItr.hasNext()) {
					String url = linkItr.next().getUrlValue();
					/*
					 * PostInfoBean bean = relatedPostsSvc.getPostInfo(url);
					 * tempList.add(bean);
					 */
					AbstractBlogPost post = new SemBlogPost(url, webpageUri);
					blogPostList.add(post);

				}
				Collections.sort(blogPostList);
				if (blogPostList.size() > 10) {
					List<AbstractBlogPost> subList = blogPostList.subList(0, 9);
					for (AbstractBlogPost post : subList) {
						PostInfoBean bean = relatedPostsSvc.getPostInfo(post
								.getBlogUrl().getUrlValue(), post.getScore());
						tempList.add(bean);
					}
				} else {
					for (AbstractBlogPost post : blogPostList) {
						PostInfoBean bean = relatedPostsSvc.getPostInfo(post
								.getBlogUrl().getUrlValue(), post.getScore());
						tempList.add(bean);
					}
				}
				similarPosts = new PostInfoBean[tempList.size()];
				tempList.toArray(similarPosts);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return similarPosts;
	}

	private Set<ILink> convertToILink(PostInfoBean[] postInfoArr) {
		Set<ILink> links = new HashSet<ILink>();
		for (PostInfoBean bean : postInfoArr) {
			ILink link = new PageLink(bean.getRelatedUri());
			links.add(link);
		}
		return links;
	}

	@Override
	public PostInfoBean[] getSimilarPostsByAuthors(String uri) {
		PostInfoBean[] results = retrievePostsByAuthor(uri);
		return results;
	}

	@Override
	public PostInfoBean[] getSimilarPostsByRankAndAuthors(String uri,
			String pageRankWeight, String dateWeight,
			String tagWeight, String linkWeight) {
		if(pageRankWeight != null && 
				dateWeight != null && 
				tagWeight != null &&
				linkWeight != null){
		logger.info("pageRankWeight : " + pageRankWeight);
		DefaultScoreCalculator.getInstance().changeWeight(
				"pageRankFactor", Double.parseDouble(pageRankWeight));
		
		logger.info("dateWeight : " + dateWeight);
		DefaultScoreCalculator.getInstance().changeWeight(
				"dateScoreFactor", Double.parseDouble(dateWeight));
		
		logger.info("tagWeight : " + tagWeight);
		DefaultScoreCalculator.getInstance().changeWeight(
				"vectorDistanceScoreFactor", Double.parseDouble(tagWeight));
		
		logger.info("linkWeight : " + linkWeight);
		DefaultScoreCalculator.getInstance().changeWeight(
				"predicateScoreFactor", Double.parseDouble(linkWeight));		
		
		}
		PostInfoBean[] results = retrievePostsByAuthor(uri);
		return results;
	}

	private Set<ILink> getPostsByAuthor(String author) {
		Set<ILink> blogPostSet = new HashSet<ILink>();

		QueryExecution qexec = null;

		try {
			initDbModel();

			// Potentially expensive query.
			String sparqlQueryString = "SELECT ?s WHERE { ?s <http://purl.org/dc/terms/creator>  \""
					+ author + "\" } LIMIT 10";

			logger.info(sparqlQueryString);
			System.out.println("Query " + sparqlQueryString);
			Query query = QueryFactory.create(sparqlQueryString);
			qexec = QueryExecutionFactory.create(query, dbModel);

			logger.info("Executing query");
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				logger.info("Inside for loop");
				QuerySolution soln = results.nextSolution();
				// String urlLit = soln.getLiteral("s").getString() ;
				RDFNode n = soln.get("s"); // "s" is a variable in the query
				// If you need to test the thing returned
				String uri = null;
				if (n.isLiteral()) {
					uri = ((Literal) n).getLexicalForm();
					ILink link = new PageLink(uri);
					blogPostSet.add(link);
				}
				if (n.isResource()) {
					Resource r = (Resource) n;
					if (!r.isAnon()) {
						uri = r.getURI();
						ILink link = new PageLink(uri);
						blogPostSet.add(link);
					}
				}
			}
			qexec.close();

			dbModel.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return blogPostSet;
	}

	protected void initDbModel() {

		DBConnection connection = rdfStore.getDBConnection();
		dbModel = ModelRDB.open(connection, rdfStore.getModelName());

	}

}
