package com.berniecode.ogre.enginelib;

/**
 * This class can be used to bypass the normal safe API and access the raw arrays inside EDR objects
 * like {@link Entity}s and {@link TypeDomain}s
 * 
 * <p>Any algorithms using this class must take care not to modify the returned arrays, which will
 * cause undefined and likely undesirable behaviour.
 * 
 * <p>Using this class to <i>deliberately</i> modify the internal state of an EDR object is asking for
 * trouble. Like, so much trouble ;o)
 * 
 * @private
 * 
 * @author Bernie Sumption
 */
public class UnsafeAccess {

	public static EntityType[] getEntityTypes(TypeDomain typeDomain) {
		return typeDomain.getEntityTypes();
	}
}
