package org.estatio.dom.lease.invoicing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import org.estatio.dom.lease.LeaseItemType;

public class InvoiceCalculationSelectionTest {

    @Test
    public void selectedItemTypes() {
        assertThat(InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE.selectedTypes().contains(LeaseItemType.RENT), is(true));
    }

    @Test
    public void testTitle() {
        assertThat(InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE.title(), is("Rent And Service Charge"));
    }
}
