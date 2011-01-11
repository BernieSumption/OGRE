package com.berniecode.ogre;

public class EntityElementImpl implements EntityElement {
	
	private String name;

	public EntityElementImpl(String name) {
		this.name = name;
		
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
}