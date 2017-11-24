package org.estatio.module.lease.dom;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseTermForTax_Test {

    public static final String VALUE = "12345.67";

    public static class ChangeRegistration extends LeaseTermForTax_Test {

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

            assertThat(term.getRegistrationDate()).isEqualTo(new LocalDate(2014, 1, 1));
            assertThat(term.getRegistrationNumber()).isEqualTo("registrationNumber");
            assertThat(term.getOfficeCode()).isEqualTo("officeCode");
            assertThat(term.getOfficeName()).isEqualTo("officeName");
            assertThat(term.getDescription()).isEqualTo("description");

        }
    }

    public static class DoAlign extends LeaseTermForTax_Test {

        private LeaseTermForTax term1;
        private LeaseTermForTax term2;

        @Before
        public void setUp() throws Exception {
            term1 = new LeaseTermForTax() {
                @Override
                public BigDecimal rentValueForDate() {
                    return new BigDecimal(VALUE);
                }

                ;
            };
            term2 = new LeaseTermForTax() {
                @Override
                public BigDecimal rentValueForDate() {
                    return BigDecimal.ZERO;
                }

                ;
            };
        }

        @Test
        public void nulls() throws Exception {
            // given
            // when
            term1.doAlign();
            // then
            assertThat(term1.getTaxableValue()).isEqualTo(new BigDecimal(VALUE));
            assertThat(term1.getTaxValue()).isEqualTo(new BigDecimal("0.00"));

        }

        @Test
        public void normal() throws Exception {
            // given
            term1.setTaxPercentage(BigDecimal.valueOf(1.00));
            term1.setRecoverablePercentage(BigDecimal.valueOf(50.00));
            // when
            term1.doAlign();
            // then
            assertThat(term1.getTaxableValue()).isEqualTo(new BigDecimal(VALUE));
            assertThat(term1.getPayableValue()).isEqualTo(new BigDecimal("123.00"));
            assertThat(term1.getTaxValue()).isEqualTo(new BigDecimal("61.50"));
        }

        @Test
        public void overrides() throws Exception {
            // given
            term1.setTaxPercentage(BigDecimal.valueOf(1.00));
            term1.setRecoverablePercentage(BigDecimal.valueOf(50.00));
            term1.setPayableValue(new BigDecimal("222.00"));
            term1.setOverridePayableValue(true);
            term1.setTaxValue(new BigDecimal("111.00"));
            term1.setOverrideTaxValue(true);
            // then
            term1.doAlign();
            // when
            assertThat(term1.getTaxableValue()).isEqualTo(new BigDecimal(VALUE));
            assertThat(term1.getPayableValue()).isEqualTo(new BigDecimal("222.00"));
            assertThat(term1.getTaxValue()).isEqualTo(new BigDecimal("111.00"));
        }

        @Test
        public void noRent() throws Exception {
            // given
            term2.setTaxPercentage(BigDecimal.valueOf(1.00));
            term2.setRecoverablePercentage(BigDecimal.valueOf(50.00));
            term2.setPayableValue(new BigDecimal("222.00"));
            term2.setOverridePayableValue(true);
            term2.setTaxValue(new BigDecimal("111.00"));
            term2.setOverrideTaxValue(true);
            // then
            term2.doAlign();
            // when
            assertThat(term2.getTaxableValue()).isEqualTo(new BigDecimal("0"));
            assertThat(term2.getPayableValue()).isEqualTo(new BigDecimal("222.00"));
            assertThat(term2.getTaxValue()).isEqualTo(new BigDecimal("111.00"));
        }

    }

    public static class ChangeTax extends LeaseTermForTax_Test {

        private LeaseTermForTax term;

        @Before
        public void setUp() throws Exception {
            term = new LeaseTermForTax() {
                @Override
                public BigDecimal rentValueForDate() {
                    return BigDecimal.valueOf(20000.00);
                }

                ;
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
            assertThat(term.getPayableValue()).isEqualTo(bd(123.45));
            assertThat(term.getTaxPercentage()).isEqualTo(bd(5));
            assertThat(term.isOverridePayableValue()).isTrue();
            // when
            term.changeTax(bd(4), null);
            // then
            assertThat(term.getPayableValue()).isNull();
            assertThat(term.getTaxPercentage()).isEqualTo(bd(4));
            assertThat(term.isOverridePayableValue()).isFalse();
        }
    }

    public static class ChangeInvoice extends LeaseTermForTax_Test {

        private LeaseTermForTax term;

        @Before
        public void setUp() throws Exception {
            term = new LeaseTermForTax() {
                @Override
                public BigDecimal rentValueForDate() {
                    return BigDecimal.valueOf(20000.00);
                }

                ;
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
            assertThat(term.getRecoverablePercentage()).isEqualTo(bd(50));
            assertThat(term.getTaxValue()).isEqualTo(bd(200));
            assertThat(term.isOverrideTaxValue()).isTrue();
            // when
            term.changeInvoicing(bd(100), null);
            // then
            assertThat(term.getRecoverablePercentage()).isEqualTo(bd(100));
            assertThat(term.isOverrideTaxValue()).isFalse();
            assertThat(term.getTaxValue()).isNull();
        }
    }

    public static class AllowsOpenEndate extends LeaseTermForTax_Test {

        LeaseTermForTax term;
        LeaseItem item;

        @Before
        public void setUp() {
            term = new LeaseTermForTax();
            item = new LeaseItem();
            term.setLeaseItem(item);
        }

        @Test
        public void noOpenEndDateAllowed() {

            //when
            item.setType(LeaseItemType.TAX);

            //then
            assertThat(term.allowOpenEndDate()).isFalse();
        }

    }

}