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

package com.berniecode.ogre;

public class EntityClassWithAllFieldsImpl implements EntityClassWithAllFields {

	private int nonNullableInt;
	private Integer nullableInt;

	private long nonNullableLong;
	private Long nullableLong;

	private String string;

	private float nonNullableFloat;
	private Float nullableFloat;

	private double nonNullableDouble;
	private Double nullableDouble;

	private byte[] bytes;
	private EntityElement entityElement;

	/**
	 * Construct with specific values for all fields
	 */
	public EntityClassWithAllFieldsImpl(int i1, Integer i2, long l1, Long l2, String str, float f1, Float f2,
			double d1, Double d2, byte[] bs, EntityElementImpl e) {
		nonNullableInt = i1;
		nullableInt = i2;
		nonNullableLong = l1;
		nullableLong = l2;
		string = str;
		nonNullableFloat = f1;
		nullableFloat = f2;
		nonNullableDouble = d1;
		nullableDouble = d2;
		bytes = bs;
		entityElement = e;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.berniecode.ogre.EntityClassWithAllFields#getNullableInt()
	 */
	@Override
	public Integer getNullableInt() {
		return nullableInt;
	}

	public void setNullableInt(Integer nullableInt) {
		this.nullableInt = nullableInt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.berniecode.ogre.EntityClassWithAllFields#getNonNullableInt()
	 */
	@Override
	public int getNonNullableInt() {
		return nonNullableInt;
	}

	public void setNonNullableInt(int nonNullableInt) {
		this.nonNullableInt = nonNullableInt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.berniecode.ogre.EntityClassWithAllFields#getNonNullableLong()
	 */
	@Override
	public long getNonNullableLong() {
		return nonNullableLong;
	}

	public void setNonNullableLong(long nonNullableLong) {
		this.nonNullableLong = nonNullableLong;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.berniecode.ogre.EntityClassWithAllFields#getNullableLong()
	 */
	@Override
	public Long getNullableLong() {
		return nullableLong;
	}

	public void setNullableLong(Long nullableLong) {
		this.nullableLong = nullableLong;
	}

	public void setString(String string) {
		this.string = string;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.berniecode.ogre.EntityClassWithAllFields#getString()
	 */
	@Override
	public String getString() {
		return string;
	}

	public void setNonNullableFloat(float nonNullableFloat) {
		this.nonNullableFloat = nonNullableFloat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.berniecode.ogre.EntityClassWithAllFields#getNonNullableFloat()
	 */
	@Override
	public float getNonNullableFloat() {
		return nonNullableFloat;
	}

	public void setNullableFloat(Float nullableFloat) {
		this.nullableFloat = nullableFloat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.berniecode.ogre.EntityClassWithAllFields#getNullableFloat()
	 */
	@Override
	public Float getNullableFloat() {
		return nullableFloat;
	}

	public void setNonNullableDouble(double nonNullableDouble) {
		this.nonNullableDouble = nonNullableDouble;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.berniecode.ogre.EntityClassWithAllFields#getNonNullableDouble()
	 */
	@Override
	public double getNonNullableDouble() {
		return nonNullableDouble;
	}

	public void setNullableDouble(Double nullableDouble) {
		this.nullableDouble = nullableDouble;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.berniecode.ogre.EntityClassWithAllFields#getNullableDouble()
	 */
	@Override
	public Double getNullableDouble() {
		return nullableDouble;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.berniecode.ogre.EntityClassWithAllFields#getBytes()
	 */
	@Override
	public byte[] getBytes() {
		return bytes;
	}

	public void setEntityElement(EntityElement entityElement) {
		this.entityElement = entityElement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.berniecode.ogre.EntityClassWithAllFields#getEntityElement()
	 */
	@Override
	public EntityElement getEntityElement() {
		return entityElement;
	}

	@Override
	public void nonGetterMethod() {

	}

}