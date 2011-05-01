/*
 * Copyright 2011 Bernie Sumption. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. THIS SOFTWARE IS PROVIDED ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

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
