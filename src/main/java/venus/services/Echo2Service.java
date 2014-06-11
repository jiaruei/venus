package venus.services;

import org.apache.log4j.Logger;

import venus.core.injected.Service;

@Service
public class Echo2Service {

	private static Logger log = Logger.getLogger(Echo2Service.class);

	public void echo() {
		log.debug("echo Service " + this.getClass().getName());
	}

}
