package com.berniecode.ogre.client;

import java.util.Collection;

public interface ClientFacade {
	
	Collection<Class<?>> getEntityClasses();
	
	<T> Collection<T> getEntitiesByType(Class<T> entityClass);

}
