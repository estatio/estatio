/*
 * @author Johannes Link (business@johanneslink.net)
 * 
 * Published under Apache License, Version 2.0 (http://apache.org/licenses/LICENSE-2.0)
 */
package org.junit.extensions.cpsuite;

import java.io.File;
import java.util.*;

/**
 * This class provides an iterator over all file names in a directory and its
 * subdirectories. The filenames are given relative to the root. Directories are
 * not considered to be files.
 */
public class RecursiveFilenameIterator implements Iterator<String>, Iterable<String> {

	private List<RecursiveFilenameIterator> innerIterators;

	private int prefixLength;

	private File root;

	private boolean alreadyUsed = false;

	private int index = 0;

	public RecursiveFilenameIterator(File root) {
		this(root, root.getAbsolutePath().length() + 1);
	}

	private RecursiveFilenameIterator(File root, int prefixLength) {
		this.root = root;
		this.prefixLength = prefixLength;
		if (!isRootFile()) {
			innerIterators = getInnerIterators(root);
		}
	}

	private boolean isRootFile() {
		return this.root.isFile();
	}

	private List<RecursiveFilenameIterator> getInnerIterators(File root) {
		List<RecursiveFilenameIterator> iterators = new ArrayList<RecursiveFilenameIterator>();
		for (File each : root.listFiles()) {
			iterators.add(new RecursiveFilenameIterator(each, prefixLength));
		}
		return iterators;
	}

	public boolean hasNext() {
		if (isRootFile()) {
			return !alreadyUsed;
		}
		if (index >= innerIterators.size()) {
			return false;
		}
		if (currentIterator().hasNext()) {
			return true;
		}
		index++;
		return hasNext();
	}

	private RecursiveFilenameIterator currentIterator() {
		return innerIterators.get(index);
	}

	public String next() {
		if (isRootFile()) {
			if (alreadyUsed) {
				throw new NoSuchElementException();
			}
			alreadyUsed = true;
			return root.getAbsolutePath().substring(prefixLength);
		}
		if (hasNext()) {
			return currentIterator().next();
		}
		throw new NoSuchElementException();
	}

	public void remove() {
		throw new RuntimeException("Not implemented");
	}

	public Iterator<String> iterator() {
		return this;
	}

}
