/*
 * Copyright 2011 Bernie Sumption. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. THIS SOFTWARE IS PROVIDED ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.ArrayBuilder;
import com.berniecode.ogre.enginelib.platformhooks.StringMap;

/**
 * A description of an entity. EntityType is to {@link Entity} as java.lang.Class is to
 * java.lang.Object.
 * 
 * @author Bernie Sumption
 */
public class EntityType {

	private final String name;
	private final Property[] properties;
	private ReferenceProperty[] referenceProperties;

	private final StringMap propertiesByName = new StringMap();

	private int entityTypeIndex;
	private TypeDomain typeDomain;

	public EntityType(String name, Property[] properties) {
		this.name = name;
		this.properties = properties;

		for (int i = 0; i < properties.length; i++) {
			propertiesByName.put(properties[i].getName(), properties[i]);
		}
		
		ArrayBuilder builder = new ArrayBuilder(ReferenceProperty.class);
		for (int i=0; i<properties.length; i++) {
			if (properties[i] instanceof ReferenceProperty) {
				builder.add(properties[i]);
			}
		}
		referenceProperties = (ReferenceProperty[]) builder.buildArray();
	}
	
	/**
	 * The {@link TypeDomain} that this {@link EntityType} belongs to.
	 */
	public TypeDomain getTypeDomain() {
		return typeDomain;
	}

	/**
	 * @return The index of this {@link EntityType} in its parent {@link TypeDomain}
	 */
	public int getEntityTypeIndex() {
		return entityTypeIndex;
	}

	/**
	 * @return The name of this entity type, typically a fully qualified class name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The {@link Property}s of this entity type.
	 */
	public Property getProperty(int propertyIndex) {
		return properties[propertyIndex];
	}

	/**
	 * @return The number of {@link Property}s in this entity type.
	 */
	public int getPropertyCount() {
		return properties.length;
	}
	
	public String toString() {
		return name;
	}

	/**
	 * @return true if this {@link EntityType} has a {@link ReferenceProperty} that points to the
	 *         specified {@link EntityType}
	 */
	public boolean isReferenceTo(EntityType entityType) {
		for (int i = 0; i < referenceProperties.length; i++) {
			if (referenceProperties[i].getReferenceType() == entityType) {
				return true;
			}
		}
		return false;
	}
	
	//
	// OGRE INTERNAL API
	//

	/**
	 * @return an array of all the reference properties in this {@link EntityType}
	 */
	ReferenceProperty[] getReferenceProperties() {
		return referenceProperties;
	}

	/**
	 * @private
	 */
	void initialise(TypeDomain typeDomain, int entityTypeIndex) {
		this.typeDomain = typeDomain;
		this.entityTypeIndex = entityTypeIndex;
		for (int i = 0; i < properties.length; i++) {
			properties[i].initialise(this, i);
		}
	}

	public Property getPropertyByName(String name) {
		return (Property) propertiesByName.get(name);
	}
}
