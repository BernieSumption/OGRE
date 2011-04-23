package com.berniecode.ogre.client;

import com.berniecode.ogre.enginelib.Entity;

/**
 * An object that wraps an {@link Entity}
 * 
 * @author Bernie Sumption
 */
public interface EntityProxy {
	
	/**
	 * @return the {@link Entity} that this proxy is backed by
	 */
	Entity getProxiedEntity();

}
