package com.berniecode.ogre.testsuiteclient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import com.berniecode.ogre.enginelib.ClientEngine;
import com.berniecode.ogre.enginelib.ClientTransportAdapter;
import com.berniecode.ogre.enginelib.EDRDescriber;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.GraphUpdateListener;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.server.pojods.EntityReferenceComparator;
import com.berniecode.ogre.wireformat.OgreWireFormatDeserialiser;
import com.berniecode.ogre.wireformat.OgreWireFormatSerialiser;

public class TestSuiteClient {

	public static final String TYPE_DOMAIN_MESSAGE_FILE_NAME = "type-domain.message";
	public static final String INITIAL_DATA_MESSAGE_FILE_NAME = "initial-data.message";
	public static final String GRAPH_UPDATE_MESSAGE_FILE_PATTERN = "graph-update-%d.message";
	public static final String TRACE_FILE_NAME = "java-client-trace.txt";
	public static final String REFERENCE_TRACE_FILE_NAME = "trace.txt";

	private BufferedWriter traceFileWriter;
	private final File suiteFolder;
	
	OgreWireFormatSerialiser serialiser = new OgreWireFormatSerialiser();
	OgreWireFormatDeserialiser deserialiser = new OgreWireFormatDeserialiser();

	public TestSuiteClient(File suiteFolder) {
		this.suiteFolder = suiteFolder;
	}

	public void runTestSuite() throws IOException, NoSuchThingException {

		File traceFile = new File(suiteFolder, TRACE_FILE_NAME);

		OutputStream os = new FileOutputStream(traceFile);
		os.write(new byte[0]);
		os.close();
		
		traceFileWriter = new BufferedWriter(new FileWriter(traceFile));
		
		try {

			final TypeDomain typeDomain = deserialiser.deserialiseTypeDomain(readFile(new File(suiteFolder, TYPE_DOMAIN_MESSAGE_FILE_NAME)));
			final GraphUpdate initialData = deserialiser.deserialiseGraphUpdate(readFile(new File(suiteFolder, INITIAL_DATA_MESSAGE_FILE_NAME)), typeDomain);
			traceLine("type domain", EDRDescriber.describeTypeDomain(typeDomain));
			traceLine("initial data set", EDRDescriber.describeObjectGraph(initialData));
			
			ClientEngine engine = new ClientEngine();
			String objectGraphId = initialData.getObjectGraphId();
			engine.setObjectGraphId(objectGraphId);
			engine.setTypeDomainId(typeDomain.getTypeDomainId());
			engine.setTransportAdapter(new ClientTransportAdapter() {
				public TypeDomain loadTypeDomain(String typeDomainId) throws NoSuchThingException {
					return typeDomain;
				}
				public GraphUpdate loadObjectGraph(TypeDomain typeDomain, String objectGraphId) throws NoSuchThingException {
					return initialData;
				}
				public void subscribeToGraphUpdates(TypeDomain typeDomain, String objectGraphId,
						GraphUpdateListener listener) {}
			});
			engine.initialise();
			

			
	
			for (int i = 0;; i++) {
				File updateMessage = new File(suiteFolder, GRAPH_UPDATE_MESSAGE_FILE_PATTERN.replace("%d", String.valueOf(i)));
				if (!updateMessage.exists()) {
					break;
				}
				
				GraphUpdate graphUpdate = deserialiser.deserialiseGraphUpdate(readFile(updateMessage), typeDomain);
				
				engine.acceptGraphUpdate(graphUpdate);

				traceLine("update " + i, describeGraphUpdate(graphUpdate));
				traceLine("graph state after update " + i, describeObjectGraph(engine));
			}
		
		} finally {
			traceFileWriter.close();
		}
		
		if (!Arrays.equals(readFile(traceFile), readFile(new File(suiteFolder, REFERENCE_TRACE_FILE_NAME)))) {
			OgreLog.error("Differences detected in test suite " + suiteFolder.getName());
		}
	}

	private String describeObjectGraph(ClientEngine engine) {
		GraphUpdate objectGraph = engine.createSnapshot();
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


	private void traceLine(String title, String string) throws IOException {
		traceFileWriter.write("#\n# " + title + "\n#\n" + string + "\n");
	}
	
	private byte[] readFile(File file) throws IOException {
		byte[] buffer = new byte[(int) file.length()];
		FileInputStream r = new FileInputStream(file);
		r.read(buffer);
		r.close();
		return buffer;
	}

}
