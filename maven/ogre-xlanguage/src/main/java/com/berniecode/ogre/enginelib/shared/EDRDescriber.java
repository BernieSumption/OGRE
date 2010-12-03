package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.StringConcatenator;

public class EDRDescriber {
	
	private static String INDENT = "  ";

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
			doDescribeEntityType((EntityType) entityTypes[i], sc, indent+1);
		}
	}
	
	private static void doDescribeEntityType(EntityType entityType, StringConcatenator sc, int indent) {
		doIndent(sc, indent);
		sc.add("EntityType ");
		sc.add(entityType.getName());
		OrderedCollection properties = entityType.getProperties();
		for (int i=0; i<properties.size(); i++) {
			sc.add("\n");
			doDescribeProperty((Property) properties.get(i), sc, indent+1);
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

}
