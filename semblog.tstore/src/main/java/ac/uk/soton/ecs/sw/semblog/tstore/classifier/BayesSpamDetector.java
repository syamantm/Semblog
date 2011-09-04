package ac.uk.soton.ecs.sw.semblog.tstore.classifier;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.mahout.classifier.ClassifierResult;
import org.apache.mahout.classifier.bayes.algorithm.BayesAlgorithm;
import org.apache.mahout.classifier.bayes.algorithm.CBayesAlgorithm;
import org.apache.mahout.classifier.bayes.common.BayesParameters;
import org.apache.mahout.classifier.bayes.datastore.InMemoryBayesDatastore;
import org.apache.mahout.classifier.bayes.exceptions.InvalidDatastoreException;
import org.apache.mahout.classifier.bayes.interfaces.Algorithm;
import org.apache.mahout.classifier.bayes.interfaces.Datastore;
import org.apache.mahout.classifier.bayes.model.ClassifierContext;
import org.apache.mahout.common.CommandLineUtil;
import org.apache.mahout.common.TimingStatistics;
import org.apache.mahout.common.commandline.DefaultOptionCreator;
import org.apache.mahout.common.iterator.FileLineIterable;
import org.apache.mahout.common.nlp.NGrams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ac.uk.soton.ecs.sw.semblog.tstore.common.SemblogConstants;

@Service
public class BayesSpamDetector implements ISpamDetector {

	private static final Logger logger = LoggerFactory.getLogger(BayesSpamDetector.class);	

	private static Map<String, String> classifationMap = new HashMap<String, String>();
	
	public boolean detectSpam() {
		boolean status = true;
		try {
			String cmdLine = "-m " + SemblogConstants.BAYES_MODEL_DIRECTORY
					+ " -d " + SemblogConstants.BAYES_TEST_INPUT 
					+ " -type "	+ SemblogConstants.CLASSIFIER_TYPE
					+ " -ng 1 -source hdfs -method sequential";

			logger.info(cmdLine);
			// generate arguments array from command line
			String[] args = cmdLine.split(" ");
			isClassifiedSpam(args);

		} catch (Exception ex) {
			ex.printStackTrace();
			status = false;
		}
		return status;
	}
	
