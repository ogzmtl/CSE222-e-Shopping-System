package main.DataStructures;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

public class SkipList <E extends Comparable<E>> {
	/** Static class to contain the data and the links */
	static class SLNode<E> {
		SLNode<E>[] links;
		E data;

		/** Create a node of level m */
		@SuppressWarnings("unchecked")
		SLNode (int m, E data) {
			links = (SLNode<E>[]) Array.newInstance(this.getClass(), m); // create links
			this.data = data; // store item
		}
	}

	SLNode<E> head;
	int maxLevel;
	int maxCap;
	int size;

	public SkipList() {
		head = new SLNode<>(1, null);
		maxLevel = 1;
		maxCap = 1;
		size = 0;
	}

	/** Natural Log of 2 */
	static final double LOG2 = Math.log(2.0);
	static final Random rand = new Random();

	/**
	 * Method to generate a logarithmic distributed integer between
	 * 1 and maxLevel. i.e., 1/2 of the values returned are 1, 1/4
	 * are 2, 1/8 are 3, etc.
	 * @return a random logarithmic distributed int between 1 and
	 * maxLevel
	 */
	private int logRandom() {
		int r = rand.nextInt(maxCap);
		int k = (int) (Math.log(r + 1) / LOG2);
		if (k > maxLevel - 1) {
			k = maxLevel - 1;
		}
		return maxLevel - k;
	}

	public void insert (E data) {
		SLNode<E>[] pred = search(data);

		if (pred[0].data != null && pred[0].data.equals(data))
			return;

		SLNode<E> newNode;
		size++;
		if (size > maxCap) {
			maxLevel++;
			maxCap = computeMaxCap(maxLevel);
			head.links = Arrays.copyOf(head.links, maxLevel);
			pred = Arrays.copyOf(pred, maxLevel);
			pred[maxLevel - 1] = head;
			newNode = new SLNode<>(maxLevel, data);
		}

		else
			newNode = new SLNode<>(logRandom(), data);

		for (int i = 0; i < newNode.links.length; ++i) {
			newNode.links[i] = pred[i].links[i];
			pred[i].links[i] = newNode;
		}
	}

	private int computeMaxCap (int maxlevel) {
		return ((int) Math.pow((double) 2, (double) maxlevel)) - 1;
	}

	/**
	 * Search for an item in the list
	 * @param target The item being sought
	 * @return A SLNode array which references the predecessors
	 * of the target at each level.
	 */
	@SuppressWarnings("unchecked")
	private SLNode<E>[] search (E target) {
		SLNode<E>[] pred = (SLNode<E>[]) new SLNode[maxLevel];
		SLNode<E> current = head;
		for (int i = current.links.length - 1; i >= 0; i--) {
			while (current.links[i] != null
					&& current.links[i].data.compareTo(target) < 0) {
				current = current.links[i];
			}
			pred[i] = current;
		}
		return pred;
	}

	/**
	 * Find an object in the skip‐list
	 * @param target The item being sought
	 * @return A reference to the object in the skip‐list that matches
	 * the target. If not found, null is returned.
	 */
	public E find(E target) {
		SLNode<E>[] pred = search(target);
		if (pred[0].links[0] != null &&
				pred[0].links[0].data.compareTo(target) == 0) {
			return pred[0].links[0].data;
		} else {
			return null;
		}
	}

	public boolean isEmpty () {
		return size == 0;
	}

	@SuppressWarnings("unchecked")
	public E[] toArray() {
		E[] result = (E[]) Array.newInstance(head.links[0].data.getClass(), size);
		SLNode<E> current = head.links[0];

		for (int i = 0; i < size; ++i) {
			result[i] = current.data;
			current = current.links[0];
		}

		return result;
	}
}