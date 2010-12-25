package com.berniecode.ogre.wireformat;

import com.berniecode.ogre.EDRDeserialiser;
import com.berniecode.ogre.EDRSerialiser;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.shared.EntityType;
import com.berniecode.ogre.enginelib.shared.IntegerProperty;
import com.berniecode.ogre.enginelib.shared.Property;
import com.berniecode.ogre.enginelib.shared.ReferenceProperty;
import com.berniecode.ogre.enginelib.shared.TypeDomain;
import com.berniecode.ogre.wireformat.V1TypeDomain.EntityTypeMessage;
import com.berniecode.ogre.wireformat.V1TypeDomain.PropertyMessage;
import com.berniecode.ogre.wireformat.V1TypeDomain.PropertyMessage.BitLength;
import com.berniecode.ogre.wireformat.V1TypeDomain.PropertyMessage.Type;
import com.berniecode.ogre.wireformat.V1TypeDomain.TypeDomainMessage;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Converts Entity Data Representation objects to and from OGRE's binary protocol buffers based wire
 * format
 * 
 * @author Bernie Sumption
 */
public class OgreWireFormatV1Serialiser implements EDRSerialiser, EDRDeserialiser {
	
	
	//
	// This class is uncommented. It implements a trivial mapping between the protocol
	// buffers messages defined in the various .proto files, and OGRE's EDR classes,
	// both of which are themselves commented
	//

	/**
	 * @see EDRDeserialiser#deserialiseTypeDomain(byte[])
	 */
	@Override
	public TypeDomain deserialiseTypeDomain(byte[] message) {
		TypeDomainMessage tdm;
		try {
			tdm = TypeDomainMessage.parseFrom(message);
			if (OgreLog.isDebugEnabled()) {
				OgreLog.debug("Deserialised TypeDomainMessage: " + tdm);
			}
		} catch (InvalidProtocolBufferException e) {
			throw new OgreException("The byte array is not a valid Protocol Buffers message", e);
		}
		return new TypeDomain(tdm.getTypeDomainId(), convertEntityTypes(tdm));
	}

	private EntityType[] convertEntityTypes(TypeDomainMessage tdm) {
		String[] entityTypeNames = new String[tdm.getEntityTypesCount()];
		for (int i = 0; i < entityTypeNames.length; i++) {
			entityTypeNames[i] = tdm.getEntityTypes(i).getName();
		}
		
		EntityType[] entityTypes = new EntityType[tdm.getEntityTypesCount()];
		for (int i = 0; i < entityTypes.length; i++) {
			entityTypes[i] = convertEntityType(i, tdm.getEntityTypes(i), entityTypeNames);
		} 
		return entityTypes;
	}

	private EntityType convertEntityType(int index, EntityTypeMessage etm, String[] entityTypeNames) {
		return new EntityType(index, etm.getName(), convertProperties(etm, entityTypeNames));
	}

	private Property[] convertProperties(EntityTypeMessage etm, String[] entityTypeNames) {
		Property[] properties = new Property[etm.getPropertiesCount()];
		for (int i = 0; i < properties.length; i++) {
			properties[i] = convertProperty(i, etm.getProperties(i), entityTypeNames);
		}
		return properties;
	}

	private Property convertProperty(int index, PropertyMessage pm, String[] entityTypeNames) {
		Type propertyType = pm.getPropertyType();
		String name = pm.getName();
		if (propertyType == Type.INT) {
			return new IntegerProperty(index, name, pm.getBitLength().getNumber(), pm.getIsNullable());
		}
		if (propertyType == Type.REFERENCE) {
			if (!pm.hasReferenceTypeIndex()) {
				throw new OgreException("PropertyMessage " + name + " is of type Type.REFERENCE but has no referenceTypeIndex");
			}
			return new ReferenceProperty(index, name, entityTypeNames[pm.getReferenceTypeIndex()]);
		}
		return new Property(index, name, propertyType.getNumber(), pm.getIsNullable());
	}

	/**
	 * @see EDRSerialiser#serialiseTypeDomain(TypeDomain)
	 */
	@Override
	public byte[] serialiseTypeDomain(TypeDomain typeDomain) {
		TypeDomainMessage.Builder tdBuilder = TypeDomainMessage.newBuilder();
		tdBuilder.setTypeDomainId(typeDomain.getTypeDomainId());
		for (EntityType entityType: typeDomain.getEntityTypes()) {
			EntityTypeMessage.Builder etBuilder = EntityTypeMessage.newBuilder();
			etBuilder.setName(entityType.getName());
			for (int i = 0; i < entityType.getPropertyCount(); i++) {
				Property property = entityType.getProperty(i);
				PropertyMessage.Builder pBuilder = PropertyMessage.newBuilder()
					.setName(property.getName())
					.setPropertyType(Type.valueOf(property.getTypeCode()))
					.setIsNullable(property.isNullable());
				if (property instanceof IntegerProperty) {
					pBuilder.setBitLength(BitLength.valueOf(((IntegerProperty) property).getBitLength()));
				}
				if (property instanceof ReferenceProperty) {
					pBuilder.setReferenceTypeIndex(((ReferenceProperty) property).getReferenceType().getEntityTypeIndex());
				}
				etBuilder.addProperties(pBuilder);
			}
			tdBuilder.addEntityTypes(etBuilder);
		}
		return tdBuilder.build().toByteArray();
	}

}
