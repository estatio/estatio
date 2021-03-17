/*
 * @author Johannes Link (business@johanneslink.net)
 * 
 * Published under Apache License, Version 2.0 (http://apache.org/licenses/LICENSE-2.0)
 */
package org.junit.extensions.cpsuite;

import java.io.*;
import java.util.*;
import java.util.jar.*;

/**
 * This class provides an iterator over all file names in a jar file.
 * Directories are not considered to be files.
 */
public class JarFilenameIterator implements Iterator<String>, Iterable<String> {

	private Enumeration<JarEntry> entries;

	private JarEntry next;

	public JarFilenameIterator(File jarFile) throws IOException {
		JarFile jar = new JarFile(jarFile);
		entries = jar.entries();
		retrieveNextElement();
	}

	private void retrieveNextElement() {
		next = null;
		while (entries.hasMoreElements()) {
			next = entries.nextElement();
			if (!next.isDirectory()) {
				break;
			}
		}
	}

	public boolean hasNext() {
		return next != null;
	}

	public String next() {
		if (next == null) {
			throw new NoSuchElementException();
		}
		String value = next.getName();
		retrieveNextElement();
		return value;
	}

	public void remove() {
		throw new RuntimeException("Not implemented");
	}

	public Iterator<String> iterator() {
		return this;
	}

}
