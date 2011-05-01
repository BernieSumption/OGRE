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

package com.berniecode.ogre.enginelib.platformhooks;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility for building typed arrays
 * 
 * @author Bernie Sumption
 */
public class ArrayBuilder {

	private final Class componentType;
	private List contents = new ArrayList();

	public ArrayBuilder(Class componentType) {
		this.componentType = componentType;
	}

	/**
	 * Add a value
	 */
	public void add(Object object) {
		contents.add(componentType.cast(object));
	}

	/**
	 * Add each value in an array of values
	 */
	public void addAll(Object[] objects) {
		for (int i = 0; i < objects.length; i++) {
			add(objects[i]);
		}
	}

	/**
	 * Make an array containing all the items added to this builder
	 */
	public Object[] buildArray() {
		Object[] result = (Object[]) Array.newInstance(componentType, contents.size());
		for (int i = 0; i < result.length; i++) {
			result[i] = contents.get(i);
		}
		return result;
	}

}