	public static void isClassifiedSpam(String[] args) throws IOException, InvalidDatastoreException{
		DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
	    ArgumentBuilder abuilder = new ArgumentBuilder();
	    GroupBuilder gbuilder = new GroupBuilder();
	    
	    Option pathOpt = obuilder.withLongName("model").withRequired(true).withArgument(
	      abuilder.withName("model").withMinimum(1).withMaximum(1).create()).withDescription(
	      "The path on HDFS as defined by the -source parameter").withShortName("m")
	        .create();
	    
	    Option dirOpt = obuilder.withLongName("testDir").withRequired(true).withArgument(
	      abuilder.withName("testDir").withMinimum(1).withMaximum(1).create()).withDescription(
	      "The directory where test documents resides in").withShortName("d").create();
	    
	    Option helpOpt = DefaultOptionCreator.helpOption();
	    
	    Option encodingOpt = obuilder.withLongName("encoding").withArgument(
	      abuilder.withName("encoding").withMinimum(1).withMaximum(1).create()).withDescription(
	      "The file encoding.  Defaults to UTF-8").withShortName("e").create();
	    
	    Option defaultCatOpt = obuilder.withLongName("defaultCat").withArgument(
	      abuilder.withName("defaultCat").withMinimum(1).withMaximum(1).create()).withDescription(
	      "The default category Default Value: unknown").withShortName("default").create();
	    
	    Option gramSizeOpt = obuilder.withLongName("gramSize").withRequired(false).withArgument(
	      abuilder.withName("gramSize").withMinimum(1).withMaximum(1).create()).withDescription(
	      "Size of the n-gram. Default Value: 1").withShortName("ng").create();
	    
	    Option alphaOpt = obuilder.withLongName("alpha").withRequired(false).withArgument(
	      abuilder.withName("a").withMinimum(1).withMaximum(1).create()).withDescription(
	      "Smoothing parameter Default Value: 1.0").withShortName("a").create();
	    
	    Option verboseOutputOpt = obuilder.withLongName("verbose").withRequired(false).withDescription(
	      "Output which values were correctly and incorrectly classified").withShortName("v").create();
	    
	    Option typeOpt = obuilder.withLongName("classifierType").withRequired(false).withArgument(
	      abuilder.withName("classifierType").withMinimum(1).withMaximum(1).create()).withDescription(
	      "Type of classifier: bayes|cbayes. Default Value: bayes").withShortName("type").create();
	    
	    Option dataSourceOpt = obuilder.withLongName("dataSource").withRequired(false).withArgument(
	      abuilder.withName("dataSource").withMinimum(1).withMaximum(1).create()).withDescription(
	      "Location of model: hdfs").withShortName("source").create();
	    
	    Option methodOpt = obuilder.withLongName("method").withRequired(false).withArgument(
	      abuilder.withName("method").withMinimum(1).withMaximum(1).create()).withDescription(
	      "Method of Classification: sequential|mapreduce. Default Value: sequential").withShortName("method")
	        .create();
	    
	    Group group = gbuilder.withName("Options").withOption(defaultCatOpt).withOption(dirOpt).withOption(
	      encodingOpt).withOption(gramSizeOpt).withOption(pathOpt).withOption(typeOpt).withOption(dataSourceOpt)
	        .withOption(helpOpt).withOption(methodOpt).withOption(verboseOutputOpt).withOption(alphaOpt).create();
	    
	    try {
	      Parser parser = new Parser();
	      parser.setGroup(group);
	      CommandLine cmdLine = parser.parse(args);
	      
	      if (cmdLine.hasOption(helpOpt)) {
	        CommandLineUtil.printHelp(group);
	        return ;
	      }
	      
	      BayesParameters params = new BayesParameters();
	      // Setting all default values
	      int gramSize = 1;

	      String modelBasePath = (String) cmdLine.getValue(pathOpt);
	      
	      if (cmdLine.hasOption(gramSizeOpt)) {
	        gramSize = Integer.parseInt((String) cmdLine.getValue(gramSizeOpt));
	        
	      }

	      String classifierType = "bayes";
	      if (cmdLine.hasOption(typeOpt)) {
	        classifierType = (String) cmdLine.getValue(typeOpt);
	      }

	      String dataSource = "hdfs";
	      if (cmdLine.hasOption(dataSourceOpt)) {
	        dataSource = (String) cmdLine.getValue(dataSourceOpt);
	      }

	      String defaultCat = "unknown";
	      if (cmdLine.hasOption(defaultCatOpt)) {
	        defaultCat = (String) cmdLine.getValue(defaultCatOpt);
	      }

	      String encoding = "UTF-8";
	      if (cmdLine.hasOption(encodingOpt)) {
	        encoding = (String) cmdLine.getValue(encodingOpt);
	      }

	      String alphaI = "1.0";
	      if (cmdLine.hasOption(alphaOpt)) {
	        alphaI = (String) cmdLine.getValue(alphaOpt);
	      }
	      
	      boolean verbose = cmdLine.hasOption(verboseOutputOpt);
	      
	      String testDirPath = (String) cmdLine.getValue(dirOpt);

	      String classificationMethod = "sequential";
	      if (cmdLine.hasOption(methodOpt)) {
	        classificationMethod = (String) cmdLine.getValue(methodOpt);
	      }
	      
	      params.setGramSize(gramSize);
	      params.set("verbose", Boolean.toString(verbose));
	      params.setBasePath(modelBasePath);
	      params.set("classifierType", classifierType);
	      params.set("dataSource", dataSource);
	      params.set("defaultCat", defaultCat);
	      params.set("encoding", encoding);
	      params.set("alpha_i", alphaI);
	      params.set("testDirPath", testDirPath);
	      
	      if ("sequential".equalsIgnoreCase(classificationMethod)) {
	        classifySequential(params);
	      }
	    } catch (OptionException e) {
	      CommandLineUtil.printHelp(group);
	    }	   
	}
	
