package com.berniecode.ogre;

import com.berniecode.ogre.server.pojods.IdMapper;

public class HasIdMapper implements IdMapper {

	@Override
	public long getIdForObject(Object entityObject) {
		return ((HasId) entityObject)._getId();
	}

	@Override
	public boolean objectHasId(Object entityObject) {
		HasId.class.cast(entityObject);
		return true;
	}

}
