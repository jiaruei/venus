package venus.core.exception;

public class NoSuchViewExcpeiton extends Exception {

	public NoSuchViewExcpeiton() {
		super();
	}

	public NoSuchViewExcpeiton(String mesg) {
		super(mesg);
	}

	public NoSuchViewExcpeiton(Throwable e) {
		super(e);
	}

	public NoSuchViewExcpeiton(String mesg, Throwable e) {
		super(mesg, e);
	}
}
