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

package com.berniecode.ogre.wireformat;

import com.berniecode.ogre.EDRSerialiser;
import com.berniecode.ogre.enginelib.EntityReference;
import com.berniecode.ogre.enginelib.EntityType;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.PartialRawPropertyValueSet;
import com.berniecode.ogre.enginelib.Property;
import com.berniecode.ogre.enginelib.RawPropertyValueSet;
import com.berniecode.ogre.enginelib.ReferenceProperty;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.UnsafeAccess;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.wireformat.V1GraphUpdate.EntityDeleteMessage;
import com.berniecode.ogre.wireformat.V1GraphUpdate.EntityValueMessage;
import com.berniecode.ogre.wireformat.V1GraphUpdate.EntityValueMessage.PropertyValueMessage;
import com.berniecode.ogre.wireformat.V1GraphUpdate.GraphUpdateMessage;
import com.berniecode.ogre.wireformat.V1TypeDomain.EntityTypeMessage;
import com.berniecode.ogre.wireformat.V1TypeDomain.PropertyMessage;
import com.berniecode.ogre.wireformat.V1TypeDomain.PropertyMessage.Type;
import com.berniecode.ogre.wireformat.V1TypeDomain.TypeDomainMessage;
import com.google.protobuf.ByteString;

/**
 * Converts Entity Data Representation objects to and from OGRE's binary protocol buffers based wire
 * format
 * 
 * @author Bernie Sumption
 */
public class OgreWireFormatSerialiser implements EDRSerialiser {

	//
	// This class is uncommented. It implements a trivial mapping between the protocol
	// buffers messages defined in the various .proto files, and OGRE's EDR classes,
	// both of which are themselves commented
	//
	
	//
	// TYPE DOMAIN SERIALISATION
	//

	/**
	 * @see EDRSerialiser#serialiseTypeDomain(TypeDomain)
	 */
	@Override
	public byte[] serialiseTypeDomain(TypeDomain typeDomain) {
		TypeDomainMessage.Builder tdBuilder = TypeDomainMessage.newBuilder();
		tdBuilder.setTypeDomainId(typeDomain.getTypeDomainId());
		for (EntityType entityType : UnsafeAccess.getEntityTypes(typeDomain)) {
			EntityTypeMessage.Builder etBuilder = EntityTypeMessage.newBuilder();
			etBuilder.setName(entityType.getName());
			for (int i = 0; i < entityType.getPropertyCount(); i++) {
				Property property = entityType.getProperty(i);
				PropertyMessage.Builder pBuilder = PropertyMessage.newBuilder().setName(property.getName())
						.setPropertyType(Type.valueOf(property.getTypeCode())).setNullable(property.isNullable());
				if (property instanceof ReferenceProperty) {
					pBuilder.setReferenceTypeIndex(((ReferenceProperty) property).getReferenceType()
							.getEntityTypeIndex());
				}
				etBuilder.addProperties(pBuilder);
			}
			tdBuilder.addEntityTypes(etBuilder);
		}
		return tdBuilder.build().toByteArray();
	}
	
	//
	// GRAPH UPDATE SERIALISATION
	//

	/**
	 * @see EDRSerialiser#serialiseGraphUpdate(GraphUpdate)
	 */
	@Override
	public byte[] serialiseGraphUpdate(GraphUpdate graphUpdate) {
		GraphUpdateMessage.Builder guBuilder = GraphUpdateMessage.newBuilder()
				.setTypeDomainId(graphUpdate.getTypeDomain().getTypeDomainId())
				.setObjectGraphId(graphUpdate.getObjectGraphId())
				.setDataVersion(graphUpdate.getDataVersion())
				.setDataVersionScheme(graphUpdate.getDataVersionScheme());
		for (RawPropertyValueSet entity : graphUpdate.getEntityCreates()) {
			guBuilder.addEntityCreates(getEntityValueMessage(entity, false));
		}
		for (PartialRawPropertyValueSet entity : graphUpdate.getEntityUpdates()) {
			guBuilder.addEntityUpdates(getEntityValueMessage(entity, true));
		}
		for (EntityReference delete : graphUpdate.getEntityDeletes()) {
			guBuilder.addEntityDeletes(EntityDeleteMessage.newBuilder()
					.setEntityTypeIndex(delete.getEntityType().getEntityTypeIndex()).setEntityId(delete.getEntityId()));
		}
		return guBuilder.build().toByteArray();
	}

	/**
	 * @param diffStyle whether this is a "diff-style" EntityValue as defined in
	 *            V1GraphUpdate.proto. If true, the {@code entity} parameter must be an instance of
	 *            {@link PartialRawPropertyValueSet}
	 */
	private EntityValueMessage getEntityValueMessage(RawPropertyValueSet entity, boolean diffStyle) {
		EntityValueMessage.Builder evBuilder = EntityValueMessage.newBuilder()
				.setEntityTypeIndex(entity.getEntityType().getEntityTypeIndex()).setEntityId(entity.getEntityId());

		EntityType entityType = entity.getEntityType();
		for (int i = 0; i < entityType.getPropertyCount(); i++) {
			Property property = entityType.getProperty(i);
			if (diffStyle) {
				if (!((PartialRawPropertyValueSet) entity).hasUpdatedValue(property)) {
					continue;
				}
			}
			PropertyValueMessage.Builder pvBuilder = PropertyValueMessage.newBuilder();
			Object value = entity.getRawPropertyValue(property);
			if (diffStyle) {
				pvBuilder.setPropertyIndex(property.getPropertyIndex());
			}
			if (value == null) {
				pvBuilder.setNullValue(true);
			} else {
				try {
					switch (property.getTypeCode()) {
					case Property.TYPECODE_INT32:
						pvBuilder.setIntValue((Integer) value);
						break;
					case Property.TYPECODE_INT64:
						pvBuilder.setIntValue((Long) value);
						break;
					case Property.TYPECODE_FLOAT:
						pvBuilder.setFloatValue((Float) value);
						break;
					case Property.TYPECODE_DOUBLE:
						pvBuilder.setDoubleValue((Double) value);
						break;
					case Property.TYPECODE_STRING:
						pvBuilder.setStringValue((String) value);
						break;
					case Property.TYPECODE_BYTES:
						pvBuilder.setBytesValue(ByteString.copyFrom((byte[]) value));
						break;
					case Property.TYPECODE_REFERENCE:
						pvBuilder.setIdValue((Long) value);
						break;
					default:
						throw new OgreException(property + " has invalid invalid typeCode: " + property.getTypeCode());
					}
				} catch (ClassCastException e) {
					throw new OgreException("Incorrect value for " + entityType + "/" + property, e);
				}
			}
			evBuilder.addPropertyValues(pvBuilder);
		}
		return evBuilder.build();
	}
}
