package venus.core.servlets;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import venus.core.Action;
import venus.utils.ActionHelper;

public class ActionServlet extends HttpServlet {

	private static Logger log = Logger.getLogger(ActionServlet.class);
	private static ActionHelper actionHelper;

	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		String filePath = config.getInitParameter("config");

		if (StringUtils.isNotBlank(filePath)) {
			log.debug("loading actions.xml config file from " + filePath);
			InputStream input = config.getServletContext().getResourceAsStream(filePath);
			if (input == null) {
				log.error("can't loading actions.xml file from " + filePath);
				throw new ServletException("can't loading actions.xml file from " + filePath);
			}
			actionHelper = new ActionHelper();
			actionHelper.parser(input);
			log.debug("parser actions.xml config file success ");
		} else {
			throw new ServletException("can't initialize actions.xml file....");
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String uri = req.getRequestURI();
		String context = req.getContextPath();

		log.debug("request uri : " + uri);

		// 1. get the request actionName
		String actionName = parserActionName(uri, context);
		// 2. get the mapping action class
		Action action = actionHelper.getMappingAction(actionName);
		// 3. invoke execute method
		String pathName = action.execute(req, resp);
		// 4. forward to path resource
		String url = actionHelper.getForwardPath(actionName, pathName);
		req.getRequestDispatcher(url).forward(req, resp);
	}

	private String parserActionName(String uri, String context) {

		String path = StringUtils.remove(uri, context);
		String actionName = StringUtils.remove(path, ".do");
		log.debug(" requested action name : " + actionName);
		return actionName;

	}

}
