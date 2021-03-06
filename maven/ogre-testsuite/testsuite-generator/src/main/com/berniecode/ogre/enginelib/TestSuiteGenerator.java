/*
 * Copyright 2011 Bernie Sumption. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. THIS SOFTWARE IS PROVIDED ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package com.berniecode.ogre.enginelib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.berniecode.ogre.EDRSerialiser;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.server.pojods.EntityReferenceComparator;
import com.berniecode.ogre.wireformat.OgreWireFormatSerialiser;

public class TestSuiteGenerator {

	private static final String TYPE_DOMAIN_MESSAGE_FILE_NAME = "type-domain.message";
	private static final String INITIAL_DATA_MESSAGE_FILE_NAME = "initial-data.message";
	private static final String GRAPH_UPDATE_MESSAGE_FILE_PATTERN = "graph-update-%d.message";
	private static final String TRACE_FILE_NAME = "trace.txt";

	private static final Boolean[] TRUE_OR_FALSE = new Boolean[] { true, false };

	private static final int MAX_ENTITIES_TO_CHANGE_PER_ITERATION = 10;
	private static final int MAX_ENTITIES_TO_DELETE_PER_ITERATION = 10;
	private static final int MAX_ENTITIES_TO_ADD_PER_ITERATION = 3;

	private static final int MAX_RANDOM_STRING_LENGTH = 20;

	private static final int MIN_ENTITY_TYPES_PER_TYPE_DOMAIN = 1;
	private static final int MAX_ENTITY_TYPES_PER_TYPE_DOMAIN = 10;

	private static final int MAX_PROPERTIES_PER_ENTITY_TYPE = 20;

	private static final int MAX_INITIAL_ENTITIES = 100;

	private static boolean OVERWRITE_TEST_SUITES = true;

	private final File outputFolder;
	private final int suiteNumber;
	private final int iterations;
	private final EDRSerialiser serialiser = new OgreWireFormatSerialiser();

	private Random random;
	private BufferedWriter traceFileWriter;
	private TypeDomain typeDomain;
	private List<EntityValue> entities;
	private EntityType[] entityTypes;
	private String objectGraphId;
	private int dataVersion;
	private int dataVersionScheme = new Random().nextInt();

	public TestSuiteGenerator(int suiteNumber, int iterations, File outputFolder) {
		this.suiteNumber = suiteNumber;
		this.iterations = iterations;
		this.outputFolder = outputFolder;
	}

	public void generateTestSuite() throws IOException {
		if (outputFolder.exists() && !OVERWRITE_TEST_SUITES) {
			System.err.println("Skipping suite " + suiteNumber + " because an output folder already exists at "
					+ outputFolder.getAbsolutePath());
			return;
		}
		if (!outputFolder.exists() && !outputFolder.mkdir()) {
			System.err.println("Failed to create folder " + outputFolder.getAbsolutePath());
			return;
		}

		try {

			random = new Random(suiteNumber);
			setFile(TRACE_FILE_NAME, "");
			traceFileWriter = new BufferedWriter(new FileWriter(new File(outputFolder, TRACE_FILE_NAME)));

			typeDomain = makeRandomTypeDomain(suiteNumber);
			setFile(TYPE_DOMAIN_MESSAGE_FILE_NAME, serialiser.serialiseTypeDomain(typeDomain));
			traceLine("type domain", EDRDescriber.describeTypeDomain(typeDomain));

			entities = createObjectGraph();
			objectGraphId = "object-graph-" + suiteNumber;
			GraphUpdate initialData = new GraphUpdate(typeDomain, objectGraphId, ++dataVersion, dataVersionScheme,
					entities.toArray(new EntityValue[0]), null, null);
			setFile(INITIAL_DATA_MESSAGE_FILE_NAME, serialiser.serialiseGraphUpdate(initialData));
			traceLine("initial data set", EDRDescriber.describeObjectGraph(initialData));

			for (int i = 0; i < iterations; i++) {
				doChangeIteration(i);
			}

		} finally {
			if (traceFileWriter != null) {
				traceFileWriter.close();
				traceFileWriter = null;
			}
		}
	}

	private void doChangeIteration(int iteration) throws IOException {
		EntityValue[] existingEntities = entities.toArray(new EntityValue[0]);

		// delete some entities
		List<EntityReference> entityDeletes = new ArrayList<EntityReference>();
		for (int i : makeRandomIndices(existingEntities.length, MAX_ENTITIES_TO_DELETE_PER_ITERATION)) {
			EntityValue entityToDelete = existingEntities[i];
			entityDeletes.add(entityToDelete);
			entities.remove(entityToDelete);
			for (EntityValue entity : existingEntities) {
				entity.nullReferencesTo(entityToDelete);
			}
		}
		existingEntities = entities.toArray(new EntityValue[0]);

		// build a Map<EntityType, List<Long>> of available references
		Map<EntityType, List<Long>> availableEntities = new HashMap<EntityType, List<Long>>();
		for (EntityType entityType : entityTypes) {
			availableEntities.put(entityType, new ArrayList<Long>());
		}
		for (EntityValue entity : existingEntities) {
			availableEntities.get(entity.getEntityType()).add(entity.getEntityId());
		}

		// decide what entities we're going to add
		int numEntitiesToAdd = makeRandomNumberInclusive(0, MAX_ENTITIES_TO_ADD_PER_ITERATION);
		EntityType[] typesToAdd = new EntityType[numEntitiesToAdd];
		long[] idsToAdd = new long[numEntitiesToAdd];
		for (int i = 0; i < numEntitiesToAdd; i++) {
			typesToAdd[i] = makeRandomChoice(entityTypes);
			idsToAdd[i] = makeId();
			availableEntities.get(typesToAdd[i]).add(idsToAdd[i]);
		}

		// add the new entities
		EntityValue[] newEntities = new EntityValue[numEntitiesToAdd];
		for (int i = 0; i < numEntitiesToAdd; i++) {
			EntityValue newEntity = makeRandomEntityValue(typesToAdd[i], idsToAdd[i], availableEntities);
			entities.add(newEntity);
			newEntities[i] = newEntity;
		}

		// modify existing entities

		int qtyToChange = makeRandomNumberInclusive(0,
				Math.min(MAX_ENTITIES_TO_CHANGE_PER_ITERATION, existingEntities.length));
		List<EntityValue> entitiesToChange = Arrays.asList(existingEntities);
		Collections.shuffle(entitiesToChange, random);
		List<EntityDiff> entityDiffs = new ArrayList<EntityDiff>();
		for (int i = 0; i < qtyToChange; i++) {
			EntityValue entity = entitiesToChange.get(i);
			EntityType entityType = entity.getEntityType();
			Object[] values = new Object[entityType.getPropertyCount()];
			for (int j = 0; j < values.length; j++) {
				values[i] = entity.getRawPropertyValue(entityType.getProperty(i));
			}
			for (int index : makeRandomIndices(entityType.getPropertyCount())) {
				values[index] = makeRandomPropertyValue(entityType.getProperty(index), availableEntities);
			}
			EntityValue newEntity = new EntityValue(entityType, entity.getEntityId(), values);
			EntityDiff entityDiff = EntityDiff.build(entity, newEntity);
			if (entityDiff != null) {
				entityDiffs.add(entityDiff);
			}
			entities.set(entities.indexOf(entity), newEntity);
		}

		// make update message
		GraphUpdate graphUpdate = new GraphUpdate(typeDomain, objectGraphId, ++dataVersion, dataVersionScheme,
				newEntities, entityDiffs.toArray(new EntityDiff[0]), entityDeletes.toArray(new EntityReference[0]));

		// trace text version of update message
		traceLine("update " + iteration, describeGraphUpdate(graphUpdate));
		traceLine("graph state after update " + iteration, describeObjectGraph());

		// trace text version of object graph state
		setFile(GRAPH_UPDATE_MESSAGE_FILE_PATTERN.replace("%d", String.valueOf(iteration)),
				serialiser.serialiseGraphUpdate(graphUpdate));
	}

	private String describeObjectGraph() {
		GraphUpdate objectGraph = new GraphUpdate(typeDomain, objectGraphId, dataVersion, dataVersionScheme,
				entities.toArray(new EntityValue[0]), null, null);
		Arrays.sort(objectGraph.getEntityCreates(), new EntityReferenceComparator());
		return EDRDescriber.describeObjectGraph(objectGraph);
	}

	private String describeGraphUpdate(GraphUpdate graphUpdate) {
		EntityReferenceComparator comparator = new EntityReferenceComparator();
		Arrays.sort(graphUpdate.getEntityCreates(), comparator);
		Arrays.sort(graphUpdate.getEntityUpdates(), comparator);
		Arrays.sort(graphUpdate.getEntityDeletes(), comparator);
		return EDRDescriber.describeGraphUpdate(graphUpdate);
	}

	private long idCounter = 0;

	private long makeId() {
		return ++idCounter;
		// return random.nextLong() & 0x000FFFFFFFFFFFFFL; // always positive, never greater than
		// 2^52
	}

	//
	// TYPE DOMAIN GENERATION
	//

	/**
	 * Pick {@code count} random indices from a list of size {@code size}
	 */
	private List<Integer> makeRandomIndices(int size, int count) {
		if (count > size) {
			count = size;
		}
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			indices.add(i);
		}
		Collections.shuffle(indices, random);
		return indices.subList(0, makeRandomNumberInclusive(0, count));
	}

	/**
	 * Pick a random number of random indices from a list of size {@size}
	 */
	private List<Integer> makeRandomIndices(int size) {
		return makeRandomIndices(size, size);
	}

	private TypeDomain makeRandomTypeDomain(int suiteNumber) {
		return new TypeDomain("com.berniecode.ogre.enginelib." + suiteNumber, entityTypes = makeRandomEntityTypes());
	}

	private EntityType[] makeRandomEntityTypes() {
		int length = makeRandomNumberInclusive(MIN_ENTITY_TYPES_PER_TYPE_DOMAIN, MAX_ENTITY_TYPES_PER_TYPE_DOMAIN);
		String[] names = new String[length];
		for (int i = 0; i < names.length; i++) {
			names[i] = "entityType" + i;
		}
		EntityType[] entityTypes = new EntityType[length];
		for (int i = 0; i < entityTypes.length; i++) {
			entityTypes[i] = new EntityType(names[i], makeRandomProperties(names[i], names));
		}
		return entityTypes;
	}

	private Property[] makeRandomProperties(String entityTypeName, String[] entityTypeNames) {
		int length = makeRandomNumberInclusive(0, MAX_PROPERTIES_PER_ENTITY_TYPE);
		Property[] properties = new Property[length];
		for (int i = 0; i < properties.length; i++) {
			int typeCode = makeRandomNumberInclusive(0, Property.TYPECODE_REFERENCE);
			Property property;
			String name = entityTypeName + Property.getNameForTypecode(typeCode) + "Property" + i;
			if (typeCode == Property.TYPECODE_REFERENCE) {
				String referenceTypeName = makeRandomChoice(entityTypeNames);
				property = new ReferenceProperty(name, referenceTypeName);
			} else {
				property = new Property(name, typeCode, makeRandomChoice(TRUE_OR_FALSE));
			}
			properties[i] = property;
		}
		return properties;
	}

	//
	// DATA GENERATION
	//

	private List<EntityValue> createObjectGraph() {

		// allow replace
		List<EntityValue> store = new ArrayList<EntityValue>();

		if (entityTypes.length > 0) {

			int numEntities = makeRandomNumberInclusive(0, MAX_INITIAL_ENTITIES);

			// pre-calculate the types and IDs of all entities, because makeRandomEntity() needs to
			// know them in advance
			// in order to create random references to them
			EntityType[] graphEntityTypes = new EntityType[numEntities];
			long[] graphEntityIds = new long[numEntities];
			Map<EntityType, List<Long>> idMap = new HashMap<EntityType, List<Long>>();

			for (int i = 0; i < graphEntityTypes.length; i++) {
				EntityType entityType = makeRandomChoice(entityTypes);
				long id = makeId();
				graphEntityTypes[i] = entityType;
				graphEntityIds[i] = id;
				if (!idMap.containsKey(entityType)) {
					idMap.put(entityType, new ArrayList<Long>());
				}
				idMap.get(entityType).add(id);

			}

			for (int i = 0; i < graphEntityTypes.length; i++) {
				store.add(makeRandomEntityValue(graphEntityTypes[i], graphEntityIds[i], idMap));
			}
		}

		return store;
	}

	private EntityValue makeRandomEntityValue(EntityType entityType, long id, Map<EntityType, List<Long>> idMap) {
		Object[] values = new Object[entityType.getPropertyCount()];
		for (int i = 0; i < values.length; i++) {
			values[i] = makeRandomPropertyValue(entityType.getProperty(i), idMap);
		}
		return new EntityValue(entityType, id, values);
	}

	private Object makeRandomPropertyValue(Property property, Map<EntityType, List<Long>> idMap) {
		switch (property.getTypeCode()) {
		case Property.TYPECODE_INT32:
			return random.nextInt();
		case Property.TYPECODE_INT64:
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
			if (ids == null || ids.size() == 0) {
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
		traceFileWriter.write("#\n# " + title + "\n#\n" + string + "\n");
	}

	private <T> T makeRandomChoice(T[] options) {
		return options[makeRandomNumberInclusive(0, options.length - 1)];
	}

	private <T> T makeRandomChoice(List<T> list) {
		return list.get(makeRandomNumberInclusive(0, list.size() - 1));
	}

	private String makeRandomString() {
		int length = makeRandomNumberInclusive(0, MAX_RANDOM_STRING_LENGTH);
		char[] buffer = new char[length];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (char) makeRandomNumberInclusive(28, 128);
		}
		return new String(buffer);
	}

	private byte[] makeRandomBytes() {
		int length = makeRandomNumberInclusive(0, MAX_RANDOM_STRING_LENGTH);
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
