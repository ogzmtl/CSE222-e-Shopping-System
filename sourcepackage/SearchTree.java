package sourcepackage;
//SearchTree interface
public interface SearchTree<E>{
	/**
	 * add method to add given element to the tree.
	 * @param item Element to be added to tree.
	 * @return boolean value indicates if addition is successful.
	 */
	boolean add(E item);

	/**
	 * Checks if given element is in the tree
	 * @param target Element to be searched.
	 * @return boolean value to indicate if tree contains the target
	 */
	boolean contains(E target);

	/**
	 * Checks if given element is in the tree and returns if it exist
	 * @param target Element to be searched
	 * @return found element
	 */
	E find(E target);

	/**
	 * Deletes given element from the tree
	 * @param target Element to be deleted from the tree
	 * @return Deleted element.
	 */
	E delete(E target);

	/**
	 * Deletes given element from the tree
	 * @param target Element to be deleted from the tree
	 * @return boolean value to indicate if removal is happened correctly
	 */
	boolean remove(E target);
}