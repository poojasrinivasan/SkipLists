package pxs176230;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

// Skeleton for skip list implementation.

public class SkipList<T extends Comparable<? super T>> {
	static final int PossibleLevels = 33;
	int maxLevel;
	static Random random = new Random();
	Entry<T> head, tail;
	int size;
	Entry<T>[] last;

	static class Entry<E extends Comparable<? super E>> {
		E element;
		Entry<E>[] next;
		Entry<E> prev;

		public Entry(E x, int lev) {
			element = x;
			next = new Entry[lev];
			// add more code if needed
		}

		public E getElement() {
			return element;
		}
	}

	// Constructor
	public SkipList() {
		head = new Entry<T>(null, 33);
		tail = new Entry<T>(null, 33);
		
		for(int i = 0; i <33; i++) {
			head.next[i] = tail;
		}
		tail.prev = head;
		
		size = 0;
		maxLevel = 1;
		last = new Entry[33];
	}

	// Add x to list. If x already exists, reject it. Returns true if new node is
	// added to list
	public boolean add(T x) {

		int level = chooseLevel();

		if (contains(x))
			return false;

		Entry<T> node = new Entry<>(x, level);
		for (int i = 0; i < level; i++) {
			node.next[i] = last[i].next[i];
			last[i].next[i] = node;
		}
		
		node.next[0].prev = node;
		node.prev = last[0];
		
		size = size + 1;
		return true;
	}

	private void find(T x) {
		Entry<T> cur = head;
		for (int i = maxLevel - 1; i >= 0; i--) {
			while (cur.next[i] != tail && cur.next[i].element.compareTo(x) < 0) {
				cur = cur.next[i];
			}
			last[i] = cur;
		}
	}

	// Does list contain x?
	public boolean contains(T x) {
		find(x);
		return last[0].next[0] != tail && last[0].next[0].element.compareTo(x) == 0;
	}

	// Return first element of list
	public T first() {
		return head.next[0]!=tail ? head.next[0].element : null;
	}

	// Find smallest element that is greater or equal to x
	public T ceiling(T x) {
		find(x);
		if(last[0].next[0]!=tail) {
			return last[0].next[0].element;
		}
		return null;
	}
	
	// Find largest element that is less than or equal to x
	public T floor(T x) {
		if(contains(x))
			return x;
		
		if(last[0].next[0]!=tail || size() > 0) {
				return last[0].element;
		}
		return null;
	}

	// Return element at index n of list. First element is at index 0.
	public T get(int n) {
		return getLinear(n);
	}

	// O(n) algorithm for get(n)
	public T getLinear(int n) {
		if(n<0 || n>size-1)
			throw new NoSuchElementException();
		
		Entry<T> cur = head.next[0];
		int i=0;
		while(cur!=tail && i<n) {
			cur=cur.next[0];
			i++;
		}
		return cur.element;
	}

	// Optional operation: Eligible for EC.
	// O(log n) expected time for get(n). Requires maintenance of spans, as
	// discussed in class.
	public T getLog(int n) {
		return null;
	}

	// Is the list empty?
	public boolean isEmpty() {
		return size()==0;
	}

	// Iterate through the elements of list in sorted order
	public Iterator<T> iterator() {
		return null;
	}

	// Return last element of list
	public T last() {
		return tail.prev!=head ? tail.prev.element : null;
	}

	// Optional operation: Reorganize the elements of the list into a perfect skip
	// list
	// Not a standard operation in skip lists. Eligible for EC.
	public void rebuild() {

	}

	// Remove x from list. Removed element is returned. Return null if x not in list
	public T remove(T x) {
		if (!contains(x))
			return null;

		Entry<T> cur = last[0].next[0];
		for (int i = 0; i < cur.next.length; i++) {
			last[i].next[i] = cur.next[i];
		}
		
		cur.next[0].prev = cur.prev;
		size--;
		return cur.element;
	}

	// Return the number of elements in the list
	public int size() {
		return size;
	}

	private int chooseLevel() {
		int lev = 1 + Integer.numberOfTrailingZeros(random.nextInt());
		if (lev > maxLevel) {
			maxLevel = lev;
		}
		return lev;
	}

	private void printList() {
		Entry<T> cur = head.next[0];
		while (cur != tail) {
			System.out.print(cur.element + " ");
			cur = cur.next[0];
		}
		System.out.println();
	}

	public static void main(String[] args) {
		SkipList<Integer> sk = new SkipList<>();
		sk.add(1);
		sk.add(3);
		sk.add(3);
		sk.add(7);
		sk.add(4);
		sk.remove(3);
		System.out.println(sk.first());
		System.out.println(sk.last());
		System.out.println(sk.ceiling(0));
		System.out.println(sk.floor(0));
		System.out.println(sk.get(2));
		sk.printList();

	}
}
