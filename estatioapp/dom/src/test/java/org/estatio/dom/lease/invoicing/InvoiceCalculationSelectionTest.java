package org.estatio.dom.lease.invoicing;

import org.junit.Test;
import org.estatio.dom.lease.LeaseItemType;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InvoiceCalculationSelectionTest {

    final InvoiceCalculationSelection invoiceCalculationSelection = InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE;

    public static class SelectedTypes extends InvoiceCalculationSelectionTest {

        @Test
        public void selectedItemTypes() {
            assertThat(invoiceCalculationSelection.selectedTypes().contains(LeaseItemType.RENT), is(true));
        }
    }

    public static class Title extends InvoiceCalculationSelectionTest {
        @Test
        public void testTitle() {
            assertThat(invoiceCalculationSelection.title(), is("Rent And Service Charge"));
        }

    }
}