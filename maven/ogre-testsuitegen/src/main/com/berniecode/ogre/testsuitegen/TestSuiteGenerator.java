package com.berniecode.ogre.testsuitegen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.berniecode.ogre.EDRSerialiser;
import com.berniecode.ogre.enginelib.EDRDescriber;
import com.berniecode.ogre.enginelib.Entity;
import com.berniecode.ogre.enginelib.EntityStore;
import com.berniecode.ogre.enginelib.EntityType;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.IntegerProperty;
import com.berniecode.ogre.enginelib.Property;
import com.berniecode.ogre.enginelib.ReferenceProperty;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.wireformat.OgreWireFormatV1Serialiser;

public class TestSuiteGenerator {

	private static final String TYPE_DOMAIN_MESSAGE_FILE_NAME = "type-domain.message";
	private static final String INITIAL_DATA_MESSAGE_FILE_NAME = "initial-data.message";
	private static final String TRACE_FILE_NAME = "description.txt";
	
	private static final Integer[] ALL_INTEGER_BITLENGTHS = new Integer[] {8, 16, 32, 64};
	private static final Boolean[] TRUE_OR_FALSE = new Boolean[] {true, false};

	
	private static final int MIN_RANDOM_STRING_LENGTH = 1;
	private static final int MAX_RANDOM_STRING_LENGTH = 500;
	private static final String RANDOM_STRING_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_1234567890";

	private static final int MIN_ENTITY_TYPES_PER_TYPE_DOMAIN = 0;
	private static final int MAX_ENTITY_TYPES_PER_TYPE_DOMAIN = 50;

	private static final int MIN_PROPERTIES_PER_ENTITY_TYPE = 0;
	private static final int MAX_PROPERTIES_PER_ENTITY_TYPE = 50;

	private static final int MIN_INITIAL_ENTITIES = 0;
	private static final int MAX_INITIAL_ENTITIES = 100;
	
	private static boolean OVERWRITE_TEST_SUITES = true;
	
	
	EDRSerialiser serialiser = new OgreWireFormatV1Serialiser();

	private final File outputFolder;
	private final int suiteNumber;
	private BufferedWriter traceFileWriter;
	private TypeDomain typeDomain;
	private EntityStore entityStore;
	private Random random;
	private EntityType[] entityTypes;

	public TestSuiteGenerator(int suiteNumber, File outputFolder) {
		this.suiteNumber = suiteNumber;
		this.outputFolder = outputFolder;
	}

	public void generateTestSuite() throws IOException {
		if (outputFolder.exists() && !OVERWRITE_TEST_SUITES) {
			System.err.println("Skipping suite " + suiteNumber + " because an output folder already exists at " + outputFolder.getAbsolutePath());
			return;
		}
		if (!outputFolder.exists() && !outputFolder.mkdir()) {
			System.err.println("Failed to create folder " + outputFolder.getAbsolutePath());
			return;
		}

		setFile(TRACE_FILE_NAME, "");
		

		try {
			traceFileWriter = new BufferedWriter(new FileWriter(new File(outputFolder, TRACE_FILE_NAME)));
			
			random = new Random(suiteNumber);
			typeDomain = makeRandomTypeDomain(suiteNumber);
			
			setFile(TYPE_DOMAIN_MESSAGE_FILE_NAME, serialiser.serialiseTypeDomain(typeDomain));
			traceLine("type domain", EDRDescriber.describeTypeDomain(typeDomain));
			

			
			entityStore = createObjectGraph();
			GraphUpdate initialData = new GraphUpdate(typeDomain, "object-graph-" + suiteNumber, entityStore.getEntities(), null, null);
			setFile(INITIAL_DATA_MESSAGE_FILE_NAME, serialiser.serialiseGraphUpdate(initialData));
			traceLine("initial data set", EDRDescriber.describeObjectGraph(typeDomain, initialData));
		}
		finally {
			if (traceFileWriter != null) {
				traceFileWriter.close();
			}
		}
	}
	
	//
	// TYPE DOMAIN GENERATION
	//

	private TypeDomain makeRandomTypeDomain(int suiteNumber) {
		return new TypeDomain("com.berniecode.ogre.testsuitegen." + suiteNumber, entityTypes = makeRandomEntityTypes());
	}
	
	private EntityType[] makeRandomEntityTypes() {
		int length = makeRandomNumberInclusive(MIN_ENTITY_TYPES_PER_TYPE_DOMAIN, MAX_ENTITY_TYPES_PER_TYPE_DOMAIN);
		String[] names = new String[length];
		for (int i = 0; i < names.length; i++) {
			names[i] = "entityType" + i;
		}
		EntityType[] entityTypes = new EntityType[length];
		for (int i = 0; i < entityTypes.length; i++) {
			entityTypes[i] = new EntityType(i, names[i], makeRandomProperties(names[i], names));
		}
		return entityTypes;
	}

