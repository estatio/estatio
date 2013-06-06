package org.estatio.dom.agreement;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class AgreementTypeTest_beanProperties extends AbstractBeanPropertiesTest {

    @Test
    public void test() {
        final AgreementType agreementType = new AgreementType();
        newPojoTester()
            .exercise(agreementType);
    }

}
