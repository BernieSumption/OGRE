package com.berniecode.ogre;

import java.lang.reflect.Method;

public class Utils {

	private Utils() {}

	/**
	 * Check whether a method is a javabean getter, e.g. getFoo().
	 * 
	 * <p>java.lang.Object#getClass() is not considered a valid getter
	 */
	public static boolean isGetterMethod(Method method) {
		if (method.getDeclaringClass().equals(Object.class)) {
			return false;
		}
		String name = method.getName();
		return name.length() > 3 && name.startsWith("get") && method.getParameterTypes().length == 0;
	}

	/**
	 * Convert a getter method, e.g. getMyDog() into a property name "my_dog"
	 */
	public static String getPropertyNameForGetter(Method method) {
		return getPropertyNameForGetter(method.getName());
	}

	protected static String getPropertyNameForGetter(String name) {
		if (name.startsWith("get") && name.length() > 3) {
			name = name.substring(3);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(name.substring(0, 1).toLowerCase());
		for (int i = 1; i < name.length(); i++) {
			char ch = name.charAt(i);
			if (Character.isLowerCase(ch)) {
				sb.append(ch);
			} else {
				sb.append("_");
				sb.append(Character.toLowerCase(ch));
			}
		}
		return sb.toString();
	}

}
