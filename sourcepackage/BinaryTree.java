package sourcepackage;
import java.io.*;
import java.util.*;
public class BinaryTree<E> implements Serializable{
	protected static class Node<E> implements Serializable{
		protected E data;
		protected Node<E> left;
		protected Node<E> right;

		public Node(E data){
			this.data = data;
			left = null;
			right = null;
		}

		public String toString(){
			return data.toString();
		}
	}

	protected Node<E> root;

	/**
	 * No parameter constructor
	 */
	public BinaryTree(){
		root = null;
	}

	protected BinaryTree(Node<E> root){
		this.root = root;
	}

	/**
	 * Constructor to start the tree with given left tree, right tree, and the root data
	 */
	public BinaryTree(E data, BinaryTree<E> leftTree, BinaryTree<E> rightTree){
		root = new Node<E>(data);
		if (leftTree != null) root.left = leftTree.root;
		else root.left = null;

		if (rightTree != null) root.right = rightTree.root;
		else root.right = null;
	}

	/**
	 * This method returns left subtree
	 * @return Left subtree
	 */
	public BinaryTree<E> getLeftSubtree(){
		if (root != null && root.left != null) return new BinaryTree<E>(root.left);
		else return null;
	}

	/**
	 * This method returns right subtree
	 * @return Right subtree
	 */
	public BinaryTree<E> getRightSubtree(){
		if (root != null && root.right != null) return new BinaryTree<E>(root.right);
		else return null;
	}

	/**
	 * Getter for the data.
	 * @return Returns data of the root.
	 */
	public E getData(){
		if (root != null) return root.data;
		else return null;
	}

	/**
	 * Checks if the root is leaf
	 * @return boolean value to indicate if root is leaf
	 */
	public boolean isLeaf(){
		return (root.left == null && root.right == null);
	}

	/**
	 * Overridden toString method
	 * @return String representation of the tree
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		traverse(root, 1, sb);
		return sb.toString();
	}

	/**
	 * Traversing in the tree
	 */
	private void traverse(Node<E> node, int depth, StringBuilder sb){
		for (int i = 1; i < depth; ++i) sb.append(" ");
		if (node == null) sb.append("null\n");
		else{
			sb.append(node.toString());
			sb.append("\n");
			traverse(node.left, depth+1, sb);
			traverse(node.right, depth+1, sb);
		}

	}

	/**
	 * Reads binary tree from given scanner
	 */
	public static BinaryTree<String> readBinaryTree(Scanner scan){
		String data = scan.next();
		if (data.equals("null")) return null;
		else{ 
			BinaryTree<String> leftTree = readBinaryTree(scan);
			BinaryTree<String> rightTree = readBinaryTree(scan);
			return new BinaryTree<String>(data, leftTree, rightTree);
		}
	}
}