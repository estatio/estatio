package org.estatio.dom.lease.invoicing;

import org.junit.Test;

import org.estatio.dom.lease.LeaseItemType;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceCalculationSelectionTest {

    final InvoiceCalculationSelection invoiceCalculationSelection = InvoiceCalculationSelection.ALL_RENT_AND_SERVICE_CHARGE;

    public static class SelectedTypes extends InvoiceCalculationSelectionTest {

        @Test
        public void selectedItemTypes() {
            assertThat(invoiceCalculationSelection.selectedTypes().contains(LeaseItemType.RENT)).isTrue();
        }
    }

    public static class Title extends InvoiceCalculationSelectionTest {
        @Test
        public void testTitle() {
            assertThat(invoiceCalculationSelection.title()).isEqualTo("All Rent And Service Charge");
        }

    }
}