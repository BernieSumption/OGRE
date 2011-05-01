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

package com.berniecode.ogre.enginelib;

/**
 * A ServerDataAdapter is a view onto a single object graph that converts the object graph into EDR
 * (Entity Data Representation). The object graph can be any structured data. By writing new
 * DataSource implementations, you can cause OGRE to run off any source of structured data.
 * 
 * <p>
 * Implementations do not need to be thread safe, or perform any caching
 * 
 * @author Bernie Sumption
 */
public interface DataSource {

	TypeDomain getTypeDomain();

	/**
	 * @return the ID of the object graph produced by this data source
	 */
	String getObjectGraphId();

	/**
	 * @return A snapshot of the state of the object graph
	 */
	GraphUpdate createSnapshot();

	/**
	 * Set the listener that will be notified about updates to the object graph.
	 */
	void setGraphUpdateListener(GraphUpdateListener listener);
}
