package com.berniecode.ogre.engine.shared;

/**
 * A simple list interface, used in preference to {@link java.util.List} because it is smaller, and
 * therefore easier to re-implement in other languages
 * 
 * @author Bernie Sumption
 */
public interface OrderedCollection {

	/**
	 * @return the number of elements in this collection.
	 */
	int size();

	/**
	 * @return a specific item from the list
	 */
	Object get(int index);

	/**
	 * @return the position of an item in this collection, or -1 if the item is not in the
	 *         collection
	 */
	int indexOf(Object item);

	/**
	 * Add an item to the collection. After it is added, the new item be at position `size - 1`
	 */
	void push(Object item);
}
