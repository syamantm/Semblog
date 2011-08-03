package ac.uk.soton.ecs.sw.semblog.tstore;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ac.uk.soton.ecs.sw.semblog.tstore.impl.jena.JenaRdfStoreManager1;


public class TStoreProcesser {
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/appContext.xml");
		BeanFactory factory = context;
		JenaRdfStoreManager1 storeMgr = (JenaRdfStoreManager1) factory
				.getBean("jenaRdfStoreManager");
		storeMgr.run();
	}

}
