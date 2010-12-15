package com.berniecode.ogre;

/**
 * An object with an ID. This is used to explicitly set object IDs in test cases, in cases where
 * arbitrarily assigned IDs are hard to predict and write tests for
 *
 * @author Bernie Sumption
 */
public interface HasId {
	
	// not a getter
	public long _getId();

}
