package venus.core.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import venus.core.Constants;

public class AuthenticatedFilter implements Filter {

	private static Logger log = Logger.getLogger(AuthenticatedFilter.class);

	private static final String PAGE_FOLDER = "pages";

	private static final String PREFIX_SECURED = "secured";

	private ServletContext context;

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String actionPath = StringUtils.remove(req.getRequestURI(), req.getContextPath());
		if (isSecuredResouce(actionPath) && !isAuthenticated(req)) {

			res.sendRedirect(req.getContextPath() + "/pages/login.jsp");
		} else {
			chain.doFilter(request, response);
		}
	}

	private boolean isAuthenticated(HttpServletRequest req) {

		HttpSession session = req.getSession(true);
		if (session.getAttribute(Constants.LOGIN_USER) == null) {
			log.debug("user had not authenticated ...");
			return false;
		}
		return true;
	}

	private boolean isSecuredResouce(String actionPath) {

		// path like ${context}/pages/secured/.../*.jsp is authenciated

		String[] split = StringUtils.split(actionPath, "/");
		if (split[0].equals(PAGE_FOLDER) && split[1].equals(PREFIX_SECURED)) {
			log.debug("request secured resource : " + actionPath);
			return true;
		}
		return false;
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		context = config.getServletContext();
	}

}
