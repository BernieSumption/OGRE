
package com.berniecode.ogre.server.pojods;

import java.util.Comparator;

import com.berniecode.ogre.enginelib.shared.Named;

/**
 * ValueUtils two {@link Named} objects
 *
 * @author Bernie Sumption
 */
final class NamedComparator implements Comparator<Named> {
	
	public static final NamedComparator INSTANCE = new NamedComparator();
	
	// private constructor
	private NamedComparator() {}

	@Override
	public int compare(Named o1, Named o2) {
		return o1.getName().compareTo(o2.getName());
	}
}