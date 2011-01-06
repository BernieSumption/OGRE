package com.berniecode.ogre.wireformat;

import java.util.ArrayList;
import java.util.List;

import com.berniecode.ogre.EDRDeserialiser;
import com.berniecode.ogre.EDRSerialiser;
import com.berniecode.ogre.enginelib.EntityDelete;
import com.berniecode.ogre.enginelib.EntityDiff;
import com.berniecode.ogre.enginelib.EntityType;
import com.berniecode.ogre.enginelib.EntityValue;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.PartialRawPropertyValueSet;
import com.berniecode.ogre.enginelib.Property;
import com.berniecode.ogre.enginelib.RawPropertyValueSet;
import com.berniecode.ogre.enginelib.ReferenceProperty;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.UnsafeAccess;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.wireformat.V1GraphUpdate.EntityDeleteMessage;
import com.berniecode.ogre.wireformat.V1GraphUpdate.EntityValueMessage;
import com.berniecode.ogre.wireformat.V1GraphUpdate.GraphUpdateMessage;
import com.berniecode.ogre.wireformat.V1GraphUpdate.PropertyValueMessage;
import com.berniecode.ogre.wireformat.V1TypeDomain.EntityTypeMessage;
import com.berniecode.ogre.wireformat.V1TypeDomain.PropertyMessage;
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
			throw new OgreException("The supplied byte array is not a valid Protocol Buffers message", e);
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
			entityTypes[i] = convertEntityType(tdm.getEntityTypes(i), entityTypeNames);
		} 
		return entityTypes;
	}

	private EntityType convertEntityType(EntityTypeMessage etm, String[] entityTypeNames) {
		return new EntityType(etm.getName(), convertProperties(etm, entityTypeNames));
	}

	private Property[] convertProperties(EntityTypeMessage etm, String[] entityTypeNames) {
		Property[] properties = new Property[etm.getPropertiesCount()];
		for (int i = 0; i < properties.length; i++) {
			properties[i] = convertProperty(etm.getProperties(i), entityTypeNames);
		}
		return properties;
	}

	private Property convertProperty(PropertyMessage pm, String[] entityTypeNames) {
		Type propertyType = pm.getPropertyType();
		String name = pm.getName();
		if (propertyType == Type.REFERENCE) {
			if (!pm.hasReferenceTypeIndex()) {
				throw new OgreException("PropertyMessage " + name + " is of type Type.REFERENCE but has no referenceTypeIndex");
			}
			return new ReferenceProperty(name, entityTypeNames[pm.getReferenceTypeIndex()]);
		}
		return new Property(name, propertyType.getNumber(), pm.getNullable());
	}

	/**
	 * @see EDRSerialiser#serialiseTypeDomain(TypeDomain)
	 */
	@Override
	public byte[] serialiseTypeDomain(TypeDomain typeDomain) {
		TypeDomainMessage.Builder tdBuilder = TypeDomainMessage.newBuilder();
		tdBuilder.setTypeDomainId(typeDomain.getTypeDomainId());
		for (EntityType entityType: UnsafeAccess.getEntityTypes(typeDomain)) {
			EntityTypeMessage.Builder etBuilder = EntityTypeMessage.newBuilder();
			etBuilder.setName(entityType.getName());
			for (int i = 0; i < entityType.getPropertyCount(); i++) {
				Property property = entityType.getProperty(i);
				PropertyMessage.Builder pBuilder = PropertyMessage.newBuilder()
					.setName(property.getName())
					.setPropertyType(Type.valueOf(property.getTypeCode()))
					.setNullable(property.isNullable());
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
			.setTypeDomainId(graphUpdate.getTypeDomain().getTypeDomainId())
			.setObjectGraphId(graphUpdate.getObjectGraphId());
		for (RawPropertyValueSet entity : graphUpdate.getEntityValues()) {
			guBuilder.addEntities(getEntityValueMessage(entity, false));
		}
		for (PartialRawPropertyValueSet entity : graphUpdate.getEntityDiffs()) {
			guBuilder.addEntityDiffs(getEntityValueMessage(entity, true));
		}
		for (EntityDelete delete : graphUpdate.getEntityDeletes()) {
			guBuilder.addEntityDeletes(EntityDeleteMessage.newBuilder()
					.setEntityTypeIndex(delete.getEntityType().getEntityTypeIndex())
					.setEntityId(delete.getEntityId()));
		}
		return guBuilder.build().toByteArray();
	}

	/**
	 * @param diffStyle whether this is a "diff-style" EntityValue as defined in
	 *            V1GraphUpdate.proto. If true, the {@code entity} parameter must
	 *            be an instance of {@link PartialRawPropertyValueSet}
	 */
	private EntityValueMessage getEntityValueMessage(RawPropertyValueSet entity, boolean diffStyle) {
		EntityValueMessage.Builder evBuilder = EntityValueMessage.newBuilder()
				.setEntityTypeIndex(entity.getEntityType().getEntityTypeIndex())
				.setEntityId(entity.getEntityId());
		
		EntityType entityType = entity.getEntityType();
		for (int i = 0; i < entityType.getPropertyCount(); i++) {
			Property property = entityType.getProperty(i);
			boolean hasUpdatedValue;
			if (diffStyle) {
				hasUpdatedValue = ((PartialRawPropertyValueSet) entity).hasUpdatedValue(property);
			} else {
				hasUpdatedValue = true;
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
					switch(property.getTypeCode()) {
					case Property.TYPECODE_INT32:
					case Property.TYPECODE_INT64:
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
				} catch (ClassCastException e) {
					throw new OgreException("Incorrect value for " + entityType + "/" + property, e);
				}
			}
			evBuilder.addPropertyValues(pvBuilder);
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

	@Override
	public GraphUpdate deserialiseGraphUpdate(byte[] message, TypeDomain typeDomain) {
		GraphUpdateMessage gum;
		try {
			gum = GraphUpdateMessage.parseFrom(message);
			if (OgreLog.isDebugEnabled()) {
				OgreLog.debug("Deserialised GraphUpdateMessage: " + gum);
			}
		} catch (InvalidProtocolBufferException e) {
			throw new OgreException("The supplied byte array is not a valid Protocol Buffers message", e);
		}
		return new GraphUpdate(
				typeDomain,
				gum.getObjectGraphId(),
				getEntityValues(gum, typeDomain),
				getEntityDiffs(gum, typeDomain),
				getEntityDeletes(gum, typeDomain));
	}

	private EntityValue[] getEntityValues(GraphUpdateMessage gum, TypeDomain typeDomain) {
		List<EntityValue> entities = new ArrayList<EntityValue>();
		for (EntityValueMessage evm: gum.getEntitiesList()) {
			EntityType entityType = typeDomain.getEntityType(evm.getEntityTypeIndex());
			entities.add(new EntityValue(
					entityType,
					evm.getEntityId(),
					getEntityValuePropertiesArray(evm, entityType, false)));
		}
		return entities.toArray(new EntityValue[0]);
	}

	private Object[] getEntityValuePropertiesArray(EntityValueMessage evm, EntityType entityType, boolean diffStyle) {
		if (!diffStyle && evm.getPropertyValuesCount() != entityType.getPropertyCount()) {
			throw new OgreException("Invalid graph update: complete-style EntityValueMessages must " +
					"have the same number of properties as the corresponding EntityType. " + entityType +
					" has " + entityType.getPropertyCount() + ", the message has " + evm.getPropertyValuesCount());
		}
		Object[] values = new Object[entityType.getPropertyCount()];
		for (int i = 0; i < evm.getPropertyValuesCount(); i++) {
			PropertyValueMessage pvm = evm.getPropertyValues(i);

			int propertyIndex;			
			if (diffStyle) {
				if (!pvm.hasPropertyIndex()) {
					throw new OgreException("Invalid graph update: diff-style PropertyValueMessage has no propertyIndex");
				}
				propertyIndex = pvm.getPropertyIndex();
			} else {
				if (pvm.hasPropertyIndex()) {
					throw new OgreException("Invalid graph update: complete-style PropertyValueMessage should not have a propertyIndex");
				}
				propertyIndex = i;
			}
			Property property = entityType.getProperty(propertyIndex);

			
			int expectedFieldCount = diffStyle ? 2 : 1; // diff-style EntityValueMessages have both propertyIndex and value
			if (pvm.getAllFields().size() != expectedFieldCount) {
				throw new OgreException("Invalid graph update: PropertyValueMessage must have exactly one value. " +
						"This message for " + entityType.getName() + "." + property.getName() + " has " + 
						pvm.getAllFields().size() + " fields");
			}
			
			Object value;
			if (pvm.hasNullValue()) {
				value = null;
				if (!property.isNullable()) {
					throw new OgreException("Invalid graph update: the property " + entityType.getName() + "." + property.getName() + " doesn't allow null values.");
				}
			} else {
				switch (property.getTypeCode()) {
				case Property.TYPECODE_INT32:
					value = (int) pvm.getIntValue();
					break;
				case Property.TYPECODE_INT64:
					value = pvm.getIntValue();
					break;
				case Property.TYPECODE_FLOAT:
					value = pvm.getFloatValue();
					break;
				case Property.TYPECODE_DOUBLE:
					value = pvm.getDoubleValue();
					break;
				case Property.TYPECODE_STRING:
					value = pvm.getStringValue();
					break;
				case Property.TYPECODE_BYTES:
					value = pvm.getBytesValue().toByteArray();
					break;
				case Property.TYPECODE_REFERENCE:
					value = pvm.getIdValue();
					break;
				default:
					value = null;
				}
				if (value == null) {
					throw new OgreException("Invalid graph update: the property " + entityType.getName() + "." + property.getName() + " doesn't have the right type of value.");
				}
			}
			values[propertyIndex] = value;
		}
		return values;
	}

	private EntityDiff[] getEntityDiffs(GraphUpdateMessage gum, TypeDomain typeDomain) {
		List<EntityDiff> diffs = new ArrayList<EntityDiff>();
		for (EntityValueMessage evm: gum.getEntityDiffsList()) {
			EntityType entityType = typeDomain.getEntityType(evm.getEntityTypeIndex());
			Object[] entityValues = getEntityValuePropertiesArray(evm, entityType, true);
			boolean[] changed = new boolean[entityType.getPropertyCount()];
			for (PropertyValueMessage pvm: evm.getPropertyValuesList()) {
				changed[pvm.getPropertyIndex()] = true;
			}
			diffs.add(new EntityDiff(
					entityType,
					evm.getEntityId(),
					entityValues,
					changed));
		}
		return diffs.toArray(new EntityDiff[0]);
	}

	private EntityDelete[] getEntityDeletes(GraphUpdateMessage gum, TypeDomain typeDomain) {
		EntityDelete[] deletes = new EntityDelete[gum.getEntityDeletesCount()];
		for (int i = 0; i < deletes.length; i++) {
			EntityDeleteMessage entityDelete = gum.getEntityDeletes(i);
			deletes[i] = new EntityDelete(
					typeDomain.getEntityType(entityDelete.getEntityTypeIndex()),
					entityDelete.getEntityId());
		}
		return deletes;
	}

}
