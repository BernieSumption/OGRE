package com.berniecode.ogre.enginelib.shared;

/**
 * A simple immutable record of the state of an object graph
 * 
 * @author Bernie Sumption
 */
public class ObjectGraphSnapshot implements ObjectGraph {
	
	private final TypeDomain typeDomain;
	private final String objectGraphId;
	private final Entity[] entities;

	public ObjectGraphSnapshot(TypeDomain typeDomain, String objectGraphId, Entity[] entities) {
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
