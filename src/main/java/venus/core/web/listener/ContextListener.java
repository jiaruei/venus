package venus.core.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import venus.core.context.ControllerFactory;

public class ContextListener implements ServletContextListener {

	private static Logger log = Logger.getLogger(ContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {

		// initialize annotation to register
		ServletContext servletContext = event.getServletContext();
		String basePackage = servletContext.getInitParameter("basePackage");
		log.debug("basePackage : " + basePackage);

		if (basePackage == null) {
			// scanner root classpath
			basePackage = servletContext.getContextPath().substring(1);
			log.debug("basePackage assigned root classpath : " + basePackage);
		}

		ControllerFactory factory = ControllerFactory.getInstance();
		factory.setBasePackage(basePackage);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {

	}

}
