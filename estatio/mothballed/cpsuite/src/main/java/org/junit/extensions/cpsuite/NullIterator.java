/*
 * @author Johannes Link (business@johanneslink.net)
 * 
 * Published under Apache License, Version 2.0 (http://apache.org/licenses/LICENSE-2.0)
 */
package org.junit.extensions.cpsuite;

import java.util.*;

public class NullIterator<T> implements Iterable<T>, Iterator<T> {

	public Iterator<T> iterator() {
		return this;
	}

	public boolean hasNext() {
		return false;
	}

	public T next() {
		throw new NoSuchElementException();
	}

	public void remove() {
	}

}
