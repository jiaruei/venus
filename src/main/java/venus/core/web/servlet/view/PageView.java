package venus.core.web.servlet.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PageView implements Views {

	private String url;

	public PageView(String url) {
		this.url = url;
	}

	@Override
	public void response(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.getRequestDispatcher(url).forward(request, response);
		} catch (ServletException | IOException e) {
			throw new RuntimeException(e);
		}
	}

}
