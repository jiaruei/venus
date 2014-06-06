package venus.core.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class SetCharacterEncodingFilter implements Filter {

	private static Logger log = Logger.getLogger(SetCharacterEncodingFilter.class);

	private String default_encoding = "utf-8";

	public void init(FilterConfig config) throws ServletException {

		String encoding = config.getInitParameter("encoding");
		if (encoding != null) {
			log.info("using encoding : " + encoding);
			default_encoding = encoding;
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		req.setCharacterEncoding(default_encoding);
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

}
