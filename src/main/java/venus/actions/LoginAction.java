package venus.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import venus.core.Action;
import venus.core.Constants;
import venus.core.LoginUser;

public class LoginAction implements Action {

	private static Logger log = Logger.getLogger(LoginAction.class);

	@Override
	public String execute(HttpServletRequest req, HttpServletResponse resp) {

		String accountId = req.getParameter("accountId");
		String password = req.getParameter("password");

		LoginUser loginUser = new LoginUser(accountId, password);

		if (isValidUser(loginUser)) {
			log.debug("user login success ...");
			req.getSession().setAttribute(Constants.LOGIN_USER, loginUser);
			return "success";
		} else {
			log.debug("user login failed ...");
			return "failed";
		}
	}

	private boolean isValidUser(LoginUser loginUser) {

		return true;
	}

}
