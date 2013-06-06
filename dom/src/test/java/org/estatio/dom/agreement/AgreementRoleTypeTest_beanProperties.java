package org.estatio.dom.agreement;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class AgreementRoleTypeTest_beanProperties extends AbstractBeanPropertiesTest {

    @Test
    public void test() {
        final AgreementRoleType agreementRoleType = new AgreementRoleType();
        newPojoTester()
            .withFixture(pojos(AgreementType.class))
            .exercise(agreementRoleType);
    }

}
