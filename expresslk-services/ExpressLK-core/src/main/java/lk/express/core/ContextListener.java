package lk.express.core;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ExpressSystem.getSystem();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ExpressSystem.getSystem().shutdown();
	}

}
