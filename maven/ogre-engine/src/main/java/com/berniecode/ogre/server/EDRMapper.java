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

package com.berniecode.ogre.server;

import java.util.List;

import com.berniecode.ogre.enginelib.EntityType;
import com.berniecode.ogre.enginelib.EntityValue;
import com.berniecode.ogre.enginelib.TypeDomain;

/**
 * Maps a set of classes to a TypeDomain
 * 
 * @author Bernie Sumption
 */
public interface EDRMapper extends IdMapper {

	/**
	 * @return The TypeDOmain mapped by this {@link EDRMapper}
	 */
	TypeDomain getTypeDomain();

	/**
	 * Convert an object into an {@link EntityValue}
	 */
	EntityValue createEntityValue(Object object);

	/**
	 * Get the EntityType for an object. Equivalent to, but more efficient than,
	 * createEntity(entityObject).getEntityType()
	 */
	public EntityType getEntityTypeForObject(Object entityObject);

	/**
	 * @param entityObject
	 * @return any objects that this object references.
	 */
	public List<Object> getRelatedObjects(Object entityObject);

}