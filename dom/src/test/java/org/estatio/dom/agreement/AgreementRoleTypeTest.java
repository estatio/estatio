package org.estatio.dom.agreement;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.Is;
import org.junit.Test;

public class AgreementRoleTypeTest {

    @Test
    public void test() {
        assertThat(AgreementRoleType.applicableTo(AgreementType.LEASE).size(), Is.is(3));
        assertThat(AgreementRoleType.applicableTo(AgreementType.MANDATE).size(), Is.is(3));
    }
}
