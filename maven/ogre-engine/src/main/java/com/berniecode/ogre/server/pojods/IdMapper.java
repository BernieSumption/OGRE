package com.berniecode.ogre.server.pojods;

/**
 * An {@link IdMapper} knows how to get an ID from an object managed by {@link PojoDataSource}. If
 * your objects have a natural source of persistent IDs, e.g. a getId() method, create an
 * {@link IdMapper} to expose this ID.
 * 
 * @author Bernie Sumption
 */
public interface IdMapper {

	/**
	 * @return the ID of the specified object
	 */
	long getId(Object entityObject);

	/**
	 * Check whether an object has an ID
	 */
	boolean hasId(Object entityObject);

}
