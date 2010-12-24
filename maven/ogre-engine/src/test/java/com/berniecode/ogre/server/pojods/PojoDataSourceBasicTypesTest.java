package com.berniecode.ogre.server.pojods;

import java.util.Date;

import com.berniecode.ogre.AbstractHasId;
import com.berniecode.ogre.HasIdMapper;
import com.berniecode.ogre.OgreTestCase;
import com.berniecode.ogre.enginelib.OgreLog;

public class PojoDataSourceBasicTypesTest extends OgreTestCase {

	public void testTypeDomainCreation() throws Exception {
		
		PojoDataSource dataSource = createInitialisedDataSource(SimpleEntityClassOne.class, SimpleEntityClassNoFields.class);
		
		assertTypeDomainState(
			"TypeDomain TypeDomain" +
			"  0. EntityType SimpleEntityClassNoFields" +
			"  1. EntityType SimpleEntityClassOne" +
			"       32 bit integer property public_int_property",
			dataSource.getTypeDomain());
	}
	
	public void testAddingEntityOfWrongTypeFails() {
		
		PojoDataSource dataSource = createInitialisedDataSource(SimpleEntityClassNoFields.class);
		
		try {
			dataSource.setEntityObjects("A string");
			fail("addEntityObjects() should fail with an unmapped class");
		} catch (ValueMappingException e) {
		}
	}

	public void testAssignmentOfIds() {
		
		PojoDataSource dataSource = createInitialisedDataSource(SimpleEntityClassNoFields.class);
		
		Object e0 = new SimpleEntityClassNoFields("e0");
		Object e1 = new SimpleEntityClassNoFields("e1");
		Object e2 = new SimpleEntityClassNoFields("e2");
		
		assertFalse(dataSource.containsEntityObject(e0));

		// which we then add twice
		dataSource.setEntityObjects(e0, e2, e2, e0 ,e0); // e0: #1, e2: #2
		dataSource.setEntityObjects(e2, e0);
		dataSource.setEntityObjects(e0, e2, e1); // e1: #3
		dataSource.setEntityObjects(e1, e1);

		assertTrue(dataSource.containsEntityObject(e0));

		assertEquals(1, dataSource.getIdForObject(e0));
		assertEquals(3, dataSource.getIdForObject(e1));
		assertEquals(2, dataSource.getIdForObject(e2));
	}
	
	public void testUnMappableTypesAreIgnored() {
		PojoDataSource dataSource = createInitialisedDataSource(EntityClassWithDateProperty.class);
		assertTypeDomainState(
				"TypeDomain TypeDomain" +
				"  0. EntityType EntityClassWithDateProperty" +
				"       string property dummy",
				dataSource.getTypeDomain());
	}
	
	public void testMappingFailsForUnmappableType() {

		try {
			createInitialisedDataSource(EnumType.class);
			fail("A TypeMappingException should have been thrown when trying to map an enum type");
		} catch (TypeMappingException e) {
		}

		try {
			createInitialisedDataSource(int.class);
			fail("A TypeMappingException should have been thrown when trying to map a primitive type");
		} catch (TypeMappingException e) {
		}

		try {
			createInitialisedDataSource(new InterfaceWithNoMethods() {}.getClass());
			fail("A TypeMappingException should have been thrown when trying to map an anonymous type");
		} catch (TypeMappingException e) {
		}

		try {
			createInitialisedDataSource(SimpleInterface[].class);
			fail("A TypeMappingException should have been thrown when trying to map an array type");
		} catch (TypeMappingException e) {
		}
	}
	
	public void testExceptionRethrownFromGetter() {
		PojoDataSource dataSource = createInitialisedDataSource(EntityClassWithPropertyThatThrowsException.class);
		try {
			dataSource.setEntityObjects(new EntityClassWithPropertyThatThrowsException());
			fail("A ValueMappingException should have been thrown when trying to access a property getter that throws an exception");
		} catch (ValueMappingException e) {
			assertNotNull(e.getCause());
		}
	}
	
	public void testInterfaceTypeCanBeUsedForMapping() {
		PojoDataSource dataSource = createInitialisedDataSource(SimpleInterface.class);

		assertTypeDomainState(
			"TypeDomain TypeDomain" +
			"  0. EntityType SimpleInterface" +
			"       32 bit integer property public_int_property",
			dataSource.getTypeDomain());
		
		dataSource.setEntityObjects(new SimpleEntityClassOne());

		assertObjectGraphState(
			"ObjectGraph TypeDomain/TestObjectGraph" +
			"  Entity SimpleInterface#1" +
			"    public_int_property=10",
			dataSource.createSnapshot(), dataSource.getTypeDomain());
	}
	
