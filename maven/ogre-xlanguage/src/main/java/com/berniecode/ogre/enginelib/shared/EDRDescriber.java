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

	public static String describeObjectGraph(TypeDomain typeDomain, ObjectGraphValue objectGraph) {
		StringConcatenator sc = new StringConcatenator();
		doDescribeObjectGraph(typeDomain, objectGraph, sc, 0);
		return sc.buildString();
	}

	private static void doDescribeObjectGraph(TypeDomain typeDomain, ObjectGraphValue objectGraph, StringConcatenator sc, int indent) {
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
		  .add("=")
		  .add(value);
	}

}
