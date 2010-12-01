package com.berniecode.ogre.server.pojods;

import java.util.Set;

import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.Utils;
import com.berniecode.ogre.engine.server.DataSource;
import com.berniecode.ogre.engine.shared.TypeDomain;

/**
 * A {@link DataSource} that extracts type information from java classes and entity data information
 * from java objcts
 * 
 * @author Bernie Sumption
 */
public class PojoDataSource extends InitialisingBean implements DataSource {

	private Set<Class<?>> classes;

	TypeDomainMapper typeDomainMapper = new DefaultTypeDomainMapper();
	private String typeDomainId;
	TypeDomain typeDomain;

	// Check that all required fields are present
	@Override
	protected void doInitialise() {
		requireNotNull(typeDomainMapper, "typeDomainMapper");
		requireNotNull(typeDomainId, "typeDomainId");
	}

	/**
	 * Set the classes used to create the type domain. Must be called before initialise();
	 */
	public void setClasses(Class<?>... classes) {
		this.classes = Utils.arrayToSet(classes);
	}

	/**
	 * Provide an alternative TypeDomainMapper. If not set, {@link DefaultTypeDomainMapper} will be
	 * used
	 */
	public void setTypeDomainMapper(TypeDomainMapper typeDomainMapper) {
		this.typeDomainMapper = typeDomainMapper;
	}

	/**
	 * Must be called before initialise();
	 */
	public void setTypeDomainId(String typeDomainId) {
		this.typeDomainId = typeDomainId;
	}

	@Override
	public TypeDomain getTypeDomain() {
		requireInitialised(true, "getTypeDomain()");
		if (typeDomain == null) {
			typeDomain = typeDomainMapper.mapTypeDomain(typeDomainId, classes);
		}
		return typeDomain;
	}

}
