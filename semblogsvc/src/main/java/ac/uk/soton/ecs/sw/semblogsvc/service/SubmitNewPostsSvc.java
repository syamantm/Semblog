package ac.uk.soton.ecs.sw.semblogsvc.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;

@Service
public class SubmitNewPostsSvc implements INewPostsService{
	
	private static final Logger logger = Logger.getLogger(SubmitNewPostsSvc.class);

	
	@Override
	public boolean submitNewPost(String newUrl) {

		logger.info("New Uri : " + newUrl);
		Date date= new java.util.Date();		
		//Timestamp timeStamp = new Timestamp(date.getTime());
		String fileName = getQualifiedFilename(date);
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		    out.write(newUrl);
		    out.close();
		} catch (IOException e) {
		}
		return true;
	}
	
	
	public String getQualifiedFilename(Date timeStamp){
		String fileName = SemblogConstants.HOT_FOLDER_PATH + "/"+ 
							SemblogConstants.NEW_FILE_PREFIX + "." + 
							timeStamp.getTime() + "." + 
							SemblogConstants.NEW_FILE_SUFFIX;
		return fileName;
	}

	


}
