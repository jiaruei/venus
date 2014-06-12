package venus.core.context;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;

import venus.core.bean.ClazzState;
import venus.core.utils.StringHelper;

/**
 * 
 * @author jerrywu
 * @since 2014/06/14
 */
public class ControllerFactory {

	private static Logger log = Logger.getLogger(ControllerFactory.class);

	private static ControllerFactory factory;

	private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	private ComponentScan componentScan;
	private Map<String, ClazzState> controllerMap = new HashMap<String, ClazzState>();
	private Map<String, ClazzState> serviceMap = new HashMap<String, ClazzState>();

	private ControllerFactory() {
	}

	public static ControllerFactory getInstance() {

		if (factory == null) {
			factory = new ControllerFactory();
		}
		return factory;
	}

	public void setBasePackage(String basePackage) {
		this.componentScan = new ComponentScan(basePackage);
		registerComponents();
	}

	private void registerComponents() {

		for (ClazzState controller : this.componentScan.getControllerStateList()) {

			String urlName = controller.getUrlName();
			if (controllerMap.containsKey(urlName)) {
				ClazzState exist = controllerMap.get(urlName);
				throw new RuntimeException("the exist urlName between " + exist.getLoadClassName() + " and " + controller.getLoadClassName());
			} else {
				controllerMap.put(urlName, controller);
			}
		}

		for (ClazzState service : this.componentScan.getServiceClazzStateList()) {
			serviceMap.put(service.getUrlName(), service);
		}

	}

	public Object getMappingController(String url) {

		Object controllerObj = null;

		ClazzState controller = controllerMap.get(url);
		if (controller == null) {
			throw new IllegalArgumentException("No such adapt url[" + url + "] config");
		}

		try {
			controllerObj = classLoader.loadClass(controller.getLoadClassName()).newInstance();
			dependcyInjectComponents(controllerObj, controller.getAutoWiredfieldMapping());
			return controllerObj;
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}

	}

	private void dependcyInjectComponents(Object hostObj, Map<String, String> wiredFields) {

		if (!wiredFields.isEmpty()) {

			try {
				for (String fieldName : wiredFields.keySet()) {
					String fieldType = wiredFields.get(fieldName);
					Object serviceObj;
					// check the autoWired field had registered
					String refServiceName = getFirstLetterLowerCaseClassName(fieldType);
					if (!serviceMap.containsKey(refServiceName)) {
						throw new RuntimeException("service class[" + fieldType + "] don't register when class[" + hostObj.getClass().getName() + "] using it");
					}
					serviceObj = classLoader.loadClass(fieldType).newInstance();
					FieldUtils.writeDeclaredField(hostObj, fieldName, serviceObj, true);
					dependcyInjectComponents(serviceObj, serviceMap.get(refServiceName).getAutoWiredfieldMapping());

				}
			} catch (Exception e) {
				log.error(e, e);
				throw new RuntimeException(e);
			}
		}

	}

	private String getFirstLetterLowerCaseClassName(String fieldTypeClassName) {
		int index = StringUtils.lastIndexOf(fieldTypeClassName, ".");
		String simpleClassName = StringUtils.substring(fieldTypeClassName, index + 1);
		return StringHelper.firstLetterLowerCase(simpleClassName);
	}

}
