package ac.uk.soton.ecs.sw.semblog.tstore.common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import ac.uk.soton.ecs.sw.semblog.jaxb.RdfPersister;
import ac.uk.soton.ecs.sw.semblog.jaxb.RdfPersisterType;
import ac.uk.soton.ecs.sw.semblog.jaxb.ScoreFactor;
import ac.uk.soton.ecs.sw.semblog.jaxb.ScoreFactorType;

public class JaxbContextLoader {

	private static final Logger logger = Logger
			.getLogger(JaxbContextLoader.class);

	private static Map<String, JAXBContext> contextInstances = new HashMap<String, JAXBContext>();

	private final static String CONFIG_PATH = "conf";
	
	private final static String JAXB_PACKAGE_NAME = "ac.uk.soton.ecs.sw.semblog.jaxb";
	
	private final static String SCORE_CONFIG_FILE = "scoreFactorConfig.xml";
	
	private final static String PERSISTER_CONFIG_FILE = "persisterConfig.xml";
	


	public static Map<String, Double> loadScoreFactors() {
		Map<String, Double> scoreFactors = new HashMap<String, Double>();
		try {
			Object rootElement = getRootElement(JAXB_PACKAGE_NAME,
					SCORE_CONFIG_FILE);
			if (rootElement instanceof ScoreFactor) {
				ScoreFactor rootScoreFactor = (ScoreFactor) rootElement;
				for (ScoreFactorType score : rootScoreFactor.getScoreFactor()) {
					String beanName = score.getScoreFactorBean();
					logger.info("Bean Name : " + beanName);

					double weightage = score.getDefaultWeightage();

					scoreFactors.put(beanName, weightage);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return scoreFactors;
	}
	
	public static List<String> loadRdfpersisters() {
		List<String> rdfPersisters = new ArrayList<String>();
		try {
			Object rootElement = getRootElement(JAXB_PACKAGE_NAME,
					PERSISTER_CONFIG_FILE);
			if (rootElement instanceof RdfPersister) {
				RdfPersister rootRdfPersister = (RdfPersister) rootElement;
				for (RdfPersisterType rdfPersister : rootRdfPersister.getRdfPersister()) {
					String beanName = rdfPersister.getRdfPersisterBean();
					logger.info("Bean Name : " + beanName);				

					rdfPersisters.add(beanName);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return rdfPersisters;
	}

	/**
	 * @param packageName
	 *            the jaxb package
	 * @param file
	 *            the file to process
	 * @return the root element from the file
	 * @throws JAXBException
	 */
	public static Object getRootElement(String packageName, File file)
			throws JAXBException {
		JAXBContext instance;
		instance = getContextInstance(packageName);
		Unmarshaller unm = instance.createUnmarshaller();
		return unm.unmarshal(file);

	}

	/**
	 * @param packageName
	 *            the jaxb package
	 * @param fileName
	 *            the file to process
	 * @return the root element from the file
	 * @throws JAXBException
	 */
	public static Object getRootElement(String packageName, String fileName)
			throws JAXBException {
		File file = new File(CONFIG_PATH + File.separatorChar + fileName);
		return getRootElement(packageName, file);
	}

	/**
	 * @param contextPath
	 *            the jaxb package
	 * @return the corresponding JAXB context
	 * @throws JAXBException
	 */
	public static JAXBContext getContextInstance(String contextPath)
			throws JAXBException {
		JAXBContext result = contextInstances.get(contextPath);
		if (null == result) {
			result = JAXBContext.newInstance(contextPath);
			contextInstances.put(contextPath, result);
		}
		return result;
	}

}
