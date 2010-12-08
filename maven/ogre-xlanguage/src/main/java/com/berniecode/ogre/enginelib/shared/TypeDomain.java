package com.berniecode.ogre.enginelib.shared;


/**
 * A collection of {@link EntityType}s.
 * 
 * @author Bernie Sumption
 */
public class TypeDomain {

	private EntityType[] entityTypes;
	private String typeDomainId;

	public TypeDomain(String typeDomainId, EntityType[] entityTypes) {
		this.typeDomainId = typeDomainId;
		this.entityTypes = entityTypes;
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

	public void setTypeDomainId(String typeDomainId) {
		this.typeDomainId = typeDomainId;
	}

	/**
	 * @return All {@link EntityType}s in this {@link TypeDomain}. The returned array is not safe to
	 *         modify. It must be copied before being passed outside of OGRE
	 */
	public EntityType[] getEntityTypes() {
		return entityTypes;
	}

	public void setEntityTypes(EntityType[] entityTypes) {
		this.entityTypes = entityTypes;
	}

}
