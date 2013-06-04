package org.estatio.dom.index;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class IndexBaseTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Index.class))
	        .withFixture(pojos(IndexBase.class))
	        .exercise(new IndexBase());
	}


}
