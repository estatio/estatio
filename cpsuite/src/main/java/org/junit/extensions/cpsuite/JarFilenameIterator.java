/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
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
