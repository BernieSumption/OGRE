package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.StringMap;


/**
 * A collection of {@link EntityType}s.
 * 
 * @author Bernie Sumption
 */
public class TypeDomain {

	private final StringMap entityTypesByName = new StringMap();
	private final EntityType[] entityTypes;
	private final String typeDomainId;

	public TypeDomain(String typeDomainId, EntityType[] entityTypes) {
		this.typeDomainId = typeDomainId;
		this.entityTypes = entityTypes;
		for (int i = 0; i < entityTypes.length; i++) {
			entityTypesByName.put(entityTypes[i].getName(), entityTypes[i]);
		}
		for (int i = 0; i < entityTypes.length; i++) {
			entityTypes[i].setTypeDomain(this);
		}
	}

	/**
	 * An ID used to locate this type domain. This is chosen by the programmer, and should be
	 * something globally unique to prevent clashes with other applications running on the same OGRE
	 * server.
	 * 
	 * <p>
	 * A Java-style package name that includes a domain name that you own is appropriate, e.g.
	 * "com.berniecode.ogre.demos.socialnetwork".
	 */
	public String getTypeDomainId() {
		return typeDomainId;
	}

	/**
	 * @return the number of {@link EntityType}s in this {@link TypeDomain}
	 */
	public int getEntityTypeCount() {
		return entityTypes.length;
	}

	/**
	 * @return A single {@link EntityType} identified by its order in this {@link TypeDomain}
	 */
	public EntityType getEntityType(int entityTypeIndex) {
		return entityTypes[entityTypeIndex];
	}

	/**
	 * @return A single {@link EntityType} identified by its name
	 */
	public EntityType getEntityTypeByName(String entityName) {
		return (EntityType) entityTypesByName.get(entityName);
	}
	
	
	/**
	 * @private
	 */
	EntityType[] getEntityTypes() {
		return entityTypes;
	}

}