	private Property[] makeRandomProperties(String entityTypeName, String[] entityTypeNames) {
		int length = makeRandomNumberInclusive(MIN_PROPERTIES_PER_ENTITY_TYPE, MAX_PROPERTIES_PER_ENTITY_TYPE);
		Property[] properties = new Property[length];
		for (int i = 0; i < properties.length; i++) {
			int typeCode = makeRandomNumberInclusive(0, Property.TYPECODE_REFERENCE);
			Property property;
			String name = entityTypeName + Property.getNameForTypecode(typeCode) + "Property" + i;
			if (typeCode == Property.TYPECODE_REFERENCE) {
				String referenceTypeName = makeRandomChoice(entityTypeNames);
				property = new ReferenceProperty(i, name, referenceTypeName);
			} else if (typeCode == Property.TYPECODE_INT) {
				property = new IntegerProperty(i, name, makeRandomChoice(ALL_INTEGER_BITLENGTHS), makeRandomChoice(TRUE_OR_FALSE));
			} else {
				property = new Property(i, name, typeCode, makeRandomChoice(TRUE_OR_FALSE));
			}
			properties[i] = property;
		}
		return properties;
	}
	
	//
	// DATA GENERATION
	//
	
	private EntityStore createObjectGraph() {
		
		int numEntities = makeRandomNumberInclusive(MIN_INITIAL_ENTITIES, MAX_INITIAL_ENTITIES);

		// pre-calculate the types and IDs of all entities, because makeRandomEntity() needs to know them in advance
		// in order to create random references to them
		EntityType[] graphEntityTypes = new EntityType[numEntities];
		long[] graphEntityIds = new long[numEntities];
		Map<EntityType, List<Long>> idMap = new HashMap<EntityType, List<Long>>();
		
		for (int i = 0; i < graphEntityTypes.length; i++) {
			EntityType entityType = makeRandomChoice(entityTypes);
			long id = random.nextLong();
			graphEntityTypes[i] = entityType;
			graphEntityIds[i] = id;
			if (!idMap.containsKey(entityType)) {
				idMap.put(entityType, new ArrayList<Long>());
			}
			idMap.get(entityType).add(id);
			
		}

		EntityStore store = new EntityStore(typeDomain, true);
		for (int i = 0; i < graphEntityTypes.length; i++) {
			store.put(makeRandomEntity(graphEntityTypes[i], graphEntityIds[i], idMap));
		}
		
		return store;
	}

	private Entity makeRandomEntity(EntityType entityType, long id, Map<EntityType, List<Long>> idMap) {
		Object[] values = new Object[entityType.getPropertyCount()];
		for (int i = 0; i < values.length; i++) {
			values[i] = makeRandomPropertyValue(entityType.getProperty(i), idMap);
		}
		return new Entity(entityType, random.nextLong(), values );
	}
	
	private Object makeRandomPropertyValue(Property property, Map<EntityType, List<Long>> idMap) {
		switch(property.getTypeCode()) {
		case Property.TYPECODE_INT:
			return random.nextLong();
		case Property.TYPECODE_FLOAT:
			return random.nextFloat();
		case Property.TYPECODE_DOUBLE:
			return random.nextDouble();
		case Property.TYPECODE_STRING:
			return makeRandomString();
		case Property.TYPECODE_BYTES:
			return makeRandomBytes();
		case Property.TYPECODE_REFERENCE:
			EntityType refType = ((ReferenceProperty) property).getReferenceType();
			List<Long> ids = idMap.get(refType);
			if (ids == null) {
				return null; // if there are no objects of the right type, the ref must be null
			} else {
				long refId = makeRandomChoice(ids);
				return refId;
			}
		default:
			throw new OgreException(property + " has invalid invalid typeCode: " + property.getTypeCode());
		}
	}

	//
	// UTILITIES
	//

	private void setFile(String fileName, String string) throws IOException {
		setFile(fileName, string.getBytes(Charset.forName("UTF8")));
	}
	
	private void setFile(String fileName, byte[] bytes) throws IOException {
		OutputStream os = new FileOutputStream(new File(outputFolder, fileName));
		os.write(bytes);
		os.close();
	}

	private void traceLine(String title, String string) throws IOException {
		traceFileWriter.write("# " + title + "\n" + string + "\n");
	}

	private <T> T makeRandomChoice(T[] options) {
		return options[makeRandomNumberInclusive(0, options.length - 1)];
	}

	private <T> T makeRandomChoice(List<T> list) {
		return list.get(makeRandomNumberInclusive(0, list.size() - 1));
	}

	private String makeRandomString() {
		int length = makeRandomNumberInclusive(MIN_RANDOM_STRING_LENGTH, MAX_RANDOM_STRING_LENGTH);
		char[] buffer = new char[length];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (char) makeRandomNumberInclusive(28, 128); // TODO check printable ASCII range
		}
		return new String(buffer);
	}

	private byte[] makeRandomBytes() {
		int length = makeRandomNumberInclusive(MIN_RANDOM_STRING_LENGTH, MAX_RANDOM_STRING_LENGTH);
		byte[] buffer = new byte[length];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (byte) random.nextInt();
		}
		return buffer;
	}

	private int makeRandomNumberInclusive(int from, int to) {
		return from + random.nextInt(to - from + 1);
	}

}
