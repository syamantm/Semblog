package ac.uk.soton.ecs.sw.semblog.tstore.classifier;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.mahout.classifier.ClassifierResult;
import org.apache.mahout.classifier.ConfusionMatrix;

public class BayesResultAnalyzer {
	 
	  private final ConfusionMatrix confusionMatrix;
	  
	  private Map<String, Integer> correctMap = new HashMap<String, Integer>();
	  
	  /*
	   * === Summary ===
	   * 
	   * Correctly Classified Instances 635 92.9722 % Incorrectly Classified Instances 48 7.0278 % Kappa statistic
	   * 0.923 Mean absolute error 0.0096 Root mean squared error 0.0817 Relative absolute error 9.9344 % Root
	   * relative squared error 37.2742 % Total Number of Instances 683
	   */
	  private int correctlyClassified;
	  
	  private int incorrectlyClassified;
	  
	  public BayesResultAnalyzer(Collection<String> labelSet, String defaultLabel) {
	    confusionMatrix = new ConfusionMatrix(labelSet, defaultLabel);
	  }
	  
	  public ConfusionMatrix getConfusionMatrix() {
	    return this.confusionMatrix;
	  }
	  
	  /**
	   * 
	   * @param correctLabel
	   *          The correct label
	   * @param classifiedResult
	   *          The classified result
	   * @return whether the instance was correct or not
	   */
	  public boolean addInstance(String correctLabel, ClassifierResult classifiedResult) {
	    boolean result = correctLabel.equals(classifiedResult.getLabel());
	    if (result) {
	      correctlyClassified++;
	      correctMap.put(correctLabel, correctlyClassified);
	    } else {
	      incorrectlyClassified++;
	    }
	    confusionMatrix.addInstance(correctLabel, classifiedResult);
	    return result;
	  }
	  
	  @Override
	  public String toString() {
	    StringBuilder returnString = new StringBuilder();
	    
	    returnString.append("=======================================================\n");
	    returnString.append("Summary\n");
	    returnString.append("-------------------------------------------------------\n");
	    int totalClassified = correctlyClassified + incorrectlyClassified;
	    double percentageCorrect = (double) 100 * correctlyClassified / totalClassified;
	    double percentageIncorrect = (double) 100 * incorrectlyClassified / totalClassified;
	    NumberFormat decimalFormatter = new DecimalFormat("0.####");
	    
	    returnString.append(StringUtils.rightPad("Correctly Classified Instances", 40)).append(": ").append(
	      StringUtils.leftPad(Integer.toString(correctlyClassified), 10)).append('\t').append(
	      StringUtils.leftPad(decimalFormatter.format(percentageCorrect), 10)).append("%\n");
	    returnString.append(StringUtils.rightPad("Incorrectly Classified Instances", 40)).append(": ").append(
	      StringUtils.leftPad(Integer.toString(incorrectlyClassified), 10)).append('\t').append(
	      StringUtils.leftPad(decimalFormatter.format(percentageIncorrect), 10)).append("%\n");
	    returnString.append(StringUtils.rightPad("Total Classified Instances", 40)).append(": ").append(
	      StringUtils.leftPad(Integer.toString(totalClassified), 10)).append('\n');
	    returnString.append('\n');
	    
	    returnString.append(confusionMatrix);
	    
	    return returnString.toString();
	  }
	  
	  public boolean isClassifiedAsSpam(){
		  if(correctMap.containsKey("spam.post")){
			  return correctMap.get("spam.post") > 0;
		  }
		  return false;
	  }

}
