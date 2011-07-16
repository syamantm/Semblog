package ac.uk.soton.ecs.sw.semblog.tstore;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ac.uk.soton.ecs.sw.semblog.tstore.impl.jena.JenaRdfStoreManager;


public class TStoreProcesser {
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/appContext.xml");
		BeanFactory factory = context;
		JenaRdfStoreManager storeMgr = (JenaRdfStoreManager) factory
				.getBean("jenaRdfStoreManager");
		storeMgr.run();
	}

}
