package venus.core.exception;

public class PageForwardException extends Exception {

	public PageForwardException() {
		super();
	}

	public PageForwardException(String mesg) {
		super(mesg);
	}

	public PageForwardException(Throwable e) {
		super(e);
	}

	public PageForwardException(String mesg, Throwable e) {
		super(mesg, e);
	}
}
