package venus.core.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;

import venus.core.bean.ClazzState;
import venus.core.injected.Autowired;

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

		Object controllerObj;

		ClazzState controller = controllerMap.get(url);
		if (controller == null) {
			throw new IllegalArgumentException("No such adapt url[" + url + "] config");
		}

		try {
			controllerObj = classLoader.loadClass(controller.getLoadClassName()).newInstance();

			// injected if there are autoWired fields
			Map<String, String> autoWiredFields = controller.getAutoWiredfieldMapping();

			for (String fieldName : autoWiredFields.keySet()) {
				String fieldTypeClassName = autoWiredFields.get(fieldName);
				String simpleName = getFistLetterLowerCaseClassName(fieldTypeClassName);
				Object fieldObj;
				// check the autoWired field had registered
				if (!serviceMap.containsKey(simpleName)) {
					throw new RuntimeException("class[" + controller.getLoadClassName() + "] don't register mapping class[" + fieldTypeClassName + "]");
				}

				fieldObj = classLoader.loadClass(fieldTypeClassName).newInstance();
				FieldUtils.writeDeclaredField(controllerObj, fieldName, fieldObj, true);

			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return controllerObj;
	}

	private String getFistLetterLowerCaseClassName(String fieldTypeClassName) {
		int index = StringUtils.lastIndexOf(fieldTypeClassName, ".");
		String simpleClassName = StringUtils.substring(fieldTypeClassName, index + 1);
		return StringHelper.firstLetterLowerCase(simpleClassName);
	}

	private void recursiveServiceAutoWired(Object fieldObj) throws Exception {

		Class clazz = fieldObj.getClass();
		Field[] declaredFields = clazz.getDeclaredFields();
		for (Field field : declaredFields) {
			if (field.isAnnotationPresent(Autowired.class)) {
				classLoader.loadClass(field.getType().getName());
			} else {

			}
		}
		serviceMap.get(componentScan);
	}

	public static void main(String[] args) {

		try {

			Class<?> clazz = Class.forName("venus.core.bean.ClazzState");
			Field[] declaredFields = clazz.getDeclaredFields();
			for (Field field : declaredFields) {
				System.out.println(field.getType().getName());
				System.out.println(field.getName());
			}
			Object obj = clazz.newInstance();

			FieldUtils.writeDeclaredField(obj, "urlName", "/loginController", true);
			String val = FieldUtils.readField(obj, "urlName", true).toString();
			System.out.println(val);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
