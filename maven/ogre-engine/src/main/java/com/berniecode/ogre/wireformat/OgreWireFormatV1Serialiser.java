package com.berniecode.ogre.wireformat;

import com.berniecode.ogre.EDRDeserialiser;
import com.berniecode.ogre.EDRSerialiser;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.shared.EntityType;
import com.berniecode.ogre.enginelib.shared.EntityUpdate;
import com.berniecode.ogre.enginelib.shared.GraphUpdate;
import com.berniecode.ogre.enginelib.shared.IntegerProperty;
import com.berniecode.ogre.enginelib.shared.Property;
import com.berniecode.ogre.enginelib.shared.ReferenceProperty;
import com.berniecode.ogre.enginelib.shared.TypeDomain;
import com.berniecode.ogre.wireformat.V1GraphUpdate.EntityValueMessage;
import com.berniecode.ogre.wireformat.V1GraphUpdate.GraphUpdateMessage;
import com.berniecode.ogre.wireformat.V1GraphUpdate.PropertyValueMessage;
import com.berniecode.ogre.wireformat.V1TypeDomain.EntityTypeMessage;
import com.berniecode.ogre.wireformat.V1TypeDomain.PropertyMessage;
import com.berniecode.ogre.wireformat.V1TypeDomain.PropertyMessage.BitLength;
import com.berniecode.ogre.wireformat.V1TypeDomain.PropertyMessage.Type;
import com.berniecode.ogre.wireformat.V1TypeDomain.TypeDomainMessage;
import com.google.protobuf.ByteString;
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

	/**
	 * @see EDRSerialiser#serialiseGraphUpdate(GraphUpdate)
	 */
	@Override
	public byte[] serialiseGraphUpdate(GraphUpdate graphUpdate) {
		GraphUpdateMessage.Builder guBuilder = GraphUpdateMessage.newBuilder()
			.setTypeDomainId(graphUpdate.getTypeDomainId())
			.setObjectGraphId(graphUpdate.getObjectGraphId());
		for (EntityUpdate entity : graphUpdate.getEntities()) {
			guBuilder.addEntities(getEntityValueMessage(entity, false));
		}
		for (EntityUpdate entity : graphUpdate.getEntityDiffs()) {
			guBuilder.addEntities(getEntityValueMessage(entity, true));
		}
		//TODO: do entity deletes here
		return guBuilder.build().toByteArray();
	}

	/**
	 * @param diffStyle whether this is a "diff-style" EntityValue as defined in V1GraphUpdate.proto
	 */
	private EntityValueMessage getEntityValueMessage(EntityUpdate entity, boolean diffStyle) {
		EntityValueMessage.Builder evBuilder = EntityValueMessage.newBuilder()
				.setEntityTypeIndex(entity.getEntityType().getEntityTypeIndex())
				.setEntityId(entity.getEntityId());
		
		EntityType entityType = entity.getEntityType();
		for (int i = 0; i < entityType.getPropertyCount(); i++) {
			Property property = entityType.getProperty(i);
			if (entity.hasUpdatedValue(property)) {
				PropertyValueMessage.Builder pvBuilder = PropertyValueMessage.newBuilder();
				Object value = entity.getPropertyValue(property);
				if (diffStyle) {
					pvBuilder.setPropertyIndex(property.getPropertyIndex());
				}
				if (value == null) {
					pvBuilder.setIsNull(true);
				} else {
					switch(property.getTypeCode()) {
					case Property.TYPECODE_INT:
						pvBuilder.setIntValue(numberToLong(value));
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
				}
				evBuilder.addPropertyValues(pvBuilder);
			}
		}
		return evBuilder.build();
	}
	
	private long numberToLong(Object number) {
		if (number instanceof Integer) {
			return ((Integer) number).longValue();
		}
		if (number instanceof Short) {
			return ((Short) number).longValue();
		}
		if (number instanceof Byte) {
			return ((Byte) number).longValue();
		}
		return (Long) number;
	}



//	// INT Property objects can be cast to IntegerProperty to access more information
//    public static final int TYPECODE_INT       = 0;
//    public static final int TYPECODE_FLOAT     = 1;
//    public static final int TYPECODE_DOUBLE    = 2;
//    public static final int TYPECODE_STRING    = 3;
//    public static final int TYPECODE_BYTES     = 4;
//	// REFERENCE Property objects can be cast to ReferenceProperty to access more information
//    public static final int TYPECODE_REFERENCE = 5;
	@Override
	public GraphUpdate deserialiseGraphUpdate(byte[] message) {
		// TODO validate rules specified in V1GraphUpdate.proto
		return null;
	}

}
