package com.berniecode.ogre.enginelib;

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
			doIndent(sc, indent+1);
			sc.add(entityType.getProperty(i));
		}
	}

	private static void doIndent(StringConcatenator sc, int indent) {
		for (int i=0; i<indent; i++) {
			sc.add(INDENT);
		}
	}
	
	//
	// DESCRIBE OBJECT GRAPH
	//

	public static String describeObjectGraph(GraphUpdate objectGraph) {
		StringConcatenator sc = new StringConcatenator();
		doDescribeObjectGraph(objectGraph, sc, 0);
		return sc.buildString();
	}

	private static void doDescribeObjectGraph(GraphUpdate objectGraph, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		sc.add("ObjectGraph ")
		  .add(objectGraph.getTypeDomain().getTypeDomainId())
		  .add("/")
		  .add(objectGraph.getObjectGraphId());
		RawPropertyValueSet[] entities = objectGraph.getEntityCreates();
		for (int i=0; i<entities.length; i++) {
			sc.add("\n");
			doDescribeEntity(entities[i], sc, indent+1);
		}
	}

	private static void doDescribeEntity(RawPropertyValueSet entity, StringConcatenator sc, int indent) {
		EntityType entityType = entity.getEntityType();
		doIndent(sc, indent);
		sc.add("Entity ")
		  .add(entityType.getName())
		  .add("#")
		  .addNumber(entity.getEntityId());
		for (int i=0; i<entityType.getPropertyCount(); i++) {
			Property property = entityType.getProperty(i);
			sc.add("\n");
			doDescribeValue(entity.getRawPropertyValue(property), property, sc, indent+1);
		}
	}

	private static void doDescribeValue(Object value, Property property, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		sc.add(property.getName())
		  .add("=");
		if (value != null && property instanceof ReferenceProperty) {
			sc.add(((ReferenceProperty) property).getReferenceType());
			sc.add("#");
		}
		sc.add(ValueUtils.valueToString(value));
	}
	
	//
	// DESCRIBE GRAPH UPDATES
	//

	
	public static String describeEntityUpdate(PartialRawPropertyValueSet entityUpdate) {
		StringConcatenator sc = new StringConcatenator();
		doDescribePartialRawPropertyValueSet(entityUpdate, sc, 0);
		return sc.buildString();
	}
	
	public static String describeGraphUpdate(GraphUpdate graphUpdate) {
		StringConcatenator sc = new StringConcatenator();
		doDescribeGraphUpdate(graphUpdate, sc, 0);
		return sc.buildString();
	}
	
	private static void doDescribeGraphUpdate(GraphUpdate graphUpdate, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		sc.add("GraphUpdate for object graph ")
		  .add(graphUpdate.getTypeDomain().getTypeDomainId())
		  .add("/")
		  .add(graphUpdate.getObjectGraphId());
		if (graphUpdate.getEntityCreates().length > 0) {
			sc.add("\ncomplete values:");
			RawPropertyValueSet[] entityValues = graphUpdate.getEntityCreates();
			for (int i=0; i<entityValues.length; i++) {
				sc.add("\n");
				doDescribeRawPropertyValueSet(entityValues[i], sc, indent + 1);
			}
		}
		if (graphUpdate.getEntityUpdates().length > 0) {
			sc.add("\npartial values:");
			PartialRawPropertyValueSet[] entityDiffs = graphUpdate.getEntityUpdates();
			for (int i=0; i<entityDiffs.length; i++) {
				sc.add("\n");
				doDescribePartialRawPropertyValueSet(entityDiffs[i], sc, indent + 1);
			}
		}
		if (graphUpdate.getEntityDeletes().length > 0) {
			sc.add("\ndeleted entities:");
			doDescribeEntityDeletes(graphUpdate.getEntityDeletes(), sc, indent + 1);
		}
	}

	private static void doDescribeRawPropertyValueSet(RawPropertyValueSet update, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		EntityType entityType = update.getEntityType();
		sc.add("value for ")
		  .add(entityType)
		  .add("#")
		  .addNumber(update.getEntityId());
		for (int i=0; i<entityType.getPropertyCount(); i++) {
			sc.add("\n");
			Property property = entityType.getProperty(i);
			doDescribeValue(update.getRawPropertyValue(property), property, sc, indent + 1);
		}
	}

	private static void doDescribePartialRawPropertyValueSet(PartialRawPropertyValueSet update, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		EntityType entityType = update.getEntityType();
		sc.add("partial value for ")
		  .add(entityType)
		  .add("#")
		  .addNumber(update.getEntityId());
		for (int i=0; i<entityType.getPropertyCount(); i++) {
			Property property = entityType.getProperty(i);
			if (update.hasUpdatedValue(property)) {
				sc.add("\n");
				doDescribeValue(update.getRawPropertyValue(property), property, sc, indent + 1);
			}
		}
	}

	private static void doDescribeEntityDeletes(EntityReference[] entityDeletes, StringConcatenator sc, int indent) {
		for (int i=0; i<entityDeletes.length; i++) {
			sc.add("\n");
			doDescribeEntityDelete(entityDeletes[i], sc, indent + 1);
		}
	}

	private static void doDescribeEntityDelete(EntityReference delete, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		EntityType entityType = delete.getEntityType();
		sc.add("delete ")
		  .add(entityType)
		  .add("#")
		  .addNumber(delete.getEntityId());
	}

}
