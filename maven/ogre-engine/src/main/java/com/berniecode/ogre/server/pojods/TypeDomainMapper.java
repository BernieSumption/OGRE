
package com.berniecode.ogre.server.pojods;

import com.berniecode.ogre.enginelib.shared.Entity;
import com.berniecode.ogre.enginelib.shared.TypeDomain;

/**
 * Maps a set of classes to a TypeDomain
 * 
 * @author Bernie Sumption
 */
public interface TypeDomainMapper {
	
	/**
	 * @return The TypeDOmain mapped by this {@link TypeDomainMapper}
	 */
	TypeDomain getTypeDomain();
	
	/**
	 * Convert an object into an {@link Entity} with the specified id.
	 */
	Entity createEntity(Object object, long id, TypeDomain typeDomain);

}