	public static void classifySequential(BayesParameters params) throws IOException, InvalidDatastoreException {
		logger.info("Loading model from: {}", params.print());
		classifationMap.clear();
	    boolean verbose = Boolean.valueOf(params.get("verbose"));
	    File dir = new File(params.get("testDirPath"));
	    File[] subdirs = dir.listFiles(new FilenameFilter() {
	      @Override
	      public boolean accept(File file, String s) {
	        return !s.startsWith(".");
	      }
	    });
	    
	    Algorithm algorithm;
	    Datastore datastore;
	    
	    if ("hdfs".equals(params.get("dataSource"))) {
	      if ("bayes".equalsIgnoreCase(params.get("classifierType"))) {
	    	  logger.info("Testing Bayes Classifier");
	        algorithm = new BayesAlgorithm();
	        datastore = new InMemoryBayesDatastore(params);
	      } else if ("cbayes".equalsIgnoreCase(params.get("classifierType"))) {
	    	  logger.info("Testing Complementary Bayes Classifier");
	        algorithm = new CBayesAlgorithm();
	        datastore = new InMemoryBayesDatastore(params);
	      } else {
	        throw new IllegalArgumentException("Unrecognized classifier type: " + params.get("classifierType"));
	      }
	      
	    } else {
	      throw new IllegalArgumentException("Unrecognized dataSource type: " + params.get("dataSource"));
	    }
	    ClassifierContext classifier = new ClassifierContext(algorithm, datastore);
	    classifier.initialize();
	    BayesResultAnalyzer resultAnalyzer = new BayesResultAnalyzer(classifier.getLabels(), params.get("defaultCat"));
	    TimingStatistics totalStatistics = new TimingStatistics();
	    if (subdirs != null) {
	      
	      for (File file : subdirs) {
	        if (verbose) {
	        	logger.info("--------------");
	        	logger.info("Testing: {}", file);
	        }
	        TimingStatistics operationStats = new TimingStatistics();
	        
	        long lineNum = 0;
	        for (String line : new FileLineIterable(new File(file.getPath()), Charset.forName(params
	            .get("encoding")), false)) {
	          
	          Map<String,List<String>> document = new NGrams(line, Integer.parseInt(params.get("gramSize")))
	              .generateNGrams();
	          for (Map.Entry<String,List<String>> stringListEntry : document.entrySet()) {
	            String correctLabel = stringListEntry.getKey();
	            List<String> strings = stringListEntry.getValue();
	            TimingStatistics.Call call = operationStats.newCall();
	            TimingStatistics.Call outercall = totalStatistics.newCall();
	            ClassifierResult classifiedLabel = classifier.classifyDocument(strings.toArray(new String[strings
	                .size()]), params.get("defaultCat"));
	            call.end();
	            outercall.end();
	            logger.info("File : " + file.getName() + " Label :" + classifiedLabel.getLabel());
	           
	           	classifationMap.put(file.getName(), classifiedLabel.getLabel());
	           
	           /* boolean correct = resultAnalyzer.addInstance(correctLabel, classifiedLabel);
	            if (verbose) {
	              // We have one document per line
	            	logger.info("Line Number: {} Line(30): {} Expected Label: {} Classified Label: {} Correct: {}",
	                new Object[] {lineNum, line.length() > 30 ? line.substring(0, 30) : line, correctLabel,
	                              classifiedLabel.getLabel(), correct,});
	            }*/
	            // log.info("{} {}", correctLabel, classifiedLabel);
	            
	          }
	          lineNum++;
	        }
	        /*
	         * log.info("{}\t{}\t{}/{}", new Object[] {correctLabel,
	         * resultAnalyzer.getConfusionMatrix().getAccuracy(correctLabel),
	         * resultAnalyzer.getConfusionMatrix().getCorrect(correctLabel),
	         * resultAnalyzer.getConfusionMatrix().getTotal(correctLabel)});
	         */
	        /*logger.info("Classified instances from {}", file.getName());
	        if (verbose) {
	        	logger.info("Performance stats {}", operationStats.toString());
	        }*/
	      }
	      
	    }
	   /* if (verbose) {
	    	logger.info("{}", totalStatistics);
	    }*/
	    //logger.info("{}", resultAnalyzer);	    
	  }

	@Override
	public boolean isSpamPost(String content) {
		boolean isSpam = true;
		BayesInput input = new BayesInput();
		input.prepareInput(content);
		detectSpam();
		if(classifationMap.containsKey(input.getFileName())){
			String cls  = classifationMap.get(input.getFileName());
			isSpam = cls.equals("spam.post");
		}
		if(!input.removeInputFiles())
			logger.error("Can't delete input files for classifier");
		return isSpam;
	}

	@Override
	public boolean testSpamDetection() {
		// TODO Auto-generated method stub
		return detectSpam();
	}

}
