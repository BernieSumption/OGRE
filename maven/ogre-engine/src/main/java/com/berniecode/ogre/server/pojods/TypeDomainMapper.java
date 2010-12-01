/**
 * 
 */
package com.berniecode.ogre.server.pojods;

import java.util.Set;

import com.berniecode.ogre.engine.shared.TypeDomain;

/**
 * Maps a set of classes to a TypeDomain
 * 
 * @author Bernie Sumption
 */
public interface TypeDomainMapper {
	/**
	 * Create a type domain from a set of classes
	 */
	TypeDomain mapTypeDomain(String typeDomainId, Set<Class<?>> classes);
}