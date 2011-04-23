
package com.berniecode.ogre.server;

import java.util.List;

import com.berniecode.ogre.enginelib.EntityType;
import com.berniecode.ogre.enginelib.EntityValue;
import com.berniecode.ogre.enginelib.TypeDomain;

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
	 * Convert an object into an {@link EntityValue}
	 */
	EntityValue createEntityValue(Object object);

	/**
	 * Get the EntityType for an object. Equivalent to, but more efficient than,
	 * createEntity(entityObject).getEntityType()
	 */
	public EntityType getEntityTypeForObject(Object entityObject);
	
	/**
	 * @param entityObject 
	 * @return any objects that this object references.
	 */
	public List<Object> getRelatedObjects(Object entityObject);

}