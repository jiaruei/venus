package venus.core.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import venus.core.bean.ClazzState;
import venus.core.injected.Autowired;
import venus.core.injected.Controller;
import venus.core.injected.Service;

public class ComponentScanner {

	private static Logger log = Logger.getLogger(ComponentScanner.class);

	private ClassLoader classLoader;
	private Class<Controller> controllerAnnotation = Controller.class;
	private Class<Service> serviceAnnotation = Service.class;
	private Class<Autowired> autoWiredAnnotation = Autowired.class;

	private List<ClazzState> controllerStateList = new ArrayList<ClazzState>();
	private List<ClazzState> serviceStateList = new ArrayList<ClazzState>();

	/**
	 * scanner classpath classes
	 * 
	 * @param basePackage
	 */
	public ComponentScanner(String basePackage) {

		try {
			classLoader = Thread.currentThread().getContextClassLoader();
			Enumeration<URL> resources = classLoader.getResources(basePackage);
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				File file = new File(url.toURI());
				collectByteCodeClassWithAnnotation(file);
			}

		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException("parser package error :" + e.getMessage());
		}
	}

	private void collectByteCodeClassWithAnnotation(File file) throws IOException, ClassNotFoundException {

		if (file.isDirectory()) {
			File[] subFiles = file.listFiles();
			for (File f : subFiles) {
				collectByteCodeClassWithAnnotation(f);
			}
		} else {
			String path = file.getAbsolutePath();
			if (path.endsWith(".class")) {
				String className = getClassName(new FileInputStream(file));
				Class<?> loadClass = classLoader.loadClass(className);
				// handler Controller Service AutoWired annotation

				if (loadClass.isAnnotationPresent(controllerAnnotation)) {

					log.debug("class[" + loadClass.getName() + "] with annotation[" + controllerAnnotation + "]");
					ClazzState clazzState = createClazzState(loadClass.getAnnotation(controllerAnnotation).name(), loadClass);
					controllerStateList.add(clazzState);

				} else if (loadClass.isAnnotationPresent(serviceAnnotation)) {

					log.debug("class[" + loadClass.getName() + "] with annotation[" + serviceAnnotation + "]");
					ClazzState clazzState = createClazzState(loadClass.getAnnotation(serviceAnnotation).name(), loadClass);
					serviceStateList.add(clazzState);
				}
			}
		}
	}

	private String lowerCaseFirstLetter(String str) {
		return StringUtils.isBlank(str) ? str : Character.toString(str.charAt(0)).toLowerCase() + str.substring(1);
	}

	private ClazzState createClazzState(String name, Class<?> loadClass) {

		String idName = StringUtils.isNotBlank(name) ? name : lowerCaseFirstLetter(loadClass.getSimpleName());
		ClazzState clazzState = new ClazzState(idName, loadClass.getName());
		Field[] fields = loadClass.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(autoWiredAnnotation)) {
				String refName = field.getAnnotation(autoWiredAnnotation).name();
				refName = StringUtils.isNotBlank(refName) ? refName : lowerCaseFirstLetter(field.getType().getSimpleName());
				clazzState.addAutoWiredFiledMapping(refName, field.getType().getName());
			}
		}
		return clazzState;
	}

	/**
	 * parser bytecode inputstream to get className
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private static String getClassName(InputStream is) throws IOException {

		DataInputStream dis = new DataInputStream(is);
		try {
			// skip header and class version
			dis.readLong();
			int cpcnt = (dis.readShort() & 0xffff) - 1;
			int[] classes = new int[cpcnt];
			String[] strings = new String[cpcnt];
			for (int i = 0; i < cpcnt; i++) {
				int t = dis.read();
				if (t == 7)
					classes[i] = dis.readShort() & 0xffff;
				else if (t == 1)
					strings[i] = dis.readUTF();
				else if (t == 5 || t == 6) {
					dis.readLong();
					i++;
				} else if (t == 8)
					dis.readShort();
				else
					dis.readInt();
			}
			dis.readShort(); // skip access flags
			return strings[classes[(dis.readShort() & 0xffff) - 1] - 1].replace('/', '.');
		} catch (IOException e) {
			throw e;
		}
	}

}
