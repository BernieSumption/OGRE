package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.StringConcatenator;
import com.berniecode.ogre.enginelib.platformhooks.ValueUtils;

public class EDRDescriber {

	private EDRDescriber() {}
	
	private static String INDENT = "  ";
	
	//
	// DESCRIBE TYPE DOMAIN
	//

	public static String describeTypeDomain(TypeDomain typeDomain) {
		StringConcatenator sc = new StringConcatenator();
		doDescribeTypeDomain(typeDomain, sc, 0);
		return sc.buildString();
	}
	
	private static void doDescribeTypeDomain(TypeDomain typeDomain, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		sc.add("TypeDomain ");
		sc.add(typeDomain.getTypeDomainId());
		EntityType[] entityTypes = typeDomain.getEntityTypes();
		for (int i=0; i<entityTypes.length; i++) {
			sc.add("\n");
			doDescribeEntityType(entityTypes[i], sc, indent+1);
		}
	}
	
	private static void doDescribeEntityType(EntityType entityType, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		sc.addNumber(entityType.getEntityTypeIndex());
		sc.add(". EntityType ");
		sc.add(entityType.getName());
		for (int i=0; i<entityType.getPropertyCount(); i++) {
			sc.add("\n");
			doDescribeProperty(entityType.getProperty(i), sc, indent+1);
		}
	}

	private static void doDescribeProperty(Property property, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		sc.add(property.getPropertyType().getDescription())
		  .add(" property ")
		  .add(property.getName());
	}

	private static void doIndent(StringConcatenator sc, int indent) {
		for (int i=0; i<indent; i++) {
			sc.add(INDENT);
		}
	}
	
	//
	// DESCRIBE OBJECT GRAPH
	//

	public static String describeObjectGraph(TypeDomain typeDomain, ObjectGraphValueMessage objectGraph) {
		StringConcatenator sc = new StringConcatenator();
		doDescribeObjectGraph(typeDomain, objectGraph, sc, 0);
		return sc.buildString();
	}

	private static void doDescribeObjectGraph(TypeDomain typeDomain, ObjectGraphValueMessage objectGraph, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		sc.add("ObjectGraph ")
		  .add(objectGraph.getTypeDomainId())
		  .add("/")
		  .add(objectGraph.getObjectGraphId());
		EntityValueMessage[] entities = objectGraph.getEntityValues();
		for (int i=0; i<entities.length; i++) {
			sc.add("\n");
			doDescribeEntity(typeDomain, entities[i], sc, indent+1);
		}
	}

	private static void doDescribeEntity(TypeDomain typeDomain, EntityValueMessage entityValue, StringConcatenator sc, int indent) {
		EntityType entityType = typeDomain.getEntityType(entityValue.getEntityTypeIndex());
		doIndent(sc, indent);
		sc.add("Entity ")
		  .add(entityType.getName())
		  .add("#")
		  .addNumber(entityValue.getEntityId());
		for (int i=0; i<entityType.getPropertyCount(); i++) {
			Property property = entityType.getProperty(i);
			sc.add("\n");
			doDescribeValue(entityValue.getValue(property.getPropertyIndex()), property, sc, indent+1);
		}
	}

	private static void doDescribeValue(Object value, Property property, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		sc.add(property.getName())
		  .add("=");
		if (ValueUtils.isArray(value)) {
			int length = ValueUtils.getArrayLength(value);
			for (int i=0; i<length; i++) {
				if (i > 0) {
					sc.add(",");
				}
				if (property.getPropertyType() instanceof ReferencePropertyType) {
					sc.add("#");
				}
				sc.add(ValueUtils.getArrayValue(value, i));
			}
		} else {
			if (property.getPropertyType() instanceof ReferencePropertyType) {
				sc.add("#");
			}
			sc.add(value);
		}
	}
	
	//
	// DESCRIBE UPDATE MESSAGES
	//

	
	public static String describeEntityUpdate(TypeDomain typeDomain, EntityUpdate entityUpdate) {
		StringConcatenator sc = new StringConcatenator();
		doDescribeEntityUpdate(typeDomain, entityUpdate, sc, 0);
		return sc.buildString();
	}
	
	public static String describeUpdateMessage(TypeDomain typeDomain, UpdateMessage updateMessage) {
		StringConcatenator sc = new StringConcatenator();
		doDescribeUpdateMessage(typeDomain, updateMessage, sc, 0);
		return sc.buildString();
	}
	
	private static void doDescribeUpdateMessage(TypeDomain typeDomain, UpdateMessage updateMessage, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		sc.add("UpdateMessage for object graph ")
		  .add(updateMessage.getTypeDomainId())
		  .add("/")
		  .add(updateMessage.getObjectGraphId());
		if (updateMessage.getEntityValues().length > 0) {
			sc.add("\ncomplete values:");
			doDescribeEntityUpdates(typeDomain, updateMessage.getEntityValues(), sc, indent + 1);
		}
		if (updateMessage.getEntityDiffs().length > 0) {
			sc.add("\npartial values:");
			doDescribeEntityUpdates(typeDomain, updateMessage.getEntityDiffs(), sc, indent + 1);
		}
		if (updateMessage.getEntityDeletes().length > 0) {
			sc.add("\ndeleted entities:");
			doDescribeEntityDeletes(typeDomain, updateMessage.getEntityDeletes(), sc, indent + 1);
		}
	}

	private static void doDescribeEntityUpdates(TypeDomain typeDomain, EntityUpdate[] entityValues, StringConcatenator sc, int indent) {
		for (int i=0; i<entityValues.length; i++) {
			sc.add("\n");
			doDescribeEntityUpdate(typeDomain, entityValues[i], sc, indent + 1);
		}
	}

	private static void doDescribeEntityUpdate(TypeDomain typeDomain, EntityUpdate update, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		EntityType entityType = typeDomain.getEntityType(update.getEntityTypeIndex());
		sc.add("EntityUpdate for ")
		  .add(entityType)
		  .add("#")
		  .addNumber(update.getEntityId());
		for (int i=0; i<entityType.getPropertyCount(); i++) {
			if (update.hasUpdatedValue(i)) {
				sc.add("\n");
				doDescribeValue(update.getValue(i), entityType.getProperty(i), sc, indent + 1);
			}
		}
	}

	private static void doDescribeEntityDeletes(TypeDomain typeDomain, EntityDeleteMessage[] entityDeletes, StringConcatenator sc, int indent) {
		for (int i=0; i<entityDeletes.length; i++) {
			sc.add("\n");
			doDescribeEntityDelete(typeDomain, entityDeletes[i], sc, indent + 1);
		}
	}

	private static void doDescribeEntityDelete(TypeDomain typeDomain, EntityDeleteMessage delete, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		EntityType entityType = typeDomain.getEntityType(delete.getEntityTypeIndex());
		sc.add("EntityDelete for ")
		  .add(entityType)
		  .add("#")
		  .addNumber(delete.getEntityId());
	}

}
