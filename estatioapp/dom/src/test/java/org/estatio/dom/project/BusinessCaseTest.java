package org.estatio.dom.project;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.junit.Test;

public class BusinessCaseTest {
	
    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final BusinessCase pojo = new BusinessCase();
            newPojoTester()
                    .withFixture(pojos(Project.class, ProjectForTesting.class))
                    .withFixture(pojos(BusinessCase.class, BusinessCaseForTesting.class))
                    .exercise(pojo);
        }

    }

}
