package pxs176230;
import java.util.Arrays;
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
	int[] steps;

	static class Entry<E extends Comparable<? super E>> {
		E element;
		Entry<E>[] next;
		int[] span;
		Entry<E> prev;

		public Entry(E x, int lev) {
			element = x;
			next = new Entry[lev];
			span = new int[lev];
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
			head.span[i] = 0;
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

		int step = 0;
		Entry<T> node = new Entry<>(x, level);
		for (int i = 0; i < level; i++) {
			node.next[i] = last[i].next[i];
			last[i].next[i] = node;			

			if(node.next[i] != tail)
				node.span[i] = last[i].span[i] - step;
			else
				node.span[i] = 0;
		    last[i].span[i] = step + 1;
		    step += steps[i];
		}
		
		for(int i=level ; i < maxLevel; i++) {
			if(last[i].next[i] != tail)
				last[i].span[i] += 1;
		    step += steps[i];
		}
		
		node.next[0].prev = node;
		node.prev = last[0];
		
		size = size + 1;
		return true;
	}

	private void find(T x) {
		Entry<T> cur = head;
		steps = new int[33];
		for (int i = maxLevel - 1; i >= 0; i--) {
			while (cur.next[i] != tail && cur.next[i].element.compareTo(x) < 0) {
				steps[i] += cur.span[i];
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
		if(n<0 || n>size-1)
			throw new NoSuchElementException();
		
		return getLog(n);
	}

	// O(n) algorithm for get(n)
	public T getLinear(int n) {
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
		Entry<T> cur = head;
		n += 1;
		for(int lev=maxLevel-1; lev>=0; lev--) {
			while(lev<cur.span.length && cur.span[lev] <= n && cur.span[lev] > 0) {
				n -= cur.span[lev];
				cur = cur.next[lev];
			}
			if (n==0) break;
		}
		return cur.element;
	}

	// Is the list empty?
	public boolean isEmpty() {
		return size()==0;
	}

	// Iterate through the elements of list in sorted order
	public Iterator<T> iterator() {
		return new ListIterator();
	}

	private class ListIterator implements Iterator<T> {
	    Entry<T> cursor;
	    ListIterator(){
           cursor = head;
        }
        public boolean hasNext(){
           if(cursor.next[0]==tail) return false;
           return true;
        }
        public T next(){
	        cursor = cursor.next[0];
	        return cursor.element;
        }
    }

	// Return last element of list
	public T last() {
		return tail.prev!=head ? tail.prev.element : null;
	}

	// Optional operation: Reorganize the elements of the list into a perfect skip
	// list
	// Not a standard operation in skip lists. Eligible for EC.
	public void rebuild() {
        Entry<T>[] entryArray = new Entry[size];
        int maxLevel = (int)Math.ceil(Math.log(size)/Math.log(2));
		rebuild(entryArray,0,size-1,maxLevel);
		SkipList<T> newSkipList = new SkipList<>();
		newSkipList.maxLevel = maxLevel;
		Entry<T>[] lastArray = new Entry[maxLevel];
		Arrays.fill(lastArray,newSkipList.head);
		Iterator<T> itr = this.iterator();
		int index = 0;
		while(itr.hasNext()){
			entryArray[index].element = itr.next();
			entryArray[index].prev = lastArray[0];
			for(int i = 0; i < entryArray[index].next.length; i++){
				Entry<T> prev = lastArray[i];
				Entry<T> next = prev.next[i];
				prev.next[i] = entryArray[index];
				entryArray[index].next[i] = next;
				lastArray[i] = entryArray[index];
			}
			index++;
			newSkipList.size++;
		}
		this.head = newSkipList.head;
	}

	//creates nextarray level using divide and conquer
	private void rebuild(Entry<T>[] newArray,int low,int high,int level){
		if(low <= high){
			if(level==1){
				newArray[low] = new Entry<>(null,level);
			  if(newArray[high]==null){
					newArray[high] = new Entry<>(null,level);
				}
			}
			else {
				int mid = (low + high) / 2;
				newArray[mid] = new Entry<>(null, level);
				rebuild(newArray, low, mid - 1, level - 1);
				rebuild(newArray, mid + 1, high, level - 1);
			}
		}
	}

	// Remove x from list. Removed element is returned. Return null if x not in list
	public T remove(T x) {
		if (!contains(x))
			return null;

		Entry<T> cur = last[0].next[0];
		for (int i = 0; i < cur.next.length; i++) {
			last[i].next[i] = cur.next[i];
			
			if (last[i].next[i] != tail)
				last[i].span[i] += cur.span[i]-1;
			else
				last[i].span[i] = 0;
		}
		
		for(int i = cur.next.length; i < maxLevel; i++) {
			if(last[i].next[i] != tail)
				last[i].span[i] -= 1;
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
	
	private void printLevels() {
		for(int i=0; i<maxLevel;i++) {
			Entry<T> cur = head;
			System.out.print("Level " + i + ": ");
			while(cur!=tail) {
				System.out.print(cur.element + "(" + cur.span[i] + ")->");
				cur = cur.next[i];
			}
			System.out.println("null");
		}
	}

	public static void main(String[] args) {
		SkipList<Integer> sk = new SkipList<>();
        for(int i = 99 ; i >= 1; i--){
        	sk.add(i);
		}
		//sk.printList();
		//System.out.println(sk.maxLevel);
	    sk.rebuild();

		/*sk.remove(3);
		System.out.println(sk.first());
		sk.add(1);
		sk.add(3);
		sk.add(5);
		sk.add(7);
		sk.add(4);
		sk.remove(3);
		sk.remove(4);
		/*System.out.println(sk.first());
		System.out.println(sk.last());
		System.out.println(sk.ceiling(0));
		System.out.println(sk.floor(0));*/
		sk.printList();
		sk.printLevels();
		System.out.println(sk.get(2));

		sk.printList();
		//SkipListIterator<Integer> it = sk.skipListIterator();
	/*	if(it.hasNext()){
		    System.out.println(it.next());
        }
        if(it.hasNext()){
        	System.out.println(it.next());
		}
		while(it.hasPrevious()){
			System.out.println(it.prev());
		}*/
        /*if(it.hasNext()){
			it.next();
			it.add(2);
		}
		while(it.hasNext()){
			System.out.println(it.next());
		}
		it.add(9);
		while(it.hasPrevious()){
			System.out.println(it.prev());
		}
*/

	}
}
















