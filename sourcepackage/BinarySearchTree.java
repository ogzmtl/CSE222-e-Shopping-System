package sourcepackage;
@SuppressWarnings("unchecked")
public class BinarySearchTree<E> extends BinaryTree<E> implements SearchTree<E>{
	protected boolean adder;
	protected E deleter;

	/**
	 * No parameter constructor
	 */
	public BinarySearchTree(){
		super();
		adder = false;
		deleter = null;
	}

	/**
	 * add method to add given element to the tree.
	 * @param element Element to be added to tree.
	 * @return boolean value indicates if addition is successful.
	 */
	public boolean add(E element){
		root = add(root, element);
		return adder;
	}

	private Node<E> add(Node<E> rootValue, E element){
		if (rootValue == null){
			adder = true;
			return new Node<E>(element);
		}
		else if (((Comparable<E>)element).compareTo(rootValue.data) == 0){
			adder = false;
			return rootValue;
		}
		else if (((Comparable<E>)element).compareTo(rootValue.data) < 0){
			rootValue.left = add(rootValue.left, element);
			return rootValue;
		}
		else{
			rootValue.right = add(rootValue.right, element);
			return rootValue;
		}
	}

	/**
	 * Checks if given element is in the tree
	 * @param target Element to be searched.
	 * @return boolean value to indicate if tree contains the target
	 */
	public boolean contains(E target){
		if (find(target) != null) return true;
		else return false;
	}

	/**
	 * Checks if given element is in the tree and returns if it exist
	 * @param target Element to be searched
	 * @return found element
	 */
	public E find(E target){
		return find(root, target);
	}

	private E find(Node<E> rootValue, E target){
		if (rootValue == null) return null;
		int compResult = ((Comparable<E>)target).compareTo(rootValue.data);
		if (compResult == 0) return rootValue.data;
		else if (compResult < 0) return find(rootValue.left, target);
		else return find(rootValue.right, target);
	}

	/**
	 * Deletes given element from the tree
	 * @param target Element to be deleted from the tree
	 * @return Deleted element.
	 */
	public E delete(E target){
		root = delete(root, target);
		return deleter;
	}

	private Node<E> delete(Node<E> rootValue, E element){
		if (rootValue == null){
			deleter = null;
			return rootValue;
		}
		int compResult = ((Comparable<E>)element).compareTo(rootValue.data);
		if (compResult < 0){
			rootValue.left = delete(rootValue.left, element);
			return rootValue;
		}
		else if (compResult > 0){
			rootValue.right = delete(rootValue.right, element);
			return rootValue;
		}
		else{
			deleter = rootValue.data;
			if (rootValue.left == null) return rootValue.right;
			else if (rootValue.right == null) return rootValue.left;
			else{
				if (rootValue.left.right == null){
					rootValue.data = rootValue.left.data;
					rootValue.left = rootValue.left.left;
					return rootValue;
				}
				else{
					rootValue.data = largestChild(rootValue.left);
					return rootValue;
				}
			}
		}
	}

	/**
	 * Deletes given element from the tree
	 * @param target Element to be deleted from the tree
	 * @return boolean value to indicate if removal is happened correctly
	 */
	public boolean remove(E target){
		if (delete(target) != null) return true;
		else return false;
	}

	private E largestChild(Node<E> parent){
		if (parent.right.right == null){
			E returnValue = parent.right.data;
			parent.right = parent.right.left;
			return returnValue;
		}
		else return largestChild(parent.right);
	}
}