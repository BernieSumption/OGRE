package com.berniecode.ogre.enginelib.platformhooks;

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
