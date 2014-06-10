package venus.controllers;

import venus.core.injected.Autowired;
import venus.core.injected.Controller;
import venus.services.A2Service;

@Controller
public class LoginController {

	@Autowired
	private String x;

	@Autowired
	private A2Service a2Service;
}
