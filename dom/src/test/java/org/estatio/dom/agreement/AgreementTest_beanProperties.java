package org.estatio.dom.agreement;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.Lockable;
import org.estatio.dom.PojoTester.FilterSet;
import org.estatio.dom.PojoTester.FixtureDatumFactory;

public class AgreementTest_beanProperties extends AbstractBeanPropertiesTest {

    @Test
    public void test() {
        final Agreement<?> agreement = new AgreementForTesting();
        newPojoTester()
            .withFixture(pojos(Agreement.class, AgreementForTesting.class))
            .withFixture(pojos(AgreementType.class))
            .exercise(agreement, FilterSet.excluding("status"));
    }

}
