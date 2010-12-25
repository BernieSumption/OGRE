package com.berniecode.ogre.wireformat;

import com.berniecode.ogre.EntityClassWithAllFields;
import com.berniecode.ogre.EntityElement;
import com.berniecode.ogre.OgreTestCase;
import com.berniecode.ogre.enginelib.shared.EDRDescriber;
import com.berniecode.ogre.enginelib.shared.TypeDomain;
import com.berniecode.ogre.server.pojods.DefaultEDRMapper;

public class OgreWireFormatV1SerialiserTest extends OgreTestCase {

	public void testTypeDomainSerialisation() {
		
		DefaultEDRMapper mapper = new DefaultEDRMapper();
		mapper.setClasses(EntityClassWithAllFields.class, EntityElement.class);
		mapper.setTypeDomainId(TYPE_DOMAIN_ID);
		mapper.initialise();
		TypeDomain typeDomain = mapper.getTypeDomain();
		
		OgreWireFormatV1Serialiser serialiser = new OgreWireFormatV1Serialiser();
		
		byte[] serialisedTD = serialiser.serialiseTypeDomain(typeDomain);
		
		TypeDomain deserialisedTD = serialiser.deserialiseTypeDomain(serialisedTD);
		
		assertState(EDRDescriber.describeTypeDomain(typeDomain), EDRDescriber.describeTypeDomain(deserialisedTD));
		
	}

}
