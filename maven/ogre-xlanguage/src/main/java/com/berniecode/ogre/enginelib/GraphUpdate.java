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

/**
 * Represents a change to an object graph.
 * 
 * <p>
 * See the OGRE white paper for an overview of the function of this and other EDR classes.
 * 
 * @author Bernie Sumption
 */
public class GraphUpdate {

	private final TypeDomain typeDomain;
	private final String objectGraphId;
	private final RawPropertyValueSet[] entityCreates;
	private final PartialRawPropertyValueSet[] entityUpdates;
	private final EntityReference[] entityDeletes;
	private final int dataVersion;
	private final int dataVersionScheme;

	public GraphUpdate(TypeDomain typeDomain, String objectGraphId, int dataVersion, int dataVersionScheme,
			RawPropertyValueSet[] entityValues, PartialRawPropertyValueSet[] entityDiffs,
			EntityReference[] entityDeletes) {
		this.typeDomain = typeDomain;
		this.objectGraphId = objectGraphId;
		this.dataVersion = dataVersion;
		this.dataVersionScheme = dataVersionScheme;
		this.entityCreates = entityValues == null ? new RawPropertyValueSet[0] : entityValues;
		this.entityUpdates = entityDiffs == null ? new PartialRawPropertyValueSet[0] : entityDiffs;
		this.entityDeletes = entityDeletes == null ? new EntityReference[0] : entityDeletes;
	}

	/**
	 * Together with {@link #getObjectGraphId()}, identifies the object graph that this message
	 * should be applied to
	 */
	public TypeDomain getTypeDomain() {
		return typeDomain;
	}

	/**
	 * Together with the type domain, identifies the object graph that this message
	 * should be applied to
	 */
	public String getObjectGraphId() {
		return objectGraphId;
	}

	/**
	 * A counter that increments with each graph update to the same object graph. Graph updates must
	 * be applied in order to guarantee the integrity of the slave object graph.
	 */
	public int getDataVersion() {
		return dataVersion;
	}

	/**
	 * A unique id for the data version series.
	 */
	public int getDataVersionScheme() {
		return dataVersionScheme;
	}

	/**
	 * @return {@link RawPropertyValueSet}s for Entities that have been been created
	 */
	public RawPropertyValueSet[] getEntityCreates() {
		return entityCreates;
	}

	/**
	 * @return {@link PartialRawPropertyValueSet}s for entities that have been updated
	 */
	public PartialRawPropertyValueSet[] getEntityUpdates() {
		return entityUpdates;
	}

	/**
	 * @return {@link EntityReference}s for entities that have been removed
	 */
	public EntityReference[] getEntityDeletes() {
		return entityDeletes;
	}

	public String toString() {
		return "graph update #" + dataVersion + " for " + typeDomain.getTypeDomainId() + "/" + objectGraphId;
	}

}
