package ac.uk.soton.ecs.sw.semblog.tstore.impl.jena;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import ac.uk.soton.ecs.sw.semblog.tstore.api.IUrlScanner;
import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;

@Component
public class LocalFolderScanner implements IUrlScanner {

	private static final Logger logger = Logger
			.getLogger(LocalFolderScanner.class);

	
	
	private List<String> urlList = new ArrayList<String>();
	

	public void createUrlListFromFile() {

		// get the silt of files in hot folder.
		File hotFolder = new File(SemblogConstants.HOT_FOLDER_PATH);
		File listFiles[] = hotFolder.listFiles();

		for (int i = 0; i < listFiles.length; i++) {
			File file = listFiles[i];
			logger.info("Reading file : " + file.getName());			
			try {
				//
				// Create a new Scanner object to read the file
				Scanner scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					urlList.add(line);
					logger.info("Adding url : " + line + " to list");
				}
				
				// Reading is finished. move to Destination directory, i.e 'cold' folder 
			    File dir = new File(SemblogConstants.COLD_FOLDER_PATH);
			    
			    // Move file to new directory
			    boolean success = file.renameTo(new File(dir, file.getName()));
			    if (!success) {
			    	logger.error("Failed to move file : " + file.getName() + " to cold folder");
			    }
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

	public List<String> getRdfUrlList() {
		createUrlListFromFile();
		return urlList;
	}

}
