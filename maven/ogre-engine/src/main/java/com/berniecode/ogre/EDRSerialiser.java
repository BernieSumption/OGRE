package com.berniecode.ogre;

import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.TypeDomain;

/**
 * An {@link EDRSerialiser} converts OGRE's Entity Data Representation into a binary format suitable
 * for sending over the network
 * 
 * @author Bernie Sumption
 */
public interface EDRSerialiser {
	
	/**
	 * Serialise a {@link TypeDomain} into a binary message.
	 */
	public byte[] serialiseTypeDomain(TypeDomain typeDomain);
	
	/**
	 * Serialise a {@link TypeDomain} into a binary message.
	 */
	public byte[] serialiseGraphUpdate(GraphUpdate graphUpdate);
	
}
