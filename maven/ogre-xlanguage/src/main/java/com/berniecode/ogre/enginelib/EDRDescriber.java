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

	public static String describeObjectGraph(TypeDomain typeDomain, GraphUpdate objectGraph) {
		StringConcatenator sc = new StringConcatenator();
		doDescribeObjectGraph(typeDomain, objectGraph, sc, 0);
		return sc.buildString();
	}

	private static void doDescribeObjectGraph(TypeDomain typeDomain, GraphUpdate objectGraph, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		sc.add("ObjectGraph ")
		  .add(objectGraph.getTypeDomain().getTypeDomainId())
		  .add("/")
		  .add(objectGraph.getObjectGraphId());
		Entity[] entities = objectGraph.getEntities();
		for (int i=0; i<entities.length; i++) {
			sc.add("\n");
			doDescribeEntity(typeDomain, entities[i], sc, indent+1);
		}
	}

	private static void doDescribeEntity(TypeDomain typeDomain, Entity entityValue, StringConcatenator sc, int indent) {
		EntityType entityType = entityValue.getEntityType();
		doIndent(sc, indent);
		sc.add("Entity ")
		  .add(entityType.getName())
		  .add("#")
		  .addNumber(entityValue.getEntityId());
		for (int i=0; i<entityType.getPropertyCount(); i++) {
			Property property = entityType.getProperty(i);
			sc.add("\n");
			doDescribeValue(entityValue.getPropertyValue(property), property, sc, indent+1);
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
				sc.add(ValueUtils.getArrayValue(value, i));
			}
		} else {
			sc.add(value);
		}
	}
	
	//
	// DESCRIBE GRAPH UPDATES
	//

	
	public static String describeEntityUpdate(TypeDomain typeDomain, EntityUpdate entityUpdate) {
		StringConcatenator sc = new StringConcatenator();
		doDescribeEntityUpdate(typeDomain, entityUpdate, sc, 0);
		return sc.buildString();
	}
	
	public static String describeGraphUpdate(TypeDomain typeDomain, GraphUpdate graphUpdate) {
		StringConcatenator sc = new StringConcatenator();
		doDescribeGraphUpdate(typeDomain, graphUpdate, sc, 0);
		return sc.buildString();
	}
	
	private static void doDescribeGraphUpdate(TypeDomain typeDomain, GraphUpdate graphUpdate, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		sc.add("GraphUpdate for object graph ")
		  .add(graphUpdate.getTypeDomain().getTypeDomainId())
		  .add("/")
		  .add(graphUpdate.getObjectGraphId());
		if (graphUpdate.getEntities().length > 0) {
			sc.add("\ncomplete values:");
			doDescribeEntityUpdates(typeDomain, graphUpdate.getEntities(), sc, indent + 1);
		}
		if (graphUpdate.getEntityDiffs().length > 0) {
			sc.add("\npartial values:");
			doDescribeEntityUpdates(typeDomain, graphUpdate.getEntityDiffs(), sc, indent + 1);
		}
		if (graphUpdate.getEntityDeletes().length > 0) {
			sc.add("\ndeleted entities:");
			doDescribeEntityDeletes(typeDomain, graphUpdate.getEntityDeletes(), sc, indent + 1);
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
		EntityType entityType = update.getEntityType();
		sc.add("EntityUpdate for ")
		  .add(entityType)
		  .add("#")
		  .addNumber(update.getEntityId());
		for (int i=0; i<entityType.getPropertyCount(); i++) {
			Property property = entityType.getProperty(i);
			if (update.hasUpdatedValue(property)) {
				sc.add("\n");
				doDescribeValue(update.getPropertyValue(property), property, sc, indent + 1);
			}
		}
	}

	private static void doDescribeEntityDeletes(TypeDomain typeDomain, EntityDelete[] entityDeletes, StringConcatenator sc, int indent) {
		for (int i=0; i<entityDeletes.length; i++) {
			sc.add("\n");
			doDescribeEntityDelete(typeDomain, entityDeletes[i], sc, indent + 1);
		}
	}

	private static void doDescribeEntityDelete(TypeDomain typeDomain, EntityDelete delete, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		EntityType entityType = delete.getEntityType();
		sc.add("EntityDelete for ")
		  .add(entityType)
		  .add("#")
		  .addNumber(delete.getEntityId());
	}

}
