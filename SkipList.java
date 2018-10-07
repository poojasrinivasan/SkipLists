package pxs176230;

import java.util.Iterator;
import java.util.Random;

// Skeleton for skip list implementation.

public class SkipList<T extends Comparable<? super T>> {
    static final int PossibleLevels = 33;
    int maxLevel;
    static Random random = new Random();
    Entry<T> head,tail;
    int size;
    Entry<T>[] last;

    static class Entry<E> {
	E element;
	Entry[] next;
	Entry prev;


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
        head = new Entry<T>(null,33);
        tail = new Entry<T>(null,33);
        size = 0;
        maxLevel = 1;
    }

    // Add x to list. If x already exists, reject it. Returns true if new node is added to list
    public boolean add(T x) {
        Entry<T> node = new Entry<>(x,chooseLevel());
        return true;
    }

    private void find(T x){
        Entry<T> curr = head;


    }

    // Find smallest element that is greater or equal to x
    public T ceiling(T x) {
	return null;
    }

    // Does list contain x?
    public boolean contains(T x) {
	return false;
    }

    // Return first element of list
    public T first() {
	return null;
    }

    // Find largest element that is less than or equal to x
    public T floor(T x) {
	return null;
    }

    // Return element at index n of list.  First element is at index 0.
    public T get(int n) {
	return null;
    }

    // O(n) algorithm for get(n)
    public T getLinear(int n) {
	return null;
    }

    // Optional operation: Eligible for EC.
    // O(log n) expected time for get(n). Requires maintenance of spans, as discussed in class.
    public T getLog(int n) {
        return null;
    }

    // Is the list empty?
    public boolean isEmpty() {
	return false;
    }

    // Iterate through the elements of list in sorted order
    public Iterator<T> iterator() {
	return null;
    }

    // Return last element of list
    public T last() {
	return null;
    }

    // Optional operation: Reorganize the elements of the list into a perfect skip list
    // Not a standard operation in skip lists. Eligible for EC.
    public void rebuild() {
	
    }

    // Remove x from list.  Removed element is returned. Return null if x not in list
    public T remove(T x) {
	return null;
    }

    // Return the number of elements in the list
    public int size() {
	return 0;
    }

    private int chooseLevel(){
     int lev = 1 +Integer.numberOfTrailingZeros(random.nextInt());
     if(lev > maxLevel){
      maxLevel = lev;
     }
     return lev;
    }


    public static void main(String[] args){
        SkipList sk = new SkipList();
        sk.add(1);
    }
}
