package venus.core.bean;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * <pre>
 * 	record all class that register components
 * </pre>
 * 
 * @author jerrywu
 * 
 */
public class ClazzState {

	// controller url or service simpleName
	private String urlName;

	// component classesFullName
	private String loadClassName;

	// annotation dependency injected
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

	public void addAutoWiredFiledMapping(String fieldName, String fieldClass) {
		autoWiredfieldMapping.put(fieldName, fieldClass);
	}

	public void removeAutoWiredFiledMapping(String fieldName) {
		if (autoWiredfieldMapping.containsKey(fieldName)) {
			autoWiredfieldMapping.remove(fieldName);
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
