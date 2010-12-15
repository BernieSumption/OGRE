package com.berniecode.ogre;

public class AbstractHasId implements HasId {

	private final long id;
	
	public AbstractHasId(long id) {
		this.id = id;
	}

	@Override
	public long _getId() {
		return id;
	}
	
	public String toString() {
		return getClass().getSimpleName() + "#" + id;
	}

}
