package ac.uk.soton.ecs.sw.semblog.tstore.common;

import org.springframework.context.ApplicationContext;

public class AppContextManager {	
	
	private static ApplicationContext appContext;
	

	public static ApplicationContext getAppContext() {
		return appContext;
	}

	public static void setAppContext(ApplicationContext appContext) {
		AppContextManager.appContext = appContext;
	}

}
