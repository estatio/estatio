package org.estatio.dom.lease;

import org.joda.time.LocalDate;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class LeaseTermForTaxTest {

    public static class ChangeRegistration extends LeaseTermForTaxTest {

        @Test
        public void test() {
            // given
            LeaseTermForTax term = new LeaseTermForTax();
            term.setRegistrationDate(new LocalDate(2014, 1, 1));
            term.setRegistrationNumber("registrationNumber");
            term.setOfficeCode("officeCode");
            term.setOfficeName("officeName");
            term.setDescription("description");

            // when
            term.changeRegistration(new LocalDate(2014, 1, 1), "registrationNumber", "officeCode", "officeName", "description");

            assertThat(term.getRegistrationDate(), is(new LocalDate(2014, 1, 1)));
            assertThat(term.getRegistrationNumber(), is("registrationNumber"));
            assertThat(term.getOfficeCode(), is("officeCode"));
            assertThat(term.getOfficeName(), is("officeName"));
            assertThat(term.getDescription(), is("description"));

        }
    }
}