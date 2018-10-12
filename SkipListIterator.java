package pxs176230;

import java.util.Iterator;

public interface SkipListIterator<T> extends Iterator<T> {
   boolean hasPrevious();
   T prev();
   void add(T x);
}
