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

import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * A {@link Property} that refers to another entity, containing metadata on the type of entity that can be referred to
 *
 * @author Bernie Sumption
 */
public class ReferenceProperty extends Property {

	private EntityType referenceType;
	private final String referenceTypeName;
	private final String toStringCache;

	public ReferenceProperty(String name, String referenceTypeName) {
		super(name, TYPECODE_REFERENCE, true);
		this.referenceTypeName = referenceTypeName;
		toStringCache = "reference to " + referenceTypeName + " property " + getName();
	}

	public EntityType getReferenceType() {
		return referenceType;
	}
	
	public String toString() {
		return toStringCache;
	}

	/**
	 * @private
	 */
	void initialise(EntityType entityType, int propertyIndex) {
		referenceType = entityType.getTypeDomain().getEntityTypeByName(referenceTypeName);
		if (referenceType == null) {
			throw new OgreException("Can't initialise property '" + this + "' because the type domain does not contain a type '" + referenceTypeName + "'");
		}
		super.initialise(entityType, propertyIndex);
	}

}
