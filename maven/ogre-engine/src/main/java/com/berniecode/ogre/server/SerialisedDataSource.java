/*
 * Copyright 2011 Bernie Sumption. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. THIS SOFTWARE IS PROVIDED ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package com.berniecode.ogre.server;

import java.util.HashSet;
import java.util.Set;

import com.berniecode.ogre.EDRSerialiser;
import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.enginelib.DataSource;
import com.berniecode.ogre.enginelib.EDRDescriber;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.GraphUpdateListener;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.wireformat.OgreWireFormatSerialiser;

/**
 * Wraps a {@link DataSource}, serialising its EDR output into a binary format
 * 
 * <p>
 * Whereas {@link DataSource} is designed to be easy for 3rd parties to implement,
 * {@link SerialisedDataSource} is designed for performance and thread safety.
 * 
 * @author Bernie Sumption
 */
public class SerialisedDataSource extends InitialisingBean {

	// private Object[] CHANGE_LOCK = new Object();

	private DataSource dataSource;
	private EDRSerialiser serialiser;

	//
	// CONFIGURATION AND INITIALISATION
	//

	/**
	 * Create an initialised {@link SerialisedDataSource} with the specified {@link DataSource} and
	 * {@link EDRSerialiser}
	 * 
	 * @param serialiser an {@link EDRSerialiser}, or null to use the default {@link EDRSerialiser}
	 */
	public SerialisedDataSource(DataSource dataSource, EDRSerialiser serialiser) {
		this.dataSource = dataSource;
		this.serialiser = serialiser;
		initialise();
	}

	/**
	 * Create an uninitialised {@link SerialisedDataSource}
	 */
	public SerialisedDataSource() {
	}

	/**
	 * Provide the {@link DataSource} that this {@link SerialisedDataSource} wraps. A value must be
	 * provied before {@link #initialise()} is called
	 */
	public void setDataSource(DataSource dataSource) {
		requireInitialised(false, "setDataSource()");
		this.dataSource = dataSource;
	}

	/**
	 * Provide an {@link EDRSerialiser}. If no value is provided before {@link #initialise()} is
	 * called, an {@link OgreWireFormatSerialiser} willbe used.
	 */
	public void setSerialiser(EDRSerialiser serialiser) {
		requireInitialised(false, "setSerialiser()");
		this.serialiser = serialiser;
	}

	@Override
	protected void doInitialise() {
		requireNotNull(dataSource, "dataSource");
		if (serialiser == null) {
			serialiser = new OgreWireFormatSerialiser();
		}
		TypeDomain initialTypeDomain = dataSource.getTypeDomain();
		typeDomain = serialiser.serialiseTypeDomain(initialTypeDomain);
		objectGraphId = dataSource.getObjectGraphId();

		if (OgreLog.isDebugEnabled()) {
			OgreLog.debug("SerialisedDataSource: initialised with type domain: " + EDRDescriber.describeTypeDomain(initialTypeDomain));
		}

		dataSource.setGraphUpdateListener(new GraphUpdateListener() {
			@Override
			public void acceptGraphUpdate(GraphUpdate update) {
				doAcceptGraphUpdate(update);
			}
		});
	}

	//
	// PUBLIC API
	//

	private byte[] typeDomain;
	private String objectGraphId;
	private byte[] currentSnapshot;

	Set<Listener> listeners = new HashSet<Listener>();

	private final Object SNAPSHOT_CACHE_LOCK = new Object();

	public byte[] getTypeDomain() {
		return typeDomain;
	}

	public String getObjectGraphId() {
		return objectGraphId;
	}

	/**
	 * @return A snapshot of the state of the object graph
	 */
	public byte[] getCurrentSnapshot() {
		synchronized (SNAPSHOT_CACHE_LOCK) {
			if (currentSnapshot == null) {
				currentSnapshot = serialiser.serialiseGraphUpdate(dataSource.createSnapshot());
			}
			return currentSnapshot;
		}
	}

	/**
	 * Add a listener to be notified about graph updates. If the listener has already been added,
	 * there is no effect
	 */
	public void addSerialisedGraphUpdateListener(Listener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	/**
	 * Remove a listener that has previously been added with
	 * {@link #addSerialisedGraphUpdateListener(Listener)}
	 */
	public void removeSerialisedGraphUpdateListener(Listener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	/**
	 * Interface to be implemented by any objects wanting to register for serialised graph updates
	 *
	 * @author Bernie Sumption
	 */
	public static interface Listener {
		void acceptSerialisedGraphUpdate(byte[] update);
	}

	private void doAcceptGraphUpdate(GraphUpdate graphUpdate) {
		OgreLog.info("SerialisedDataSource: broadcasting graph update " + graphUpdate);
		if (OgreLog.isDebugEnabled()) {
			OgreLog.debug(EDRDescriber.describeGraphUpdate(graphUpdate));
		}
		synchronized (SNAPSHOT_CACHE_LOCK) {
			currentSnapshot = null;
		}
		Set<Listener> copy;
		synchronized (listeners) {
			copy = new HashSet<Listener>(listeners);
		}
		byte[] update = serialiser.serialiseGraphUpdate(graphUpdate);
		for (Listener listener: copy) {
			listener.acceptSerialisedGraphUpdate(update);
		}
	}

}
