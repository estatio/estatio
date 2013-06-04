package org.estatio.dom.agreement;

import org.junit.Test;


import org.estatio.dom.AbstractBeanPropertiesTest;

public class AgreementTest_beanProperties extends AbstractBeanPropertiesTest {

    @Test
    public void test() {
        final Agreement agreement = new AgreementForTesting();
        newPojoTester()
            .withFixture(pojos(Agreement.class, AgreementForTesting.class))
            .withFixture(pojos(AgreementType.class))
            .exercise(agreement);
    }

}
