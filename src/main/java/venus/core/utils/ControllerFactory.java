package venus.core.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;

import venus.core.bean.ClazzState;

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
		processRelationShip();
	}

	private void processRelationShip() {

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
			// injected if there are autoWired fields
			Map<String, String> autoWiredFields = controller.getAutoWiredfieldMapping();

			for (String fieldName : autoWiredFields.keySet()) {
				String fieldType = autoWiredFields.get(fieldName);
				String simpleFieldName = getFirstLetterLowerCaseClassName(fieldType);
				Object serviceObj;
				// check the autoWired field had registered
				if (!serviceMap.containsKey(simpleFieldName)) {
					throw new RuntimeException("class[" + controller.getLoadClassName() + "] don't register mapping class[" + fieldType + "]");
				}

				serviceObj = classLoader.loadClass(fieldType).newInstance();
				FieldUtils.writeDeclaredField(controllerObj, fieldName, serviceObj, true);
				chainServiceWired(serviceObj);
			}
			return controllerObj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private void chainServiceWired(Object hostObj) {

		String simpleName = getFirstLetterLowerCaseClassName(hostObj.getClass().getSimpleName());
		log.debug("hostObj :" + simpleName);
		ClazzState clazzState = serviceMap.get(simpleName);

		try {
			// injected if there are autoWired fields
			Map<String, String> autoWiredFields = clazzState.getAutoWiredfieldMapping();

			for (String fieldName : autoWiredFields.keySet()) {
				String fieldType = autoWiredFields.get(fieldName);
				Object serviceObj;
				// check the autoWired field had registered
				String refServiceName = getFirstLetterLowerCaseClassName(fieldType);
				if (!serviceMap.containsKey(refServiceName)) {
					throw new RuntimeException("class[" + clazzState.getLoadClassName() + "] don't register mapping class[" + fieldType + "]");
				}

				log.debug("refServiceName :" + refServiceName);
				serviceObj = classLoader.loadClass(fieldType).newInstance();
				FieldUtils.writeDeclaredField(hostObj, fieldName, serviceObj, true);
				//
				chainServiceWired(serviceObj);
			}

		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}

	}

	private String getFirstLetterLowerCaseClassName(String fieldTypeClassName) {
		int index = StringUtils.lastIndexOf(fieldTypeClassName, ".");
		String simpleClassName = StringUtils.substring(fieldTypeClassName, index + 1);
		return StringHelper.firstLetterLowerCase(simpleClassName);
	}

}
