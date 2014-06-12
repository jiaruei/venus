package venus.core.servlets;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.log4j.Logger;

import venus.core.context.ControllerFactory;

/**
 * 
 * @author jerrywu
 * @since 2014/06/14
 */
public class ControllerServlet extends HttpServlet {

	private static Logger log = Logger.getLogger(ControllerServlet.class);

	private ControllerFactory factory = ControllerFactory.getInstance();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String contextPath = req.getContextPath();
		String requestURI = req.getRequestURI();

		String path = StringUtils.remove(requestURI, contextPath);
		String controllerExpress = StringUtils.remove(path, ".do");
		log.debug(" requested controller express : " + controllerExpress);
		String[] splits = StringUtils.split(controllerExpress, "/");
		String urlName = splits[0];
		String method = splits[1];

		Object mappingController = factory.getMappingController(urlName);
		try {
			Object view = MethodUtils.invokeMethod(mappingController, method, req, resp);
			req.getRequestDispatcher("").forward(req, resp);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			log.error(e, e);
			throw new ServletException(e);
		}
	}
}
