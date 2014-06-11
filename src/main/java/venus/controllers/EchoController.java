package venus.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import venus.core.injected.Autowired;
import venus.core.injected.Controller;
import venus.services.Echo1Service;

@Controller
public class EchoController {

	private static Logger log = Logger.getLogger(EchoController.class);

	@Autowired
	private Echo1Service loginService;

	public Object execute(HttpServletRequest req, HttpServletResponse resp) {
		log.debug("loginController ...." + req.getParameter("test"));
		loginService.echo();
		return null;
	}
}
