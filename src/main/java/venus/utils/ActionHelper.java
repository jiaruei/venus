package venus.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import venus.core.Action;
import venus.core.ActionClass;
import venus.exceptions.ActionParserException;

public class ActionHelper {

	private static Logger log = Logger.getLogger(ActionHelper.class);

	private List<ActionClass> actionClassList = new ArrayList<ActionClass>();
	private Map<String, ActionClass> actionMap = new HashMap<String, ActionClass>();

	public void parser(InputStream input) {

		if (input == null) {
			throw new IllegalArgumentException("inputstream can't be null ...");
		}
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(input);
			Element rootNode = document.getRootElement();
			List<Element> actionList = rootNode.getChildren("action");

			for (Element action : actionList) {
				String path = action.getAttributeValue("path");
				String clazz = action.getAttributeValue("class");
				ActionClass actionClass = new ActionClass(path, clazz);

				List<Element> forwardList = action.getChildren();
				for (Element forward : forwardList) {
					String name = forward.getAttributeValue("name");
					String forwardPath = forward.getAttributeValue("path");
					actionClass.addForwardPath(name, forwardPath);
				}
				log.debug("xml translate actionClass:" + actionClass);
				actionClassList.add(actionClass);
			}

			// translate to map
			for (ActionClass actionClass : actionClassList) {
				String path = actionClass.getPath();

				if (actionMap.containsKey(path)) {
					throw new ActionParserException("duplicated path [" + path + "] exist ...");
				} else {
					log.debug("path:" + path);
					log.debug("actionClass:" + actionClass);
					actionMap.put(path, actionClass);
				}
			}
		} catch (Exception e) {
			log.error(e, e);
			throw new ActionParserException(e);
		}

	}

	public Action getMappingAction(String actionName) {

		ActionClass actionClass = actionMap.get(actionName);
		if (actionClass == null) {
			throw new IllegalArgumentException("No mapping ActionClass by actionName [" + actionName + "] defined in actions.xml ");
		}

		String className = actionClass.getClassName();
		log.debug("initiailize calss :" + className);
		try {
			Class clazz = Class.forName(className);
			Action action = (Action) clazz.newInstance();
			return action;
		} catch (Exception e) {
			log.error(e, e);
			throw new ActionParserException("can't initialize class : " + className);
		}

	}

	public String getForwardPath(String actionName, String pathName) {

		ActionClass actionClass = actionMap.get(actionName);
		String mappingPath = actionClass.getMappingPath(pathName);
		log.debug("forward page : " + mappingPath);
		return mappingPath;
	}
}
