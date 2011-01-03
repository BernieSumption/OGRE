package com.berniecode.ogre.server.pojods;

import java.util.Comparator;

import com.berniecode.ogre.enginelib.EntityReference;

public class EntityReferenceComparator implements Comparator<EntityReference> {
	@Override
	public int compare(EntityReference o1, EntityReference o2) {
		if (o1.getEntityType().getEntityTypeIndex() != o2.getEntityType().getEntityTypeIndex()) {
			return compareNumbers(o1.getEntityType().getEntityTypeIndex(), o2.getEntityType().getEntityTypeIndex());
		} else {
			return compareNumbers(o1.getEntityId(), o2.getEntityId());
		}
	}

	private int compareNumbers(long o1Id, long o2Id) {
		return (o1Id < o2Id ? -1 : (o1Id == o2Id ? 0 : 1));
	}
}