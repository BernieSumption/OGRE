package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;

/**
 * A collection of {@link EntityType}s.
 * 
 * @author Bernie Sumption
 */
public interface TypeDomain {

	/**
	 * An ID used to locate this type domain. This is chosen by the programmer, and should be
	 * something globally unique to prevent clashes with other applications running on the same OGRE
	 * server.
	 * 
	 * <p>
	 * A Java-style package name that includes a domain name that you own is appropriate, e.g.
	 * "com.berniecode.ogre.demos.socialnetwork".
	 */
	String getTypeDomainId();

	/**
	 * @return All {@link EntityType}s in this {@link TypeDomain}. The returned array is safe to
	 *         modify without affecting the state of this {@link TypeDomain}
	 */
	EntityType[] getEntityTypes();

	/**
	 * @return A single {@link EntityType} from this {@link TypeDomain}
	 */
	EntityType getEntityTypeByName(String entityTypeName) throws NoSuchThingException;

}