	public void testTypesCantBeSupertypesOfEachOther() {
		try {
			createInitialisedDataSource(SimpleInterface.class, SimpleInterfaceChild.class);
			fail("A TypeMappingException should have been thrown when trying to map a type and its supertype");
		} catch (TypeMappingException e) {
		}

		try {
			createInitialisedDataSource(SimpleInterfaceChild.class, SimpleInterface.class);
			fail("A TypeMappingException should have been thrown when trying to map a type and its supertype");
		} catch (TypeMappingException e) {
		}
	}
	
	public void testEntityObjectsCanOnlyMatchOneType() {
		PojoDataSource dataSource = createInitialisedDataSource(InterfaceWithNoMethods.class, SimpleInterface.class);
		try {
			dataSource.setEntityObjects(new EntityClassMatchingTwoInterfaces());
			fail("A ValueMappingException should have been thrown when trying to map a object that matches two entity classes");
		} catch (ValueMappingException e) {
		}
	}
	
	public void testCustomMappers() {
		PojoDataSource dataSource = new PojoDataSource();
		dataSource.setEDRMapper(new EDRMapperImplementation());
		dataSource.setObjectGraphId(OBJECT_GRAPH_ID);
		dataSource.initialise();

		assertTypeDomainState(
				"TypeDomain custom-typedomain-name" +
				"  0. EntityType custom-entity-name" +
				"       32 bit integer property public_int_property",
				dataSource.getTypeDomain());
		
		dataSource.setEntityObjects(new SimpleEntityClassOne());

		assertObjectGraphState(
				"ObjectGraph custom-typedomain-name/TestObjectGraph" +
				"  Entity custom-entity-name#42" +
				"    public_int_property=10",
				dataSource.createSnapshot(), dataSource.getTypeDomain());
	}
	
	public void testLogWarningWhenClassOverridesEquals() {
		requireOneLogError(OgreLog.LEVEL_WARN);
		createInitialisedDataSource(ClassWithCustomEquals.class);
	}
	
	public void testDifferentObjectsWithSameId() {
		PojoDataSource dataSource = createInitialisedDataSource(new HasIdMapper(), SimpleInterface.class);
		
		dataSource.setEntityObjects(new SimpleInterfaceImpl(10L), new SimpleInterfaceImpl(100L));

		assertObjectGraphState(
			"ObjectGraph TypeDomain/TestObjectGraph" +
			"  Entity SimpleInterface#10" +
			"    public_int_property=8" +
			"  Entity SimpleInterface#100" +
			"    public_int_property=98",
			dataSource.createSnapshot(), dataSource.getTypeDomain());
	}
}

interface SimpleInterface {
	int getPublicIntProperty();
}

class SimpleInterfaceImpl extends AbstractHasId implements SimpleInterface {

	public SimpleInterfaceImpl(long id) {
		super(id);
	}

	@Override
	public int getPublicIntProperty() {
		return (int) _getId() - 2;
	}
	
}

interface SimpleInterfaceChild extends SimpleInterface {
	int getPublicIntProperty2();
}

interface InterfaceWithNoMethods {
}

class EntityClassMatchingTwoInterfaces implements InterfaceWithNoMethods, SimpleInterface {
	@Override
	public int getPublicIntProperty() {
		return 0;
	}	
}

class SimpleEntityClassOne implements SimpleInterface {
	@Override
	public int getPublicIntProperty() {
		return 10;
	}
	int getDefaultIntProperty() {
		return 20;
	}
	public int badGetterNameProperty() {
		return 30;
	}

	public int sh() {
		return 30;
	}

	public int getPropertyWithArgument(String arg) {
		return 30;
	}
}

class SimpleEntityClassNoFields {
	private final String name;

	public SimpleEntityClassNoFields(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}

class EntityClassWithDateProperty {
	public Date getDate() {
		return new Date();
	}
	public String getDummy() {
		return null;
	}
}

enum EnumType {
	A, B, C
}

class EntityClassWithPropertyThatThrowsException {
	public int getBadProperty() {
		throw new RuntimeException("the message");
	}
}

class ClassWithCustomEquals {
	public String getName() {
		return "dummy";
	}
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}


class IdMapperImplementation implements IdMapper {
	@Override
	public long getIdForObject(Object entityObject) {
		return 42;
	}

	@Override
	public boolean objectHasId(Object entityObject) {
		return false;
	}
}

class EDRMapperImplementation extends DefaultEDRMapper {
	
	public EDRMapperImplementation() {
		setTypeDomainId("custom-typedomain-name");
		setClasses(SimpleInterface.class);
		setIdMapper(new IdMapperImplementation());
		initialise();
	}
	
	@Override
	protected String getEntityTypeNameForClass(Class<?> klass) {
		return "custom-entity-name";
	}
}