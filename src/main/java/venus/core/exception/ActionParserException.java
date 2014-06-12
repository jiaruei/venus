package venus.core.exception;

public class ActionParserException extends RuntimeException {

	public ActionParserException() {
		super();
	}

	public ActionParserException(String mesg) {
		super(mesg);
	}

	public ActionParserException(Throwable e) {
		super(e);
	}

	public ActionParserException(String mesg, Throwable e) {
		super(mesg, e);
	}

}
