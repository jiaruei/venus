package venus.core.exception;

public class NoSuchViewExcepiton extends Exception {

	public NoSuchViewExcepiton() {
		super();
	}

	public NoSuchViewExcepiton(String mesg) {
		super(mesg);
	}

	public NoSuchViewExcepiton(Throwable e) {
		super(e);
	}

	public NoSuchViewExcepiton(String mesg, Throwable e) {
		super(mesg, e);
	}
}
