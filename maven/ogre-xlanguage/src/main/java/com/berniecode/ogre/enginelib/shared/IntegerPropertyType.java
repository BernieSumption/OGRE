package com.berniecode.ogre.enginelib.shared;

/**
 * A signed integer property with variable bitlength
 *
 * @author Bernie Sumption
 */
public class IntegerPropertyType implements PropertyType {
	
	public static final int TYPECODE = 1;
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
	 * @see PropertyType#getTypecode()
	 */
	public int getTypecode() {
		return TYPECODE;
	}


	/**
	 * @see PropertyType#getDescription()
	 */
	public String getDescription() {
		return (nullable ? "nullable " : "") + bitlength + " bit integer";
	}

}
