
package com.berniecode.ogre.server.pojods;

import com.berniecode.ogre.enginelib.shared.Entity;
import com.berniecode.ogre.enginelib.shared.EntityType;
import com.berniecode.ogre.enginelib.shared.TypeDomain;

/**
 * Maps a set of classes to a TypeDomain
 * 
 * @author Bernie Sumption
 */
public interface EDRMapper extends IdMapper {
	
	/**
	 * @return The TypeDOmain mapped by this {@link EDRMapper}
	 */
	TypeDomain getTypeDomain();
	
	/**
	 * Convert an object into an {@link Entity} with the specified id.
	 */
	Entity createEntity(Object object, long id);

	/**
	 * Get the EntityType for an object. Equivalent to, but more efficient than,
	 * createEntity(entityObject).getEntityType()
	 */
	public EntityType getEntityTypeForObject(Object entityObject);

}