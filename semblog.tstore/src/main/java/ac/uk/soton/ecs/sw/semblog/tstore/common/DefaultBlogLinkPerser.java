package ac.uk.soton.ecs.sw.semblog.tstore.common;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.ILink;
import ac.uk.soton.ecs.sw.semblog.tstore.api.ILinkParser;

@Component
public class DefaultBlogLinkPerser implements ILinkParser {

	private static final Logger logger = Logger
			.getLogger(DefaultBlogLinkPerser.class);
	
	private List<ILink> urlList = new ArrayList<ILink>();

	public List<ILink> getReferencedLinks() {
		return urlList;
	}

	public boolean parseContent(String content) {
		boolean status = true;
		ParserDelegator parserDelegator = new ParserDelegator();
		ParserCallback parserCallback = new ParserCallback() {
			public void handleText(final char[] data, final int pos) {
			}

			public void handleStartTag(Tag tag, MutableAttributeSet attribute,
					int pos) {
				if (tag == Tag.A) {
					String address = (String) attribute
							.getAttribute(Attribute.HREF);
					address = normalizeUrl(address);
					logger.info("Found link : " + address);
					PageLink link = new PageLink(address);
					urlList.add(link);
				}
			}

			public void handleEndTag(Tag t, final int pos) {
			}

			public void handleSimpleTag(Tag t, MutableAttributeSet a,
					final int pos) {
			}

			public void handleComment(final char[] data, final int pos) {
			}

			public void handleError(final java.lang.String errMsg, final int pos) {
			}
		};
		try {
			parserDelegator.parse(new StringReader(content), parserCallback,
					false);
		} catch (IOException e) {
			status = false;
			e.printStackTrace();
		}
		return status;
	}
	
	private String normalizeUrl(String url){
		return url.replaceAll("”", "");
	}

}
