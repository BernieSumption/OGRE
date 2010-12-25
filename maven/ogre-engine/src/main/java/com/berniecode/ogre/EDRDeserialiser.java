package com.berniecode.ogre;

import com.berniecode.ogre.enginelib.shared.GraphUpdate;
import com.berniecode.ogre.enginelib.shared.TypeDomain;

public interface EDRDeserialiser {
	
	TypeDomain deserialiseTypeDomain(byte[] message);
	
	GraphUpdate deserialiseGraphUpdate(byte[] message);

}
