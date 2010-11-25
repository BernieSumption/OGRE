package com.berniecode.ogre.edr;

import java.util.Collection;

/**
 * A collection of EntityTypes.
 *
 * @author Bernie Sumption
 */
public interface TypeDomain {

	/**
	 * An ID used to locate the type domain for this object graph. This is chosen by the programmer, and should
	 * be something globally unique to prevent clashes with other applications running on the same OGRE server.
	 * 
	 * <p>A Java-style package name that includes a domain name that you own is appropriate, e.g.
	 * "com.berniecode.ogre.demos.socialnetwork".
	 */
	String getTypeDomainId();
	
	/**
	 * @return The types in this domain
	 */
	Collection<EntityType> getEntityTypes();

}
