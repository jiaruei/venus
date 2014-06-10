package venus.core.utils;

import org.apache.commons.lang3.StringUtils;

public class StringHelper {

	public static String firstLetterLowerCase(String str) {

		if (StringUtils.isBlank(str)) {
			return str;
		}
		return StringUtils.isBlank(str) ? str : Character.toString(str.charAt(0)).toLowerCase() + str.substring(1);
	}
}
