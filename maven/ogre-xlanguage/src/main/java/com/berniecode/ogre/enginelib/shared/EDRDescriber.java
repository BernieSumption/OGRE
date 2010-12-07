package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.StringConcatenator;

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
		sc.add("EntityType ");
		sc.add(entityType.getName());
		Property[] properties = entityType.getProperties();
		for (int i=0; i<properties.length; i++) {
			sc.add("\n");
			doDescribeProperty(properties[i], sc, indent+1);
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

	public static String describeObjectGraph(ObjectGraph objectGraph) {
		StringConcatenator sc = new StringConcatenator();
		doDescribeObjectGraph(objectGraph, sc, 0);
		return sc.buildString();
	}

	private static void doDescribeObjectGraph(ObjectGraph objectGraph, StringConcatenator sc, int indent) {
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

	private static void doDescribeEntity(Entity entity, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		sc.add("Entity ")
		  .add(entity.getEntityType().getName())
		  .add("#")
		  .addNumber(entity.getId());
		Property[] properties = entity.getEntityType().getProperties();
		Object[] values = entity.getValues();
		for (int i=0; i<properties.length; i++) {
			sc.add("\n");
			doDescribeValue(values[i], properties[i], sc, indent+1);
		}
	}

	private static void doDescribeValue(Object value, Property property, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		sc.add(property.getName())
		  .add("=")
		  .add(value);
	}

}
