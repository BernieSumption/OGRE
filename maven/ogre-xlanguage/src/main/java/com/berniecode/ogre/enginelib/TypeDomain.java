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
			entityTypes[i].initialise(this, i);
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
