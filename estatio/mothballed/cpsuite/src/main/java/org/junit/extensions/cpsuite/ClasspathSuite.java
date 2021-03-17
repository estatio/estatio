/*
 * @author Johannes Link (business@johanneslink.net)
 * 
 * Published under Apache License, Version 2.0 (http://apache.org/licenses/LICENSE-2.0)
 */
package org.junit.extensions.cpsuite;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.junit.runner.notification.*;
import org.junit.runners.Suite;
import org.junit.runners.model.*;

public class ClasspathSuite extends Suite {

	private static final boolean DEFAULT_INCLUDE_JARS = false;
	private static final SuiteType[] DEFAULT_SUITE_TYPES = new SuiteType[] { SuiteType.TEST_CLASSES };
	private static final Class<?>[] DEFAULT_BASE_TYPES = new Class<?>[] { Object.class };
	private static final Class<?>[] DEFAULT_EXCLUDED_BASES_TYPES = new Class<?>[0];
	private static final String[] DEFAULT_CLASSNAME_FILTERS = new String[0];
	private static final String DEFAULT_CLASSPATH_PROPERTY = "java.class.path";

	private final Class<?> suiteClass;

	/**
	 * The <code>ClassnameFilters</code> annotation specifies a set of regex
	 * expressions for all test classes (ie. their qualified names) to include
	 * in the test run. When the annotation is missing, all test classes in all
	 * packages will be run.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface ClassnameFilters {
		public String[] value();
	}

	/**
	 * The <code>IncludeJars</code> annotation specifies if Jars should be
	 * searched in or not. If the annotation is missing Jars are not being
	 * searched.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface IncludeJars {
		public boolean value();
	}

	/**
	 * The <code>SuiteTypes</code> annotation specifies which types of tests
	 * will be included in the test run. You can choose one or more from
	 * TEST_CLASSES, RUN_WITH_CLASSES, JUNIT38_TEST_CLASSES. If none is
	 * specified only JUnit4-style TEST_CLASSES will be run.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface SuiteTypes {
		public SuiteType[] value();
	}

	/**
	 * The <code>BaseTypeFilter</code> annotation filters all test classes to be
	 * run by one or several given base types, i.e. only those classes will be
	 * run which extend one of the base types. Default is
	 * <code>Object.class</code>.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface BaseTypeFilter {
		public Class<?>[] value();
	}

	/**
	 * The <code>ExcludeBaseTypeFilter</code> annotation filters all test
	 * classes to be run by one or several given base types, i.e. only those
	 * classes will be run which <em>do not extend</em> any of the base types.
	 * Default is <code>Object.class</code>.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface ExcludeBaseTypeFilter {
		public Class<?>[] value();
	}

	/**
	 * The <code>ClasspathProperty</code> specifies the System property name
	 * used to retrieve the java classpath which is searched for Test classes
	 * and suites. Default is "java.class.path".
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface ClasspathProperty {
		String value();
	}

	/**
	 * The <code>BeforeSuite</code> marks a method that will be run before the
	 * suite is run.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface BeforeSuite {
	}

	/**
	 * Used by JUnit
	 */
	public ClasspathSuite(Class<?> suiteClass, RunnerBuilder builder) throws InitializationError {
		this(suiteClass, builder, new ClasspathFinderFactory());
	}

	/**
	 * For testing purposes only
	 */
	public ClasspathSuite(Class<?> suiteClass, RunnerBuilder builder, ClassesFinderFactory factory) throws InitializationError {
		super(builder, suiteClass, getSortedTestclasses(createFinder(suiteClass, factory)));
		this.suiteClass = suiteClass;
	}

	private static ClassesFinder createFinder(Class<?> suiteClass, ClassesFinderFactory finderFactory) {
		return finderFactory.create(getSearchInJars(suiteClass), getClassnameFilters(suiteClass), getSuiteTypes(suiteClass),
				getBaseTypes(suiteClass), getExcludedBaseTypes(suiteClass), getClasspathProperty(suiteClass));
	}

	private static Class<?>[] getSortedTestclasses(ClassesFinder finder) {
		List<Class<?>> testclasses = finder.find();
		Collections.sort(testclasses, getClassComparator());
		return testclasses.toArray(new Class[testclasses.size()]);
	}

	private static Comparator<Class<?>> getClassComparator() {
		return new Comparator<Class<?>>() {
			public int compare(Class<?> o1, Class<?> o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};
	}

	private static String[] getClassnameFilters(Class<?> suiteClass) {
		ClassnameFilters filtersAnnotation = suiteClass.getAnnotation(ClassnameFilters.class);
		if (filtersAnnotation == null) {
			return DEFAULT_CLASSNAME_FILTERS;
		}
		return filtersAnnotation.value();
	}

	private static boolean getSearchInJars(Class<?> suiteClass) {
		IncludeJars includeJarsAnnotation = suiteClass.getAnnotation(IncludeJars.class);
		if (includeJarsAnnotation == null) {
			return DEFAULT_INCLUDE_JARS;
		}
		return includeJarsAnnotation.value();
	}

	private static SuiteType[] getSuiteTypes(Class<?> suiteClass) {
		SuiteTypes suiteTypesAnnotation = suiteClass.getAnnotation(SuiteTypes.class);
		if (suiteTypesAnnotation == null) {
			return DEFAULT_SUITE_TYPES;
		}
		return suiteTypesAnnotation.value();
	}

	private static Class<?>[] getBaseTypes(Class<?> suiteClass) {
		BaseTypeFilter baseTypeAnnotation = suiteClass.getAnnotation(BaseTypeFilter.class);
		if (baseTypeAnnotation == null) {
			return DEFAULT_BASE_TYPES;
		}
		return baseTypeAnnotation.value();
	}

	private static Class<?>[] getExcludedBaseTypes(Class<?> suiteClass) {
		ExcludeBaseTypeFilter excludeBaseTypeAnnotation = suiteClass.getAnnotation(ExcludeBaseTypeFilter.class);
		if (excludeBaseTypeAnnotation == null) {
			return DEFAULT_EXCLUDED_BASES_TYPES;
		}
		return excludeBaseTypeAnnotation.value();
	}

	private static String getClasspathProperty(Class<?> suiteClass) {
		ClasspathProperty cpPropertyAnnotation = suiteClass.getAnnotation(ClasspathProperty.class);
		if (cpPropertyAnnotation == null) {
			return DEFAULT_CLASSPATH_PROPERTY;
		}
		return cpPropertyAnnotation.value();
	}

	@Override
	public void run(RunNotifier notifier) {
		try {
			runBeforeMethods();
		} catch (Exception e) {
			notifier.fireTestFailure(new Failure(getDescription(), e));
			return;
		}
		super.run(notifier);
	}

	private void runBeforeMethods() throws Exception {
		for (Method each : suiteClass.getMethods()) {
			if (each.isAnnotationPresent(BeforeSuite.class)) {
				if (isPublicStaticVoid(each)) {
					each.invoke(null, new Object[0]);
				}
			}
		}
	}

	private boolean isPublicStaticVoid(Method method) {
		return method.getReturnType() == void.class && method.getParameterTypes().length == 0
				&& (method.getModifiers() & Modifier.STATIC) != 0;
	}
}
