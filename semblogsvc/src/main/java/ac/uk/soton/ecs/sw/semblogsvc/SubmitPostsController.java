package ac.uk.soton.ecs.sw.semblogsvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ac.uk.soton.ecs.sw.semblogsvc.service.INewPostsService;
import ac.uk.soton.ecs.sw.semblogsvc.service.SubmitNewPostsSvc;

@Controller
@RequestMapping("/submitPosts/*")
public class SubmitPostsController {
	
	@Autowired
	INewPostsService newPostsSvc;

	@RequestMapping(value="rest", method=RequestMethod.PUT)
	public @ResponseBody String getRelatedPosts(@RequestBody String newUri) {
		
		boolean status = newPostsSvc.submitNewPost(newUri);		
		
		if(status){
			return "URl '"+ newUri +"' submitted. Data will be collected automatically" ;		
		}else{
			return "Server error. please contact the admin";
		}
		
	}	

}
