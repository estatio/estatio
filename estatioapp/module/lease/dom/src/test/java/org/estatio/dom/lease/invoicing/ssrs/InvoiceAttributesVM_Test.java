package org.estatio.dom.lease.invoicing.ssrs;

import java.util.TreeSet;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.charge.Charge;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceAttributesVM_Test {

    InvoiceForLease invoiceForLease;

    InvoiceAttributesVM invoiceAttributesVM;

    @Before
    public void setUp() throws Exception {
        invoiceForLease = new InvoiceForLease();
        invoiceForLease.setItems(new TreeSet<>());

        invoiceAttributesVM = new InvoiceAttributesVM(invoiceForLease);
    }


    //    CASE DATEDIFF(MONTH, ii.startDate, DATEADD(day,1,ii.endDate))
    //    WHEN 12 THEN 'YEAR'
    //    WHEN 3 THEN 'QUARTER'
    //    WHEN 1 THEN 'MONTH'
    //    ELSE 'OTHER' END AS frequency,
    public static class GetFrequency_Test extends InvoiceAttributesVM_Test {

        @Test
        public void no_items() {

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("OTHER");
        }

        @Test
        public void start_date_is_null() {

            // given
            addItemFor(this.invoiceForLease, null, ld(2015, 12, 31));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("OTHER");
        }

        @Test
        public void end_date_is_null() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 12, 31), null);

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("OTHER");
        }

        @Test
        public void year_exact_not_a_leap_year() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 1, 1), ld(2015, 12, 31));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("YEAR");
        }

        @Test
        public void year_exact_not_at_start_of_month() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 6, 15), ld(2016, 6, 14));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("YEAR");
        }

        @Test
        public void year_exact_with_leap_year_ending_on_29th_feb() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 3, 1), ld(2016, 2, 29));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("YEAR");

        }

        @Test
        public void year_exact_with_leap_year_ending_prior_to_29th_feb() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 2, 1), ld(2016, 1, 31));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("YEAR");

        }

        @Test
        public void year_exact_with_leap_year_ending_after_29th_feb() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 4, 1), ld(2016, 3, 31));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("YEAR");

        }


        @Test
        public void just_under_a_year() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 1, 1), ld(2015, 12, 30));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("OTHER");
        }

        @Test
        public void just_over_a_year() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 1, 1), ld(2016, 1, 1));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("YEAR");

        }

        @Test
        public void almost_13_months() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 1, 1), ld(2016, 1, 30));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("YEAR");

        }

        @Test
        public void over_a_year_is_13_months() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 1, 1), ld(2016, 1, 31));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("OTHER");
        }

        @Test
        public void quarter_exact() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 1, 1), ld(2015, 3, 31));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("QUARTER");
        }

        @Test
        public void quarter_not_at_start_of_month() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 4, 12), ld(2015, 7, 11));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("QUARTER");
        }

        @Test
        public void quarter_not_a_leap_year_ending_on_28th_feb() {

            // given
            addItemFor(this.invoiceForLease, ld(2014, 12, 1), ld(2015, 2, 28));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("QUARTER");
        }

        @Test
        public void quarter_for_leap_year_ending_on_29th_feb() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 12, 1), ld(2016, 2, 29));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("QUARTER");
        }

        @Test
        public void quarter_for_leap_year_ending_before_29th_feb() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 11, 1), ld(2016, 1, 31));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("QUARTER");
        }

        @Test
        public void quarter_for_leap_year_ending_after_29th_feb() {

            // given
            addItemFor(this.invoiceForLease, ld(2016, 1, 1), ld(2016, 3, 31));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("QUARTER");
        }

        @Test
        public void almost_a_quarter() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 10, 1), ld(2015, 12, 30));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("OTHER");
        }

        @Test
        public void just_over_a_quarter() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 10, 1), ld(2016, 1, 1));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("QUARTER");
        }

        @Test
        public void almost_4_months() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 10, 1), ld(2016, 1, 30));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("QUARTER");
        }

        @Test
        public void exactly_a_month() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 10, 1), ld(2015, 10, 31));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("MONTH");
        }

        @Test
        public void almost_a_month() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 10, 1), ld(2015, 10, 30));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("OTHER");
        }


        @Test
        public void just_over_a_month() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 10, 1), ld(2015, 11, 1));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("MONTH");
        }

        @Test
        public void almost_two_months() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 10, 1), ld(2015, 11, 29));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("MONTH");
        }

        @Test
        public void two_months() {

            // given
            addItemFor(this.invoiceForLease, ld(2015, 10, 1), ld(2015, 11, 30));

            // when
            String frequency = invoiceAttributesVM.getFrequency();

            // then
            assertThat(frequency).isEqualTo("OTHER");
        }

        static void addItemFor(
                final InvoiceForLease invoiceForLease,
                final LocalDate start,
                final LocalDate end) {
            InvoiceItemForLease ii = new InvoiceItemForLease();
            ii.setStartDate(start);
            ii.setEndDate(end);
            invoiceForLease.getItems().add(ii);
        }

        static LocalDate ld(final int year, final int monthOfYear, final int dayOfMonth) {
            return new LocalDate(year, monthOfYear, dayOfMonth);
        }

    }

    public static class GetChargeDescriptions_Test extends InvoiceAttributesVM_Test {

        @Test
        public void happy_case() throws Exception {
            // given
            addItemWithCharge(invoiceForLease, "Desc 1");
            addItemWithCharge(invoiceForLease, "Desc 2");
            addItemWithCharge(invoiceForLease, "Desc 3");

            // when
            String chargeDescriptions = invoiceAttributesVM.getChargeDescriptions();

            // then
            assertThat(chargeDescriptions).isEqualTo("Desc 1, Desc 2 e Desc 3");
        }

        @Test
        public void duplicate_charges_ignored() throws Exception {
            // given
            addItemWithCharge(invoiceForLease, "Desc 1");
            addItemWithCharge(invoiceForLease, "Desc 2");
            addItemWithCharge(invoiceForLease, "Desc 1");
            addItemWithCharge(invoiceForLease, "Desc 3");

            // when
            String chargeDescriptions = invoiceAttributesVM.getChargeDescriptions();

            // then
            assertThat(chargeDescriptions).isEqualTo("Desc 1, Desc 2 e Desc 3");
        }

        @Test
        public void when_zero() throws Exception {

            // when
            String chargeDescriptions = invoiceAttributesVM.getChargeDescriptions();

            // then
            assertThat(chargeDescriptions).isEqualTo("");
        }

        @Test
        public void when_one() throws Exception {
            // given
            addItemWithCharge(invoiceForLease, "Desc 1");

            // when
            String chargeDescriptions = invoiceAttributesVM.getChargeDescriptions();

            // then
            assertThat(chargeDescriptions).isEqualTo("Desc 1");
        }

        @Test
        public void when_two() throws Exception {
            // given
            addItemWithCharge(invoiceForLease, "Desc 1");
            addItemWithCharge(invoiceForLease, "Desc 2");

            // when
            String chargeDescriptions = invoiceAttributesVM.getChargeDescriptions();

            // then
            assertThat(chargeDescriptions).isEqualTo("Desc 1 e Desc 2");
        }

        static void addItemWithCharge(final InvoiceForLease invoiceForLease, final String description) {
            final InvoiceItemForLease e = new InvoiceItemForLease();
            final Charge charge = new Charge();
            charge.setReference(description); // because Charge is Comparable on this
            charge.setDescription(description);
            e.setCharge(charge);
            invoiceForLease.getItems().add(e);
        }

    }


}