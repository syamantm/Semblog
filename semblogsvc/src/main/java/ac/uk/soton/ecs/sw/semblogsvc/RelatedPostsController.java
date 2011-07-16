package ac.uk.soton.ecs.sw.semblogsvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ac.uk.soton.ecs.sw.semblogsvc.data.RelatedPostsBean;
import ac.uk.soton.ecs.sw.semblogsvc.service.FindRelatedPostsSvc;
import ac.uk.soton.ecs.sw.semblogsvc.service.IRelatedPostsService;

@Controller
@RequestMapping("/relatedPosts/*")
public class RelatedPostsController {

	@Autowired
	private IRelatedPostsService relatedPostsSvc; 
	
	@RequestMapping(value="param", method=RequestMethod.POST)
	public @ResponseBody String retrieveRelatedPosts(@RequestBody String searchUri) {
		
		RelatedPostsBean[] results = relatedPostsSvc.getRelatedPosts(searchUri);
		StringBuilder builder = new StringBuilder();
		for(RelatedPostsBean res : results){
			builder.append("<p><h3><a href=\"" + res.getRelatedUri() + "\">" + res.getTitle() +"</a></h3>");
			builder.append("Url for this posts is :");
			builder.append("<br> <a href=\"" + res.getRelatedUri() + "\">" + res.getRelatedUri() +"</a></p><br>");
		}
		return builder.toString();
				
	}	

	@RequestMapping(value="rest", method=RequestMethod.POST)
	public @ResponseBody RelatedPostsBean[] getRelatedPosts(@RequestBody String searchUri) {
		
		RelatedPostsBean[] results = relatedPostsSvc.getRelatedPosts(searchUri);
		
		
		return results;		
	}	

}
