package com.berniecode.ogre.server.pojods;

import java.util.Map;
import java.util.WeakHashMap;

import com.berniecode.ogre.enginelib.OgreLog;

/**
 * An {@link IdMapper} that assigns incrementing IDs to objects.
 * 
 * <p>
 * This implementation works fine, but if your objects do have a real ID you can expose, you should
 * do so in order to make debugging easier.
 * 
 * @author Bernie Sumption
 */
public class DefaultIdMapper implements IdMapper {
	
	Map<Object, Long> idMap = new WeakHashMap<Object, Long>();
	
	long idCounter = 1;

	@Override
	public synchronized long getIdForObject(Object object) {
		
		//TODO log an error if mapping a class that overrides object.equals
		
		Long existingId = idMap.get(object);
		if (existingId != null) {
			return existingId;
		}
		long thisId = idCounter++;
		OgreLog.debug("Assigned id " +  thisId + " to object '" + object + "'");
		idMap.put(object, thisId);
		return thisId;
	}


	@Override
	public synchronized boolean objectHasId(Object entityObject) {
		return idMap.containsKey(entityObject);
	}

}
