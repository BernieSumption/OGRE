package com.berniecode.ogre;

import java.util.Arrays;

import junit.framework.TestCase;

import org.hamcrest.core.IsNot;
import org.jmock.Expectations;
import org.jmock.Mockery;

import com.berniecode.ogre.enginelib.ClientEngine;
import com.berniecode.ogre.enginelib.EDRDescriber;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.LogWriter;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.PartialRawPropertyValueSet;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.platformhooks.StdErrLogWriter;
import com.berniecode.ogre.server.IdMapper;
import com.berniecode.ogre.server.pojods.DefaultEDRMapper;
import com.berniecode.ogre.server.pojods.EntityReferenceComparator;
import com.berniecode.ogre.server.pojods.PojoDataSource;

public abstract class OgreTestCase extends TestCase {
	
	protected Mockery context;

	protected static final String TYPE_DOMAIN_ID = "TypeDomain";
	protected static final String OBJECT_GRAPH_ID = "TestObjectGraph";
	

	@Override
	public final void setUp() throws Exception {
		OgreLog.setLogWriter(new StdErrLogWriter());
		OgreLog.setLevel(OgreLog.LEVEL_DEBUG);
		context = new Mockery();
		doAdditionalSetup();
	}
	
	@Override
	protected void tearDown() throws Exception {
		context.assertIsSatisfied();
	}

	protected void doAdditionalSetup() throws Exception {}

	public OgreTestCase() {
		super();
	}

	public OgreTestCase(String name) {
		super(name);
	}

	protected void assertEqualsIgnoreWhitespace(String expected, String actual) {
		String packagePrefix = getClass().getPackage().getName() + ".";
		String expectedMunged = expected == null ? null : expected.replaceAll("\\s+", "\n").replace(packagePrefix, "").replace("com.berniecode.ogre.", "");
		String actualMunged = actual == null ? null : actual.replaceAll("\\s+", "\n").replace(packagePrefix, "").replace("com.berniecode.ogre.", "");
		assertEquals(expectedMunged, actualMunged);
		OgreLog.debug("assertEqualsIgnoreWhitespace values are equal:\n" + actual);
	}
	
	protected void assertTypeDomainState(String expected, TypeDomain typeDomain) {
		assertEqualsIgnoreWhitespace(expected, EDRDescriber.describeTypeDomain(typeDomain));
	}
	
	protected void assertObjectGraphState(String expected, GraphUpdate objectGraph, TypeDomain typeDomain) {
		EntityReferenceComparator comparator = new EntityReferenceComparator();
		Arrays.sort(objectGraph.getEntityCreates(), comparator);
		assertEqualsIgnoreWhitespace(expected, EDRDescriber.describeObjectGraph(objectGraph));
	}
	
	protected void assertGraphUpdateState(String expected, GraphUpdate graphUpdate, TypeDomain typeDomain) {
		EntityReferenceComparator comparator = new EntityReferenceComparator();
		Arrays.sort(graphUpdate.getEntityCreates(), comparator);
		Arrays.sort(graphUpdate.getEntityDeletes(), comparator);
		Arrays.sort(graphUpdate.getEntityUpdates(), comparator);
		assertEqualsIgnoreWhitespace(expected, EDRDescriber.describeGraphUpdate(graphUpdate));
	}
	
	protected void assertEntityUpdateState(String expected, PartialRawPropertyValueSet entityUpdate, TypeDomain typeDomain) {
		assertEqualsIgnoreWhitespace(expected, EDRDescriber.describeEntityUpdate(entityUpdate));
	}
	
	protected void assertClientEngineState(String expected, ClientEngine actual) {
		assertObjectGraphState(expected, actual.createSnapshot(), actual.getTypeDomain());
	}

	/**
	 * Add a context expectation for one log message of a specific level
	 */
	protected void requireOneLogError(final int level) {
		final LogWriter mockLogWriter = context.mock(LogWriter.class);
		
		OgreLog.setLogWriter(new StdErrLogWriter() {
			@Override
			public void acceptMessage(int level, String levelDescription, String message) {
				super.acceptMessage(level, levelDescription, message);
				mockLogWriter.acceptMessage(level, levelDescription, message);
			}
		});
	
		context.checking(new Expectations() {{
			allowing (mockLogWriter).acceptMessage(with(IsNot.not((equal(level)))), with(any(String.class)), with(any(String.class)));
		    oneOf (mockLogWriter).acceptMessage(with(equal(level)), with(any(String.class)), with(any(String.class)));
		}});
	}

	protected PojoDataSource createInitialisedDataSource(Class<?> ... classes) {
		return createInitialisedDataSource(null, classes);
	}

	protected PojoDataSource createInitialisedDataSource(IdMapper idMapper, Class<?> ... classes) {
		PojoDataSource dataSource = new PojoDataSource();
		DefaultEDRMapper edrMapper = new DefaultEDRMapper();
		edrMapper.setTypeDomainId(TYPE_DOMAIN_ID);
		edrMapper.setClasses(classes);
		edrMapper.setIdMapper(idMapper);
		edrMapper.initialise();
		dataSource.setEDRMapper(edrMapper);
		dataSource.setObjectGraphId(OBJECT_GRAPH_ID);
		dataSource.initialise();
		return dataSource;
	}

}