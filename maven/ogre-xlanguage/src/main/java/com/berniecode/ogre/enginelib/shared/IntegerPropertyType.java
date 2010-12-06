package com.berniecode.ogre.enginelib.shared;

/**
 * A signed integer property with variable bitlength
 *
 * @author Bernie Sumption
 */
public class IntegerPropertyType implements PropertyType {
	
	private final boolean nullable;
	private final int bitlength;

	public IntegerPropertyType(int bitlength, boolean nullable) {
		this.bitlength = bitlength;
		this.nullable = nullable;
	}

	/**
	 * @return The maximum number of bits used to store this integer, 8, 16, 32 or 64
	 */
	public int getBitLength() {
		return bitlength;
	}
	
	/**
	 * @return Whether this integer property can have null values.
	 */
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * @see PropertyType#getDescription()
	 */
	public String getDescription() {
		return (isNullable() ? "nullable " : "") + getBitLength() + " bit integer";
	}

}
