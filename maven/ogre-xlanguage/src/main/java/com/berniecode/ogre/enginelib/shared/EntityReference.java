package com.berniecode.ogre.enginelib.shared;

/**
 * A (type, id) tuple that can be used to locate an {@link Entity}.
 *
 * @author Bernie Sumption
 */
public interface EntityReference {

	public abstract int getEntityTypeIndex();

	public abstract long getEntityId();

}