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

}