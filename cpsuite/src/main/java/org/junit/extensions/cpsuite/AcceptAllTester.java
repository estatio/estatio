/*
 * @author Johannes Link (business@johanneslink.net)
 * 
 * Published under Apache License, Version 2.0 (http://apache.org/licenses/LICENSE-2.0)
 */
package org.junit.extensions.cpsuite;

public class AcceptAllTester implements ClassTester {

	public boolean acceptClassName(String className) {
		return true;
	}

	public boolean acceptInnerClass() {
		return true;
	}

	public boolean acceptClass(Class<?> clazz) {
		return true;
	}

	public boolean searchInJars() {
		return true;
	}

}