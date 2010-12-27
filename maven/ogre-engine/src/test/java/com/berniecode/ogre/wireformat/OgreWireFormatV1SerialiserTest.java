package com.berniecode.ogre.wireformat;

import com.berniecode.ogre.EntityClassWithAllFields;
import com.berniecode.ogre.EntityClassWithAllFieldsTestCase;
import com.berniecode.ogre.EntityElement;
import com.berniecode.ogre.enginelib.EDRDescriber;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.TypeDomain;

public class OgreWireFormatV1SerialiserTest extends EntityClassWithAllFieldsTestCase {

	public void testTypeDomainSerialisation() {
		
		OgreWireFormatV1Serialiser serialiser = new OgreWireFormatV1Serialiser();
		
		byte[] serialisedTD = serialiser.serialiseTypeDomain(typeDomain);
		
		TypeDomain deserialisedTD = serialiser.deserialiseTypeDomain(serialisedTD);
		
		assertEqualsIgnoreWhitespace(EDRDescriber.describeTypeDomain(typeDomain), EDRDescriber.describeTypeDomain(deserialisedTD));
		
	}
	
	public void testGraphUpdateSerialisation() {
		
		// generate some changes
		EntityElement[] elements = new EntityElement[] {new EntityElement("A"), new EntityElement("B"), new EntityElement("C")};
		dataSource.setEntityObjects(initialEntityObject, elements[0], elements[1], elements[2]);
		initialEntityObject.setBytes(byteArray(11, 12, 13));
		initialEntityObject.setNullableInt(null);
		initialEntityObject.setNonNullableLong(42L);
		initialEntityObject.setString("Fizzle");
		initialEntityObject.setNonNullableDouble(11770.0);
		initialEntityObject.setNullableDouble(null);
		initialEntityObject.setNullableFloat(1144.0F);
		elements[0].setName("X");
		EntityClassWithAllFields newEntity = new EntityClassWithAllFields((byte)-1, (byte)-2, (short)-3, (short)-4, -5, -6, -7L, -8L, "Raaa", -9.0F, -10.0F, -11.0, -12.0, byteArray(-1, -2, -3), new EntityElement("Y"));
		dataSource.setEntityObjects(initialEntityObject, elements[0], newEntity);

		GraphUpdate graphUpdate = msgBridge.getLastGraphUpdate();
		
		System.out.println(EDRDescriber.describeGraphUpdate(typeDomain, graphUpdate));
		
		OgreWireFormatV1Serialiser serialiser = new OgreWireFormatV1Serialiser();
		byte[] serialisedGU = serialiser.serialiseGraphUpdate(graphUpdate);
		GraphUpdate deserialisedGU = serialiser.deserialiseGraphUpdate(serialisedGU, typeDomain);
		assertEqualsIgnoreWhitespace(
				EDRDescriber.describeGraphUpdate(typeDomain, graphUpdate),
				EDRDescriber.describeGraphUpdate(typeDomain, deserialisedGU));
	}

}
