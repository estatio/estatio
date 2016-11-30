package org.estatio.dom.lease.invoicing;

import org.junit.Test;

import org.estatio.dom.lease.LeaseItemType;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceCalculationSelection_Test {

    final InvoiceCalculationSelection invoiceCalculationSelection = InvoiceCalculationSelection.ALL_RENT_AND_SERVICE_CHARGE;

    public static class SelectedTypes extends InvoiceCalculationSelection_Test {

        @Test
        public void selectedItemTypes() {
            assertThat(invoiceCalculationSelection.selectedTypes().contains(LeaseItemType.RENT)).isTrue();
        }
    }

    public static class Title extends InvoiceCalculationSelection_Test {
        @Test
        public void testTitle() {
            assertThat(invoiceCalculationSelection.title()).isEqualTo("All Rent And Service Charge");
        }

    }
}