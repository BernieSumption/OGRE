package com.berniecode.ogre;

import com.berniecode.ogre.enginelib.shared.TypeDomain;

public interface EDRDeserialiser {
	
	TypeDomain deserialiseTypeDomain(byte[] message);

}
