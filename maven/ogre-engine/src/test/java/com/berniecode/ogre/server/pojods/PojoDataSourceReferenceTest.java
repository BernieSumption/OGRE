package com.berniecode.ogre.server.pojods;

import com.berniecode.ogre.AbstractHasId;
import com.berniecode.ogre.HasIdMapper;
import com.berniecode.ogre.OgreTestCase;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.GraphUpdateListener;
import com.berniecode.ogre.enginelib.OgreLog;

public class PojoDataSourceReferenceTest extends OgreTestCase {
	
	private GraphUpdate lastGraphUpdate;

	public void testCircularReferences() throws Exception {
		
		PojoDataSource dataSource = createInitialisedDataSource(new HasIdMapper(), A.class, B.class);
		
		dataSource.setGraphUpdateListener(new GraphUpdateListener() {
			@Override
			public void acceptGraphUpdate(GraphUpdate update) {
				lastGraphUpdate = update;
			}
		});
		
		assertTypeDomainState(
			"TypeDomain TypeDomain" +
			"  0. EntityType A" +
			"       reference to B property b" +
			"  1. EntityType B" +
			"       reference to A property a",
			dataSource.getTypeDomain());
		
		A a1 = new A(1);
		A a2 = new A(2);

		B b1 = new B(1);
		B b2 = new B(2);
		B b3 = new B(3);
		
		a1.setB(b2);
		a2.setB(b1);
		
		b1.setA(a2);
		b2.setA(a2);
		b3.setA(a2);
		
		// initial load by traversing tree
		
		dataSource.setEntityObjects(a1);
		
		assertObjectGraphState(
				"ObjectGraph TypeDomain/TestObjectGraph" +
				"  Entity A#1" +
				"    b=B#2" +
				"  Entity A#2" +
				"    b=B#1" +
				"  Entity B#1" +
				"    a=A#2" +
				"  Entity B#2" +
				"    a=A#2",
				dataSource.createSnapshot(), dataSource.getTypeDomain());
		
		// change tree indirectly, expect annd and deletes
		
		a2.setB(b3);
		dataSource.setEntityObjects(a1);
		
		assertGraphUpdateState(
				"GraphUpdate for object graph TypeDomain/TestObjectGraph" +
				"  complete values:" +
				"    PartialRawPropertyValueSet for B#3" +
				"      a=A#2" +
				"  partial values:" +
				"    PartialRawPropertyValueSet for A#2" +
				"      b=B#3" +
				"  deleted entities:" +
				"    EntityDelete for B#1",
				lastGraphUpdate, dataSource.getTypeDomain());
		
		
		// change back again - expect thrashing
		
		a2.setB(b1);
		dataSource.setEntityObjects(a1);
		
		assertGraphUpdateState(
				"GraphUpdate for object graph TypeDomain/TestObjectGraph" +
				"  complete values:" +
				"    PartialRawPropertyValueSet for B#1" +
				"      a=A#2" +
				"  partial values:" +
				"    PartialRawPropertyValueSet for A#2" +
				"      b=B#1" +
				"  deleted entities:" +
				"    EntityDelete for B#3",
				lastGraphUpdate, dataSource.getTypeDomain());

		// add all b's as root nodes - expect no thrashing

		dataSource.setEntityObjects(a1, b1, b2, b3);
		
		
		assertGraphUpdateState(
				"GraphUpdate for object graph TypeDomain/TestObjectGraph" +
				"  complete values:" +
				"    PartialRawPropertyValueSet for B#3" +
				"      a=A#2",
				lastGraphUpdate, dataSource.getTypeDomain());
		
		
		a2.setB(b3);
		dataSource.setEntityObjects(a1, b1, b2, b3);
		
		assertGraphUpdateState(
				"GraphUpdate for object graph TypeDomain/TestObjectGraph" +
				"  partial values:" +
				"    PartialRawPropertyValueSet for A#2" +
				"      b=B#3",
				lastGraphUpdate, dataSource.getTypeDomain());
		
		a2.setB(b1);
		dataSource.setEntityObjects(a1, b1, b2, b3);
		
		assertGraphUpdateState(
				"GraphUpdate for object graph TypeDomain/TestObjectGraph" +
				"  partial values:" +
				"    PartialRawPropertyValueSet for A#2" +
				"      b=B#1",
				lastGraphUpdate, dataSource.getTypeDomain());
	}
	
	public void testMethodsReturningSubtypesAreNotMappedAsSupertype() {
		
		requireOneLogError(OgreLog.LEVEL_WARN);
		
		PojoDataSource dataSource = createInitialisedDataSource(I1.class, I2.class);

		assertTypeDomainState(
			"TypeDomain TypeDomain" +
			"  0. EntityType I1" +
			"  1. EntityType I2" +
			"       nullable string property property",
			dataSource.getTypeDomain());
		

		dataSource.setEntityObjects(new I1Impl());
		
		
		assertObjectGraphState(
				"ObjectGraph TypeDomain/TestObjectGraph" +
				"  Entity I1#1",
				dataSource.createSnapshot(), dataSource.getTypeDomain());
	}
	
}

class A extends AbstractHasId {
	
	private B b;
	
	public A(long id) {
		super(id);
	}

	public void setB(B b) {
		this.b = b;
	}

	public B getB() {
		return b;
	}
}

class B extends AbstractHasId {
	
	private A a;
	
	public B(long id) {
		super(id);
	}

	public void setA(A a) {
		this.a = a;
	}

	public A getA() {
		return a;
	}
}

interface I1 {
	public I2Impl getI2();
}

class I1Impl implements I1 {

	@Override
	public I2Impl getI2() {
		return new I2Impl();
	}
	
}

interface I2 {
	String getProperty();
}

class I2Impl implements I2 {

	@Override
	public String getProperty() {
		return "value";
	}
	
}