package ac.uk.soton.ecs.sw.semblog.tstore.ir.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.api.ITerm;
import ac.uk.soton.ecs.sw.semblog.tstore.common.PageLink;
import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;
import ac.uk.soton.ecs.sw.semblog.tstore.common.TagTerm;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IIndexSearcher;

@Service
public class LuceneIndexSearcher implements IIndexSearcher {

	public List<ILink> searchTags(String searchTerm) {
		try {
			System.out.println("LuceneIndexSearcher.searchTags");
			List<ILink> urlList = new ArrayList<ILink>();
			Directory dir = FSDirectory.open(new File(
					SemblogConstants.INDEX_DIRECTORY_PATH));
			IndexSearcher searcher = new IndexSearcher(dir);
			Term t = new Term("tag", searchTerm);
			Query query = new TermQuery(t);		
			
			TopDocs matches = searcher.search(query, 10);
			if(matches.totalHits > 0){
				System.out.println("LuceneIndexSearcher.searchTags found hits");
			}else{
				System.out.println("LuceneIndexSearcher.searchTags no hits");
			}
			for (ScoreDoc scoredoc : matches.scoreDocs) {
				Document doc = searcher.doc(scoredoc.doc);
				String url = doc.get("id");
				ILink link = new PageLink(url);
				urlList.add(link);
			}
			searcher.close();
			return urlList;
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public List<ITerm> searchUrls(String searchUrl) {
		try {
			List<ITerm> tagList = new ArrayList<ITerm>();
			Directory dir = FSDirectory.open(new File(
					SemblogConstants.INDEX_DIRECTORY_PATH));
			IndexSearcher searcher = new IndexSearcher(dir);

			Term t = new Term("id", searchUrl);
			Query query = new TermQuery(t);

			// retrieve only the top most document
			TopDocs matches = searcher.search(query, 1);
			if(matches.totalHits > 0 ){
				System.out.println("LuceneIndexSearcher : Document Found in the index");
			}
			for (ScoreDoc scoredoc : matches.scoreDocs) {
				Document doc = searcher.doc(scoredoc.doc);
				String[] tags = doc.getValues("tag");
				for (String tag : tags) {
					System.out.println("LuceneIndexSearcher : tag : " + tag);
					ITerm term = new TagTerm(tag);
					tagList.add(term);
				}

			}
			searcher.close();
			return tagList;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
