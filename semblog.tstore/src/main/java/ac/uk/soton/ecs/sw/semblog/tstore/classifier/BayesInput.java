package ac.uk.soton.ecs.sw.semblog.tstore.classifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;

public class BayesInput {
	private String fileAbsoluteName;
	
	private String fileName;
	

	public void prepareInput(String content) {

		Date date = new java.util.Date();
		// Timestamp timeStamp = new Timestamp(date.getTime());
		String spam = "spam.post";
		createInputFile(content, date, spam);
	}

	private void createInputFile(String content, Date date, String spam) {
		fileAbsoluteName = getQualifiedFilename(date, spam);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileAbsoluteName));
			StringBuilder builder = new StringBuilder();
			builder.append("spam.post\t");
			content = content.replace("\n", "");
			builder.append(content);
			out.write(builder.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getQualifiedFilename(Date timeStamp, String type) {
		
		fileName = SemblogConstants.NEW_BAYES_FILE_PREFIX + "."
				+ timeStamp.getTime() + "." + type + "."
				+ SemblogConstants.NEW_BAYES_FILE_SUFFIX;
		String fName = SemblogConstants.BAYES_TEST_INPUT + "/"
				+ fileName;

		return fName;
	}

	public boolean removeInputFiles() {
		boolean status = true;
		// A File object to represent the filename
		File f = new File(fileAbsoluteName);
		if (f.exists() || f.canWrite()) {			
			status =  f.delete();
		}
		return status;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
