package com.berniecode.ogre.testsuitegen;

import java.io.File;
import java.util.Random;

import com.berniecode.ogre.enginelib.EntityType;
import com.berniecode.ogre.enginelib.IntegerProperty;
import com.berniecode.ogre.enginelib.Property;
import com.berniecode.ogre.enginelib.ReferenceProperty;
import com.berniecode.ogre.enginelib.TypeDomain;

public class TestSuiteGenerator {

	private static final Integer[] ALL_INTEGER_BITLENGTHS = new Integer[] {8, 16, 32, 64};
	private static final Boolean[] TRUE_OR_FALSE = new Boolean[] {true, false};

	private static final String MANIFEXT_FILE_NAME = "manifext.txt";
	
	private static final int MIN_RANDOM_STRING_LENGTH = 1;
	private static final int MAX_RANDOM_STRING_LENGTH = 500;
	private static final String RANDOM_STRING_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_1234567890";

	private static final int MIN_ENTITY_TYPES_PER_TYPE_DOMAIN = 0;
	private static final int MAX_ENTITY_TYPES_PER_TYPE_DOMAIN = 50;

	private static final int MIN_PROPERTIES_PER_ENTITY_TYPE = 0;
	private static final int MAX_PROPERTIES_PER_ENTITY_TYPE = 50;

	public void generateTestSuites(int from, int to, File outputFolder) {
		for (int i = from; i <= to; i++) {
			generateTestSuite(i, new File(outputFolder, String.valueOf(i)));
		}
	}

	private void generateTestSuite(int suiteNumber, File outputFolder) {
		if (outputFolder.exists()) {
			System.err.println("Skipping suite " + suiteNumber + " because an output folder already exists at " + outputFolder.getAbsolutePath());
			return;
		}
		if (!outputFolder.mkdir()) {
			System.err.println("Failed to create folder " + outputFolder.getAbsolutePath());
			return;
		}
		File manifest = new File(outputFolder, MANIFEXT_FILE_NAME);
		Random random = new Random(suiteNumber); // seeding PRNG with suite number ensures consistent output across runs
		
		TypeDomain typeDomain = makeRandomTypeDomain(suiteNumber, random);
	}

	private TypeDomain makeRandomTypeDomain(int suiteNumber, Random random) {
		return new TypeDomain(makeRandomString(random), makeRandomEntityTypes(random));
	}
	
	private EntityType[] makeRandomEntityTypes(Random random) {
		int length = makeRandomNumber(random, MIN_ENTITY_TYPES_PER_TYPE_DOMAIN, MAX_ENTITY_TYPES_PER_TYPE_DOMAIN);
		String[] names = new String[length];
		for (int i = 0; i < names.length; i++) {
			names[i] = makeRandomString(random);
		}
		EntityType[] entityTypes = new EntityType[length];
		for (int i = 0; i < entityTypes.length; i++) {
			entityTypes[i] = new EntityType(i, names[i], makeRandomProperties(random, names));
		}
		return null;
	}

	private Property[] makeRandomProperties(Random random, String[] entityTypeNames) {
		int length = makeRandomNumber(random, MIN_PROPERTIES_PER_ENTITY_TYPE, MAX_PROPERTIES_PER_ENTITY_TYPE);
		Property[] properties = new Property[length];
		for (int i = 0; i < properties.length; i++) {
			int typeCode = makeRandomNumber(random, 0, Property.TYPECODE_REFERENCE);
			Property property;
			String name = makeRandomString(random);
			if (typeCode == Property.TYPECODE_REFERENCE) {
				String referenceTypeName = makeRandomChoice(random, entityTypeNames);
				property = new ReferenceProperty(i, name, referenceTypeName);
			} else if (typeCode == Property.TYPECODE_INT) {
				property = new IntegerProperty(i, name, makeRandomChoice(random, ALL_INTEGER_BITLENGTHS), makeRandomChoice(random, TRUE_OR_FALSE));
			} else {
				property = new Property(i, name, typeCode, makeRandomChoice(random, TRUE_OR_FALSE));
			}
			properties[i] = property;
		}
		return properties;
	}

	private <T> T makeRandomChoice(Random random, T[] options) {
		return options[makeRandomNumber(random, 0, options.length)]; // restore -1 when we're sure that this throws
	}

	private String makeRandomString(Random random) {
		int length = makeRandomNumber(random, MIN_RANDOM_STRING_LENGTH, MAX_RANDOM_STRING_LENGTH);
		char[] buffer = new char[length];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = RANDOM_STRING_CHARS.charAt(makeRandomNumber(random, 0, RANDOM_STRING_CHARS.length() - 1));
		}
		return new String(buffer);
	}

	private int makeRandomNumber(Random random, int from, int to) {
		return from + random.nextInt(to - from);
	}

}
