package ac.uk.soton.ecs.sw.semblogsvc;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ac.uk.soton.ecs.sw.semblog.tstore.common.ITerm;
import ac.uk.soton.ecs.sw.semblog.tstore.ir.IIndexSearcher;
import ac.uk.soton.ecs.sw.semblogsvc.data.PostInfoBean;
import ac.uk.soton.ecs.sw.semblogsvc.service.ISimilarPostsService;

@Controller
@RequestMapping("/similarPosts/*")
public class SimilarPostsController {

	@Autowired
	private ISimilarPostsService similarPostsSvc;

	@RequestMapping(value = "param", method = RequestMethod.POST)
	public @ResponseBody
	String retrieveRelatedPosts(@RequestBody String searchUri) {

		PostInfoBean[] results = similarPostsSvc.getSimilarPosts(searchUri);
		StringBuilder builder = new StringBuilder();
		if (results.length > 0) {
			for (PostInfoBean res : results) {
				builder.append("<p><h3><a href=\"" + res.getRelatedUri()
						+ "\">" + res.getTitle() + "</a></h3>");
				builder.append("Url for this posts is :");
				builder.append("<br> <a href=\"" + res.getRelatedUri() + "\">"
						+ res.getRelatedUri() + "</a></p><br>");
			}
		} else {
			builder.append("<p><h3>No Result found</h3></p>");
		}
		return builder.toString();

	}

	@RequestMapping(value = "rest", method = RequestMethod.POST)
	public @ResponseBody
	PostInfoBean[] getRelatedPosts(@RequestBody String searchUri) {
		System.out.println("SimilarPostsController : similarPosts/rest");
		PostInfoBean[] results = similarPostsSvc.getSimilarPosts(searchUri);
		System.out.println("Result length : " + results.length);
		return results;
	}

}
