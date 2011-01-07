package com.berniecode.ogre.client;

import java.lang.reflect.Proxy;

import com.berniecode.ogre.enginelib.ClientEngine;

public class ClientFacadeFactory {

	public static <T extends ClientFacade> T createFacade(Class<T> klass, ClientEngine clientEngine) {
		return createFacade(klass, clientEngine, new DefaultEDRToJavaMapper());
	}

	public static <T extends ClientFacade> T createFacade(Class<T> klass, ClientEngine clientEngine, EDRToJavaMapper mapper) {
		Object proxy = Proxy.newProxyInstance(
				klass.getClassLoader(),
				new Class[] { klass },
				new ClientFacadeInvocationHandler(klass, clientEngine, mapper));
		return klass.cast(proxy);
	}

}
