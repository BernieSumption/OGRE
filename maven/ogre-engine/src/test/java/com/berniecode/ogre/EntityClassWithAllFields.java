package com.berniecode.ogre;


public class EntityClassWithAllFields {

	private int nonNullableInt;
	private Integer nullableInt;

	private long nonNullableLong;
	private Long nullableLong;

	private short nonNullableShort;
	private Short nullableShort;
	
	private byte nonNullableByte;
	private Byte nullableByte;
	
	private String string;

	private float nonNullableFloat;
	private Float nullableFloat;

	private double nonNullableDouble;
	private Double nullableDouble;
	
	/**
	 * Construct with default values for all fields
	 */
	public EntityClassWithAllFields() {}
	
	/**
	 * Construct with specific values for all fields
	 */
	public EntityClassWithAllFields(byte b1, Byte b2, short s1, Short s2, int i1, Integer i2, 
			long l1, Long l2, String str, float f1, Float f2,
			double d1, Double d2) {
		nonNullableByte = b1;
		nullableByte = b2;
		nonNullableShort = s1;
		nullableShort = s2;
		nonNullableInt = i1;
		nullableInt = i2;
		nonNullableLong = l1;
		nullableLong = l2;
		string = str;
		nonNullableFloat = f1;
		nullableFloat = f2;
		nonNullableDouble = d1;
		nullableDouble = d2;
	}

	public Integer getNullableInt() {
		return nullableInt;
	}

	public void setNullableInt(Integer nullableInt) {
		this.nullableInt = nullableInt;
	}

	public int getNonNullableInt() {
		return nonNullableInt;
	}

	public void setNonNullableInt(int nonNullableInt) {
		this.nonNullableInt = nonNullableInt;
	}

	public long getNonNullableLong() {
		return nonNullableLong;
	}

	public void setNonNullableLong(long nonNullableLong) {
		this.nonNullableLong = nonNullableLong;
	}

	public Long getNullableLong() {
		return nullableLong;
	}

	public void setNullableLong(Long nullableLong) {
		this.nullableLong = nullableLong;
	}

	public short getNonNullableShort() {
		return nonNullableShort;
	}

	public void setNonNullableShort(short nonNullableShort) {
		this.nonNullableShort = nonNullableShort;
	}

	public Short getNullableShort() {
		return nullableShort;
	}

	public void setNullableShort(Short nullableShort) {
		this.nullableShort = nullableShort;
	}

	public byte getNonNullableByte() {
		return nonNullableByte;
	}

	public void setNonNullableByte(byte nonNullableByte) {
		this.nonNullableByte = nonNullableByte;
	}

	public Byte getNullableByte() {
		return nullableByte;
	}

	public void setNullableByte(Byte nullableByte) {
		this.nullableByte = nullableByte;
	}

	public void setString(String string) {
		this.string = string;
	}

	public String getString() {
		return string;
	}

	public void setNonNullableFloat(float nonNullableFloat) {
		this.nonNullableFloat = nonNullableFloat;
	}

	public float getNonNullableFloat() {
		return nonNullableFloat;
	}

	public void setNullableFloat(Float nullableFloat) {
		this.nullableFloat = nullableFloat;
	}

	public Float getNullableFloat() {
		return nullableFloat;
	}

	public void setNonNullableDouble(double nonNullableDouble) {
		this.nonNullableDouble = nonNullableDouble;
	}

	public double getNonNullableDouble() {
		return nonNullableDouble;
	}

	public void setNullableDouble(Double nullableDouble) {
		this.nullableDouble = nullableDouble;
	}

	public Double getNullableDouble() {
		return nullableDouble;
	}

}