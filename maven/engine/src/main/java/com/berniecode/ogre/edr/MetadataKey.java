package com.berniecode.ogre.edr;

/**
 * A bit of metadata about a {@link Property}. For example, integer properties
 * have a "bitlength" that specifies how large the integer can be.
 * 
 * <p>
 * Actually, I guess that since the property is itself metadata, then this is
 * technically meta-metadata.
 * 
 * @author Bernie Sumption
 */
public interface MetadataKey<T> {

	/**
	 * @return The name of this key, e.g. "bitlength"
	 */
	String getName();

	/**
	 * @return The default value to use if no value is supplied.
	 */
	T getDefault();

	/**
	 * Convert a value into a string for transmission over the network
	 */
	T fromString(String value);

	/**
	 * Convert a string produced by {@link #fromString(String)} back into a
	 * value
	 */
	String toString(T value);
}
