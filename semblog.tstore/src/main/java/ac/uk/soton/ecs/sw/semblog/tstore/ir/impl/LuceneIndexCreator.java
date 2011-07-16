package ac.uk.soton.ecs.sw.semblog.tstore.ir.impl;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import ac.uk.soton.ecs.sw.semblog.tstore.common.ITerm;
import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IDocument;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IIndexCreator;

@Service
public class LuceneIndexCreator implements IIndexCreator {

	public void createindex(IDocument doc) {
		try {
			Directory directory = FSDirectory.open(new File(
					SemblogConstants.INDEX_DIRECTORY_PATH));
			Analyzer analyzer = new StandardAnalyzer(
					SemblogConstants.LUCENE_VERSION);

			IndexWriterConfig config = new IndexWriterConfig(
					SemblogConstants.LUCENE_VERSION, analyzer);

			IndexWriter writer = new IndexWriter(directory, config);

			Document document = new Document();
			document.add(new Field("id", doc.getDocId(), Field.Store.YES,
					Field.Index.ANALYZED, TermVector.NO));
			document.add(new Field("title", doc.getTitle(), Field.Store.YES,
					Field.Index.ANALYZED, TermVector.NO));
			for (ITerm term : doc.getTermList()) {
				document.add(new Field("tag",
						term.getTermValue(), Field.Store.YES,
						Field.Index.ANALYZED, TermVector.YES));
			}
			writer.addDocument(document);
			writer.commit();
			writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

}
