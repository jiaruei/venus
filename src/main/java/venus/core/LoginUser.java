package venus.core;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class LoginUser {

	private String accountId;
	private String password;

	public LoginUser(String accountId, String password) {
		this.accountId = accountId;
		this.password = password;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
