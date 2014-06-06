package venus.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ActionClass {

	private String path;
	private String className;
	private Map<String, String> forwardPath = new HashMap<String, String>();

	public ActionClass() {
	}

	public ActionClass(String path, String className) {
		this.path = path;
		this.className = className;
	}

	public void addForwardPath(String name, String path) {
		forwardPath.put(name, path);
	}

	public String getMappingPath(String name) {
		return forwardPath.get(name);
	}

	public String getPath() {
		return path;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
