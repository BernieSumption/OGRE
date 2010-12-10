package com.berniecode.ogre.server.pojods;

import java.util.Comparator;

public class ClassNameComparator implements Comparator<Class<?>> {

	//TODO test this - the order seems wrong
	@Override
	public int compare(Class<?> o1, Class<?> o2) {
		return o1.getName().compareTo(o2.getName()); 
	}

}
