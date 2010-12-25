package com.berniecode.ogre.enginelib.shared;

/**
 * An integer {@link Property}, containing metadata on the size of the integer and whether it is nullable
 *
 * @author Bernie Sumption
 */
public class IntegerProperty extends Property {

	private final int bitLength;
	private final boolean nullable;

	public IntegerProperty(int propertyIndex, String name, int bitLength, boolean nullable) {
		super(propertyIndex, name, TYPECODE_INT, nullable);
		this.bitLength = bitLength;
		this.nullable = nullable;
	}

	public int getBitLength() {
		return bitLength;
	}
	
	public String toString() {
		return (nullable ? "nullable " : "") + bitLength + " bit integer property " + getName();
	}

}
