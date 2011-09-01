package ac.uk.soton.ecs.sw.semblogsvc;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ac.uk.soton.ecs.sw.semblogsvc.data.PostInfoBean;
import ac.uk.soton.ecs.sw.semblogsvc.service.ISimilarAuthorsService;
import ac.uk.soton.ecs.sw.semblogsvc.service.ISimilarPostsService;

@Controller
@RequestMapping("/similarAuthors/*")
public class SimilarAuthorsController {
	
	private static final Logger logger = Logger
			.getLogger(SimilarAuthorsController.class);

	@Autowired
	private ISimilarAuthorsService similarAuthorSvc;

	@RequestMapping(value = "param", method = RequestMethod.POST)
	public @ResponseBody
	String retrieveRelatedPosts(@RequestBody String searchUri) {

		PostInfoBean[] results = similarAuthorSvc.getSimilarPostsByAuthors(searchUri);
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
	PostInfoBean[] getSimilarPosts(@RequestBody String searchUri) {
		System.out.println("SimilarAuthorsController : similarAuthors/rest");
		PostInfoBean[] results = similarAuthorSvc.getSimilarPostsByAuthors(searchUri);
		System.out.println("Result length : " + results.length);
		return results;
	}
	
	@RequestMapping(value = "rankOptions", method = RequestMethod.POST)
	public @ResponseBody
	PostInfoBean[] getSimilarPostWithRanks(@RequestBody MultiValueMap<String, String> requestMap) {

		String searchUri = null; 
		String pageRankWeight = null;
		String dateWeight = null;
		String tagWeight = null;
		String linkWeight = null;
		
		logger.info("SimilarAuthorsController : similarAuthors/rankOptions");
		if(requestMap.containsKey("searchUrl")){
			searchUri = requestMap.get("searchUrl").get(0);
		}
		if(requestMap.containsKey("pageRankWeight")){
			pageRankWeight = requestMap.get("pageRankWeight").get(0);
		}
		
		if(requestMap.containsKey("dateWeight")){
			dateWeight = requestMap.get("dateWeight").get(0);
		}
		
		if(requestMap.containsKey("tagWeight")){
			tagWeight = requestMap.get("tagWeight").get(0);
		}
		if(requestMap.containsKey("linkWeight")){
			linkWeight = requestMap.get("linkWeight").get(0);
		}
		PostInfoBean[] results = similarAuthorSvc.getSimilarPostsByRankAndAuthors(searchUri, pageRankWeight, dateWeight, tagWeight, linkWeight);

		return results;
	}


}
