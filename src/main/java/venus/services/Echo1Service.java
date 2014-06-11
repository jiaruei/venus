package venus.services;

import org.apache.log4j.Logger;

import venus.core.injected.Autowired;
import venus.core.injected.Service;

@Service
public class Echo1Service {

	private static Logger log = Logger.getLogger(Echo1Service.class);
	@Autowired
	private Echo2Service echo2Service;

	public void echo() {
		log.debug("echo Service " + this.getClass().getName());
		echo2Service.echo();
	}
}
