package com.berniecode.ogre;

import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.TypeDomain;

public interface EDRDeserialiser {

	/**
	 * Deserialise a binary message created with a compatible
	 * {@link EDRSerialiser#serialiseTypeDomain(TypeDomain)} back into a {@link TypeDomain}
	 */
	TypeDomain deserialiseTypeDomain(byte[] message);

	/**
	 * Deserialise a binary message created with a compatible
	 * {@link EDRSerialiser#serialiseGraphUpdate(GraphUpdate)} back into a {@link GraphUpdate}
	 * belonging to the specified {@link TypeDomain}
	 */
	GraphUpdate deserialiseGraphUpdate(byte[] message, TypeDomain typeDomain);

}
