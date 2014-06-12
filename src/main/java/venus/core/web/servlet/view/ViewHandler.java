package venus.core.web.servlet.view;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import venus.core.exception.NoSuchViewExcepiton;

/**
 * 
 * @author jerrywu
 * 
 */
public class ViewHandler {

	private static Logger log = Logger.getLogger(ViewHandler.class);

	private static ViewHandler viewHandler;

	private static final String JSP = "java.lang.String";
	// private static final String OUTPUTSTREAM = "java.io.OutputStream";

	private Map<String, Class<? extends Views>> viewCached = new HashMap<String, Class<? extends Views>>();

	public static ViewHandler getInstance() {
		if (viewHandler == null) {
			viewHandler = new ViewHandler();
		}
		return viewHandler;
	}

	private ViewHandler() {
		viewCached.put(JSP, PageView.class);
		// viewCached.put(OUTPUTSTREAM, OutputStreamView.class);
	}

	public void addView(String responseType, Class<? extends Views> clazz) {
		if (viewCached.containsKey(responseType)) {
			throw new RuntimeException("exist ViewType : " + responseType);
		} else {
			viewCached.put(responseType, clazz);
		}
	}

	private Class<? extends Views> getMappingView(Object responseType) {

		for (String key : viewCached.keySet()) {
			try {
				boolean isSubClass = Class.forName(key).isInstance(responseType);
				if (isSubClass) {
					return viewCached.get(key);
				}
			} catch (ClassNotFoundException e) {
				log.error(e, e);
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public void router(Object responseType, HttpServletRequest request, HttpServletResponse response) throws NoSuchViewExcepiton, ServletException {

		Class<? extends Views> viewClass = getMappingView(responseType);
		if (viewClass == null) {
			String className = responseType.getClass().getName();
			log.error("ViewHandler class Not Found By " + className);
			throw new NoSuchViewExcepiton("ViewHandler class Not Found By " + className);
		}

		Class<?> argumentsType = viewClass.getDeclaredFields()[0].getType();
		try {
			Views view = viewClass.getConstructor(argumentsType).newInstance(responseType);
			view.response(request, response);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			log.error(e, e);
			throw new ServletException(e);
		}
	}

}
