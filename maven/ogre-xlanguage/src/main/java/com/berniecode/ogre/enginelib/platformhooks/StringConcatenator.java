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

/**
 * Builds up a long string in an efficient way, similar to Java's StringBuilder.
 * 
 * @author Bernie Sumption
 */
public class StringConcatenator {

	private StringBuilder builder = new StringBuilder();

	/**
	 * Add something to the string. If the object passed in is not a string, it will be converted
	 * into a string.
	 * 
	 * @return a reference to this {@link StringConcatenator} so that calls to concat can be
	 *         chained, like so:
	 * 
	 *         <pre>
	 * new StringConcatenator().concat(&quot;a&quot;).concat(&quot;b&quot;).buildString()
	 * </pre>
	 */
	public StringConcatenator add(Object o) {
		builder.append(o);
		return this;
	}
	
	/**
	 * Add a number to the string
	 */
	public StringConcatenator addNumber(long number) {
		return add(Long.valueOf(number));
	}

	/**
	 * @return the String produced by concatenating all calls to {@link #add(Object)}
	 */
	public String buildString() {
		return builder.toString();
	}

}
