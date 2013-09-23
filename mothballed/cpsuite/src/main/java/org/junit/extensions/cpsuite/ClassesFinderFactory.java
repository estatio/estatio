/*
 * @author Johannes Link (business@johanneslink.net)
 * 
 * Published under Apache License, Version 2.0 (http://apache.org/licenses/LICENSE-2.0)
 */
package org.junit.extensions.cpsuite;

public interface ClassesFinderFactory {
	ClassesFinder create(boolean searchInJars, String[] filterPatterns, SuiteType[] suiteTypes, Class<?>[] baseTypes,
			Class<?>[] excludedBaseTypes, String classpathProperty);
}
