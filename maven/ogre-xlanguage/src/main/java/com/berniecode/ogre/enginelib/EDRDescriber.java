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
		Entity[] entities = objectGraph.getEntities();
		for (int i=0; i<entities.length; i++) {
			sc.add("\n");
			doDescribeEntity(entities[i], sc, indent+1);
		}
	}

	private static void doDescribeEntity(Entity entityValue, StringConcatenator sc, int indent) {
		EntityType entityType = entityValue.getEntityType();
		doIndent(sc, indent);
		sc.add("Entity ")
		  .add(entityType.getName())
		  .add("#")
		  .addNumber(entityValue.getEntityId());
		for (int i=0; i<entityType.getPropertyCount(); i++) {
			Property property = entityType.getProperty(i);
			sc.add("\n");
			doDescribeValue(entityValue.getPropertyValue(property), property, sc, indent+1, entityValue.isWired());
		}
	}

	private static void doDescribeValue(Object value, Property property, StringConcatenator sc, int indent, boolean isWiredEntity) {
		doIndent(sc, indent);
		sc.add(property.getName())
		  .add("=");
		//TODO all values should be plain objects, ogrelib should have a ByteArray type that overrides equals for the benefit of ValueUtils.valuesAreEquivilent
		if (ValueUtils.isArray(value)) {
			int length = ValueUtils.getArrayLength(value);
			for (int i=0; i<length; i++) {
				if (i > 0) {
					sc.add(",");
				}
				sc.add(ValueUtils.getArrayValue(value, i));
			}
		} else {
			// for unwired entities, 
			if (value != null && property instanceof ReferenceProperty && !isWiredEntity) {
				sc.add(((ReferenceProperty) property).getReferenceType());
				sc.add("#");
			}
			sc.add(value);
		}
	}
	
	//
	// DESCRIBE GRAPH UPDATES
	//

	
	public static String describeEntityUpdate(EntityUpdate entityUpdate) {
		StringConcatenator sc = new StringConcatenator();
		doDescribeEntityUpdate(entityUpdate, sc, 0);
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
		if (graphUpdate.getEntities().length > 0) {
			sc.add("\ncomplete values:");
			doDescribeEntityUpdates(graphUpdate.getEntities(), sc, indent + 1);
		}
		if (graphUpdate.getEntityDiffs().length > 0) {
			sc.add("\npartial values:");
			doDescribeEntityUpdates(graphUpdate.getEntityDiffs(), sc, indent + 1);
		}
		if (graphUpdate.getEntityDeletes().length > 0) {
			sc.add("\ndeleted entities:");
			doDescribeEntityDeletes(graphUpdate.getEntityDeletes(), sc, indent + 1);
		}
	}

	private static void doDescribeEntityUpdates(EntityUpdate[] entityValues, StringConcatenator sc, int indent) {
		for (int i=0; i<entityValues.length; i++) {
			sc.add("\n");
			doDescribeEntityUpdate(entityValues[i], sc, indent + 1);
		}
	}

	private static void doDescribeEntityUpdate(EntityUpdate update, StringConcatenator sc, int indent) {
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
				doDescribeValue(update.getPropertyValue(property), property, sc, indent + 1, update.isWired());
			}
		}
	}

	private static void doDescribeEntityDeletes(EntityDelete[] entityDeletes, StringConcatenator sc, int indent) {
		for (int i=0; i<entityDeletes.length; i++) {
			sc.add("\n");
			doDescribeEntityDelete(entityDeletes[i], sc, indent + 1);
		}
	}

	private static void doDescribeEntityDelete(EntityDelete delete, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		EntityType entityType = delete.getEntityType();
		sc.add("EntityDelete for ")
		  .add(entityType)
		  .add("#")
		  .addNumber(delete.getEntityId());
	}

}
