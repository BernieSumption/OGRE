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

package com.berniecode.ogre.wireformat;

import com.berniecode.ogre.EntityClassWithAllFieldsImpl;
import com.berniecode.ogre.EntityClassWithAllFieldsTestCase;
import com.berniecode.ogre.EntityElementImpl;
import com.berniecode.ogre.enginelib.EDRDescriber;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.TypeDomain;

public class OgreWireFormatV1SerialiserTest extends EntityClassWithAllFieldsTestCase {

	public void testTypeDomainSerialisation() {

		OgreWireFormatSerialiser serialiser = new OgreWireFormatSerialiser();

		byte[] serialisedTD = serialiser.serialiseTypeDomain(typeDomain);

		TypeDomain deserialisedTD = new OgreWireFormatDeserialiser().deserialiseTypeDomain(serialisedTD);

		assertEqualsIgnoreWhitespace(EDRDescriber.describeTypeDomain(typeDomain),
				EDRDescriber.describeTypeDomain(deserialisedTD));

	}

	public void testGraphUpdateSerialisation() {

		// generate some changes
		EntityElementImpl[] elements = new EntityElementImpl[] { new EntityElementImpl("A"),
				new EntityElementImpl("B"), new EntityElementImpl("C") };
		dataSource.setEntityObjects(initialEntityObject, elements[0], elements[1], elements[2]);
		initialEntityObject.setBytes(byteArray(11, 12, 13));
		initialEntityObject.setNullableInt(null);
		initialEntityObject.setNonNullableLong(42L);
		initialEntityObject.setString("Fizzle");
		initialEntityObject.setNonNullableDouble(11770.0);
		initialEntityObject.setNullableDouble(null);
		initialEntityObject.setNullableFloat(1144.0F);
		elements[0].setName("X");
		EntityClassWithAllFieldsImpl newEntity = new EntityClassWithAllFieldsImpl(-5, -6, -7L, -8L, "Raaa", -9.0F,
				-10.0F, -11.0, -12.0, byteArray(-1, -2, -3), new EntityElementImpl("Y"));
		dataSource.setEntityObjects(initialEntityObject, elements[0], newEntity);

		GraphUpdate graphUpdate = transport.getLastGraphUpdate();

		byte[] serialisedGU = new OgreWireFormatSerialiser().serialiseGraphUpdate(graphUpdate);
		GraphUpdate deserialisedGU = new OgreWireFormatDeserialiser().deserialiseGraphUpdate(serialisedGU, typeDomain);
		assertEqualsIgnoreWhitespace(EDRDescriber.describeGraphUpdate(graphUpdate),
				EDRDescriber.describeGraphUpdate(deserialisedGU));
	}

}
