package ac.uk.soton.ecs.sw.semblog.tstore.ir.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import ac.uk.soton.ecs.sw.semblog.tstore.common.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;
import ac.uk.soton.ecs.sw.semblog.tstore.common.impl.BlogLink;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IIndexSearcher;

@Service
public class LuceneIndexSearcher implements IIndexSearcher {

	public List<ILink> searchTags(String[] searchTerms) {
		try {
			List<ILink> urlList = new ArrayList<ILink>();
			Directory dir = FSDirectory.open(new File(
					SemblogConstants.INDEX_DIRECTORY_PATH));
			IndexSearcher searcher = new IndexSearcher(dir);

			PhraseQuery query = new PhraseQuery();
			
			// if search terms has only one term, then 
			// find exact match, else find close match
			// i.e find match for n-1 words
			if(searchTerms.length == 1){
				query.setSlop(1);				
			}else{				
				query.setSlop(searchTerms.length - 1);
			}

			for (String word : searchTerms) {
				query.add(new Term("tag", word));
			}

			TopDocs matches = searcher.search(query, 10);
			for (ScoreDoc scoredoc : matches.scoreDocs) {
				Document doc = searcher.doc(scoredoc.doc);
				String url = doc.get("id");
				ILink link = new BlogLink(url);
				urlList.add(link);
			}
			return urlList;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public List<ILink> searchUrls(String searchUrl) {
		// TODO Auto-generated method stub
		return null;
	}

}
