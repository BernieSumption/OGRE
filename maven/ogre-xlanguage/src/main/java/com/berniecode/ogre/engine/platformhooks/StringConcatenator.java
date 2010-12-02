package com.berniecode.ogre.engine.platformhooks;

/**
 * Builds up a long string in an efficient way, similar to Java's StringBuilder.
 * 
 * @author Bernie Sumption
 * 
 * @jtoxNative - not translated into other languages
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
	public StringConcatenator concat(Object o) {
		builder.append(o);
		return this;
	}

	/**
	 * @return the String produced by concatenating all calls to {@link #concat(Object)}
	 */
	public String buildString() {
		return builder.toString();
	}

}
