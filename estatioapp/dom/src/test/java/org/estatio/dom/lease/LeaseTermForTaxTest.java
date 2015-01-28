package org.estatio.dom.lease;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

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

    public static class DoAlign extends LeaseTermForTaxTest {

        private LeaseTermForTax term;

        @Before
        public void setUp() throws Exception {
            term = new LeaseTermForTax() {
                @Override
                public BigDecimal rentValueForDate() {
                    return new BigDecimal(20000.00);
                };
            };
        }

        @Test
        public void nulls() throws Exception {
            // given
            // when
            term.doAlign();
            // then
            assertThat(term.getTaxableValue(), is(new BigDecimal(20000.00)));
            assertThat(term.getTaxValue(), is(new BigDecimal(0.00)));

        }

        @Test
        public void normal() throws Exception {
            // given
            term.setTaxPercentage(new BigDecimal(1.00));
            term.setRecoverablePercentage(new BigDecimal(50.00));
            // when
            term.doAlign();
            // then
            assertThat(term.getTaxableValue(), is(new BigDecimal("20000")));
            assertThat(term.getPayableValue(), is(new BigDecimal("200.00")));
            assertThat(term.getTaxValue(), is(new BigDecimal("100")));
        }

        @Test
        public void overrides() throws Exception {
            // given
            term.setTaxPercentage(new BigDecimal(1.00));
            term.setRecoverablePercentage(new BigDecimal(50.00));
            term.setPayableValue(new BigDecimal("222.00"));
            term.setOverridePayableValue(true);
            term.setTaxValue(new BigDecimal("111.00"));
            term.setOverrideTaxValue(true);
            // then
            term.doAlign();
            // when
            assertThat(term.getTaxableValue(), is(new BigDecimal("20000")));
            assertThat(term.getPayableValue(), is(new BigDecimal("222.00")));
            assertThat(term.getTaxValue(), is(new BigDecimal("111.00")));
        }

    }

    public static class ChangeTax extends LeaseTermForTaxTest {

        private LeaseTermForTax term;

        @Before
        public void setUp() throws Exception {
            term = new LeaseTermForTax() {
                @Override
                public BigDecimal rentValueForDate() {
                    return new BigDecimal(20000.00);
                };
            };
        }

        static BigDecimal bd(double dbl) {
            return new BigDecimal(dbl);
        }

        @Test
        public void change() throws Exception {
            // when
            term.changeTax(bd(5), bd(123.45));
            // then
            assertThat(term.getPayableValue(), is(bd(123.45)));
            assertThat(term.getTaxPercentage(), is(bd(5)));
            assertTrue(term.isOverridePayableValue());
            // when
            term.changeTax(bd(4), null);
            // then
            assertNull(term.getPayableValue());
            assertThat(term.getTaxPercentage(), is(bd(4)));
            assertFalse(term.isOverridePayableValue());
        }
    }

    public static class ChangeInvoice extends LeaseTermForTaxTest {

        private LeaseTermForTax term;

        @Before
        public void setUp() throws Exception {
            term = new LeaseTermForTax() {
                @Override
                public BigDecimal rentValueForDate() {
                    return new BigDecimal(20000.00);
                };
            };
        }

        static BigDecimal bd(double dbl) {
            return new BigDecimal(dbl);
        }

        @Test
        public void change() throws Exception {
            // when
            term.changeInvoicing(bd(50), bd(200));
            // then
            assertThat(term.getRecoverablePercentage(), is(bd(50)));
            assertThat(term.getTaxValue(), is(bd(200)));
            assertTrue(term.isOverrideTaxValue());
            // when
            term.changeInvoicing(bd(100), null);
            // then
            assertThat(term.getRecoverablePercentage(), is(bd(100)));
            assertFalse(term.isOverrideTaxValue());
            assertNull(term.getTaxValue());
        }
    }

}