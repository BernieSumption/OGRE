package com.berniecode.ogre.enginelib.shared;

/**
 * A simple implementation of the {@link ObjectGraph} interface for which all values must be
 * provided in the constructor
 * 
 * @author Bernie Sumption
 */
public class ImmutableObjectGraph implements ObjectGraph {
	
	private final TypeDomain typeDomain;
	private final String objectGraphId;
	private final Entity[] entities;

	public ImmutableObjectGraph(TypeDomain typeDomain, String objectGraphId, Entity[] entities) {
		this.typeDomain = typeDomain;
		this.objectGraphId = objectGraphId;
		this.entities = entities;
	}

	public Entity[] getEntities() {
		return entities;
	}

	public String getObjectGraphId() {
		return objectGraphId;
	}

	public TypeDomain getTypeDomain() {
		return typeDomain;
	}

}
