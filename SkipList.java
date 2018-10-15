/** 
 * @author Akhila Perabe (axp178830), Pooja Srinivasan (pxs176230), Shreeya Girish Degaonkar (sxd174830) 
 * Implementation of Skip lists with operations like add(), remove(), contains(), floor(), ceiling(), get(), rebuild() etc. 
 */

package sxd174830;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;



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
	/**
	 * 
	 * @param x
	 * @return boolean value: true (if new node added) or false
	 */
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

	/**
	 * 
	 * @param x
	 * helper method to search for 'x'
	 */
	
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

	/**
	 * 
	 * @param x
	 * @return boolean value: true if skip list contains x, else false
	 */
	// Does list contain x?
	public boolean contains(T x) {
		find(x);
		return last[0].next[0] != tail && last[0].next[0].element.compareTo(x) == 0;
	}
	
	/**
	 * 
	 * @return element of type T
	 */
	// Return first element of list
	public T first() {
		return head.next[0]!=tail ? head.next[0].element : null;
	}
	
	/**
	 * 
	 * @param x
	 * @return element of type T
	 */

	// Find smallest element that is greater or equal to x
	public T ceiling(T x) {
		find(x);
		if(last[0].next[0]!=tail) {
			return last[0].next[0].element;
		}
		return null;
	}
	
	/**
	 * 
	 * @param x
	 * @return element of type T
	 */
	// Find largest element that is less than or equal to x
	public T floor(T x) {
		if(contains(x))
			return x;
		
		if(last[0].next[0]!=tail || size() > 0) {
				return last[0].element;
		}
		return null;
	}

	/**
	 * 
	 * @param n
	 * @return element of type T
	 */
	// Return element at index n of list. First element is at index 0.
	public T get(int n) {
		if(n<0 || n>size-1)
			throw new NoSuchElementException();
		
		return getLog(n);
	}

	/**
	 * 
	 * @param n
	 * @return element of Type T. Returns element at index n
	 */
	// O(n) algorithm for get(n)
	public T getLinear(int n) {
		Iterator<T> it = iterator();
		int i=-1;
		T cur = null;
		while(i<n && it.hasNext()) {
			cur = it.next();
			i++;
		}
		return cur;
	}
	
	
	/**
	 * 
	 * @param n
	 * @return element of type T. Returns element at index n
	 */
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

	/**
	 * 
	 * @return boolean value: true if the list is empty, else false 
	 */
	// Is the list empty?
	public boolean isEmpty() {
		return size()==0;
	}

	/**
	 * 
	 * @return a ListIterator<> for going through elements 
	 */
	// Iterate through the elements of list in sorted order
	public Iterator<T> iterator() {
		return new ListIterator();
	}

	/**
	 * implementation of methods of the iterator class: hasNext(), hasPrevious(), next(), prev(), add(), remove()
	 */
	private class ListIterator implements Iterator<T> {
	    Entry<T> cursor;
	    boolean isReady;
	    ListIterator(){
           cursor = head;
           isReady = false;
        }
        public boolean hasNext(){
           if(cursor.next[0]==tail) return false;
           return true;
        }
        public T next(){
	        cursor = cursor.next[0];
	        isReady = true;
	        return cursor.element;
        }
         public boolean hasPrevious(){
	        if(cursor == head) return false;
	        return true;
        }
         public T prev(){
	        isReady = true;
	        T element = cursor.element;
	        cursor = cursor.prev;
	        return element;
        }
        // add method inserts the element before the element that will be returned by call to next
        public void add(T x){
	        int lev = chooseLevel();
	        Entry<T> newNode = new Entry<>(x,lev);
 	        // if a value is inserted after head, checking only the next element range
	        if(cursor == head && cursor.next[0]!=tail && cursor.next[0].element.compareTo(x) < 0 ){
                System.out.println("Can't insert this element as it violates ordering constraint");
                return;
            }
            // if value is to be inserted before tail, checking only left range
            if(cursor.next[0] == tail && cursor != head && cursor.element.compareTo(x) > 0){
                System.out.println("Can't insert this element as it violates ordering constraint");
                return;
            }
            // else check if x lies between left and right range
            if((cursor.element!=null && cursor.element.compareTo(x) > 0) || (cursor.next[0].element!=null && cursor.next[0].element.compareTo(x) < 0)){
				System.out.println("Can't insert this element as it violates ordering constraint");
				return;
			}
            cursor.next[0].prev = newNode;
            newNode.next[0] = cursor.next[0];
            cursor.next[0] = newNode;
            newNode.prev = cursor;
            //
            cursor = cursor.next[0];
            size++;
	    }
         /**
         * Removes the current element (retrieved by the most recent next())
         * Remove can be called only if next has been called and the element has not been removed
         */
        public void remove(){
            if(size == 0) {
                System.out.println("List is empty.NoSuchElementException");
                return;
            }
            if(!isReady) {
                System.out.println("next or prev method not called. NoSuchElementException");
                return;
            }
            cursor.prev.next[0] = cursor.next[0];
            cursor.next[0].prev = cursor.prev;
            cursor = cursor.prev;
            size--;
        }
    }

	/**
	 * 
	 * @return element of type T, the last element of the list
	 */
	// Return last element of list
	public T last() {
		return tail.prev!=head ? tail.prev.element : null;
	}

	// Optional operation: Reorganize the elements of the list into a perfect skip
	// list
	public void rebuild() {
        Entry<T>[] entryArray = new Entry[size];
        int maxLevel = (int)Math.ceil(Math.log(size)/Math.log(2));
        //creates entry nodes and populates next pointers
		rebuild(entryArray,0,size-1,maxLevel);
		SkipList<T> newSkipList = new SkipList<>();
		newSkipList.maxLevel = maxLevel;
		//keeps track of previous pointers for each level
		Entry<T>[] lastArray = new Entry[maxLevel];
		//keeps track of previous indexes for each level
		int[] indexArray = new int[maxLevel];
		Arrays.fill(indexArray,-1);
		Arrays.fill(lastArray,newSkipList.head);
		Iterator<T> itr = this.iterator();
		int index = 0;
		while(itr.hasNext()){
			entryArray[index].element = itr.next();
			entryArray[index].prev = lastArray[0];
			//updates prev and next pointer
			for(int i = 0; i < entryArray[index].next.length; i++){
				Entry<T> prev = lastArray[i];
				Entry<T> next = prev.next[i];
				prev.next[i] = entryArray[index];
				entryArray[index].next[i] = next;
				prev.span[i] = index - indexArray[i];
				lastArray[i] = entryArray[index];
				indexArray[i] = index;
			}
			index++;
			newSkipList.size++;
		}
		newSkipList.tail.prev = lastArray[0];
		this.head = newSkipList.head;
		this.tail = newSkipList.tail;
	}

	/**
	 * 
	 * @param newArray
	 * @param low
	 * @param high
	 * @param level
	 */
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
	/**
	 * 
	 * @param x
	 * @return element of type T, the element that was removed
	 */

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

	/**
	 * 
	 * @return result of type int, the size of the list
	 */
	// Return the number of elements in the list
	public int size() {
		return size;
	}

	/**
	 * 
	 * @return value of type int, the level
	 */
	private int chooseLevel() {
		int lev = 1 + Integer.numberOfTrailingZeros(random.nextInt());
		if (lev > maxLevel) {
			maxLevel = lev;
		}
		return lev;
	}

	/**
	 * prints the elements of the list
	 */
	private void printList() {
		Iterator<T> it = iterator();
		while(it.hasNext()) {
			System.out.print(it.next() + " ");
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

}
