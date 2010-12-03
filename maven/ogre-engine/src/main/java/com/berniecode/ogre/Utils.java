package com.berniecode.ogre;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.berniecode.ogre.enginelib.platformhooks.NativeOrderedCollection;
import com.berniecode.ogre.enginelib.shared.OrderedCollection;

public class Utils {

	/**
	 * @return A set containing the items in the provided array, with duplicate items removed
	 */
	public static <T> Set<T> arrayToSet(T[] array) {
		Set<T> set = new HashSet<T>();
		for (T item : array) {
			set.add(item);
		}
		return set;
	}

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
	 * Convert a getter method, e.g. getMyXMLDocument() into a property name "get_my_XML_document"
	 */
	public static String getPropertyNameForGetter(Method method) {
		return getPropertyNameForGetter(method.getName());
	}

	/**
	 * Convert a getter method name, e.g. "getMyXMLDocument" into a property name
	 * "get_my_XML_document"
	 */
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

	/**
	 * Convert a Java list to an OGRE {@link OrderedCollection}
	 */
	public static OrderedCollection listToOrderedCollection(List<?> list) {
		OrderedCollection oc = new NativeOrderedCollection();
		for (Object item: list) {
			oc.push(item);
		}
		return oc;
	}

}
