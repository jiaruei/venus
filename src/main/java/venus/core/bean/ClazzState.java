package venus.core.bean;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ClazzState {

	private String urlName;
	private String loadClassName;
	private Map<String, String> autoWiredfieldMapping = new LinkedHashMap<String, String>();

	public ClazzState() {
	}

	public ClazzState(String urlName, String loadClassName) {
		this.urlName = urlName;
		this.loadClassName = loadClassName;
	}

	public String getUrlName() {
		return urlName;
	}

	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}

	public String getLoadClassName() {
		return loadClassName;
	}

	public void setLoadClassName(String loadClassName) {
		this.loadClassName = loadClassName;
	}

	public Map<String, String> getAutoWiredfieldMapping() {
		return autoWiredfieldMapping;
	}

	public void addAutoWiredFiledMapping(String field, String fieldClass) {
		autoWiredfieldMapping.put(field, fieldClass);
	}

	public void removeAutoWiredFiledMapping(String field) {
		if (autoWiredfieldMapping.containsKey(field)) {
			autoWiredfieldMapping.remove(field);
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
