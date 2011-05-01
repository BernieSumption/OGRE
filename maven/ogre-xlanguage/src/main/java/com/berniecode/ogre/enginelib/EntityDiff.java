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
import com.berniecode.ogre.enginelib.platformhooks.ValueUtils;

/**
 * Represents an update to a single {@link Entity}
 * 
 * @author Bernie Sumption
 */
public class EntityDiff extends EntityValue implements PartialRawPropertyValueSet {

	private final boolean[] isChanged;

	public EntityDiff(EntityType entityType, long id, Object[] values, boolean[] isChanged) {
		super(entityType, id, values);
		this.isChanged = isChanged;
	}

	/**
	 * @return An {@link EntityDiff} object that if applied to the entity <code>from</code> will
	 *         change its values to be equal to those of <code>to</code>
	 */
	public static EntityDiff build(RawPropertyValueSet from, RawPropertyValueSet to) {
		EntityType entityType = from.getEntityType();
		if (entityType != to.getEntityType()) {
			throw new OgreException("Can't build an EntityDiff from " + from + " to " + to
					+ " because their entityTypes are different");
		}
		int propertyCount = entityType.getPropertyCount();
		Object[] changedValues = new Object[propertyCount];
		boolean[] changed = new boolean[propertyCount];
		boolean anyChanged = false;
		for (int i = 0; i < propertyCount; i++) {
			Property property = entityType.getProperty(i);
			Object fromValue = from.getRawPropertyValue(property);
			Object toValue = to.getRawPropertyValue(property);
			if (!ValueUtils.valuesAreEquivalent(fromValue, toValue)) {
				changedValues[i] = toValue;
				changed[i] = true;
				anyChanged = true;
			}
		}
		if (!anyChanged) {
			return null;
		}
		return new EntityDiff(entityType, from.getEntityId(), changedValues, changed);
	}

	/**
	 * @see PartialRawPropertyValueSet#getRawPropertyValue(Property)
	 */
	public Object getRawPropertyValue(Property property) {
		if (!hasUpdatedValue(property)) {
			throw new OgreException(this + " has no value for " + property);
		}
		return super.getRawPropertyValue(property);
	}

	/**
	 * @see PartialRawPropertyValueSet#hasUpdatedValue(Property)
	 */
	public boolean hasUpdatedValue(Property property) {
		return isChanged[property.getPropertyIndex()];
	}

	public String toString() {
		return "partial " + super.toString();
	}

}
