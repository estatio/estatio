package org.estatio.dom.lease;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LeaseTermForTaxTest {

    public static final String VALUE = "12345.67";

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

        private LeaseTermForTax term1;
        private LeaseTermForTax term2;

        @Before
        public void setUp() throws Exception {
            term1 = new LeaseTermForTax() {
                @Override
                public BigDecimal rentValueForDate() {
                    return new BigDecimal(VALUE);
                };
            };
            term2 = new LeaseTermForTax() {
                @Override
                public BigDecimal rentValueForDate() {
                    return BigDecimal.ZERO;
                };
            };
        }

        @Test
        public void nulls() throws Exception {
            // given
            // when
            term1.doAlign();
            // then
            assertThat(term1.getTaxableValue(), is(new BigDecimal(VALUE)));
            assertThat(term1.getTaxValue(), is(new BigDecimal("0.00")));

        }

        @Test
        public void normal() throws Exception {
            // given
            term1.setTaxPercentage(new BigDecimal(1.00));
            term1.setRecoverablePercentage(new BigDecimal(50.00));
            // when
            term1.doAlign();
            // then
            assertThat(term1.getTaxableValue(), is(new BigDecimal(VALUE)));
            assertThat(term1.getPayableValue(), is(new BigDecimal("123.00")));
            assertThat(term1.getTaxValue(), is(new BigDecimal("61.50")));
        }

        @Test
        public void overrides() throws Exception {
            // given
            term1.setTaxPercentage(new BigDecimal(1.00));
            term1.setRecoverablePercentage(new BigDecimal(50.00));
            term1.setPayableValue(new BigDecimal("222.00"));
            term1.setOverridePayableValue(true);
            term1.setTaxValue(new BigDecimal("111.00"));
            term1.setOverrideTaxValue(true);
            // then
            term1.doAlign();
            // when
            assertThat(term1.getTaxableValue(), is(new BigDecimal(VALUE)));
            assertThat(term1.getPayableValue(), is(new BigDecimal("222.00")));
            assertThat(term1.getTaxValue(), is(new BigDecimal("111.00")));
        }

        
        @Test
        public void noRent() throws Exception {
            // given
            term2.setTaxPercentage(new BigDecimal(1.00));
            term2.setRecoverablePercentage(new BigDecimal(50.00));
            term2.setPayableValue(new BigDecimal("222.00"));
            term2.setOverridePayableValue(true);
            term2.setTaxValue(new BigDecimal("111.00"));
            term2.setOverrideTaxValue(true);
            // then
            term2.doAlign();
            // when
            assertThat(term2.getTaxableValue(), is(new BigDecimal("0")));
            assertThat(term2.getPayableValue(), is(new BigDecimal("222.00")));
            assertThat(term2.getTaxValue(), is(new BigDecimal("111.00")));
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