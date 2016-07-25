package org.estatio.canonical.invoice;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.estatio.canonical.invoice.v1.InvoiceItemDto;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.tax.Tax;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceItemDtoFactoryTest {

    private InvoiceItem invoiceItem;

    @Before
    public void setUp() throws Exception {

        //given
        ChargeGroup chargeGroup = new ChargeGroup();
        chargeGroup.setReference("CG");
        Charge charge = new Charge();
        charge.setReference("CH");
        charge.setExternalReference("CHE");
        charge.setGroup(chargeGroup);

        Tax tax = new Tax();

        Unit unit = new Unit();
        unit.setReference("UN");

        Occupancy occupancy = new Occupancy();
        occupancy.setEndDate(new LocalDate(2013, 12, 31));
        occupancy.setUnit(unit);

        Lease lease = new Lease();
        lease.getOccupancies().add(occupancy);

        Invoice invoice = new Invoice();
        invoice.setLease(lease);

        invoiceItem = new InvoiceItemForLease();
        invoiceItem.setInvoice(invoice);
        invoiceItem.setCharge(charge);
        invoiceItem.setTax(tax);

    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_error_when_outside_scope_of_occupancies() throws Exception {
        //Given
        invoiceItem.setEffectiveStartDate(new LocalDate(2014, 1, 1));

        // When
        InvoiceItemDto invoiceItemDto = new InvoiceItemDtoFactory().newDto(invoiceItem);
    }

    @Test
    public void occupancy_is_used_when_in_scope() throws Exception {
        //Given
        invoiceItem.setEffectiveStartDate(new LocalDate(2013, 10, 1));
        // When
        InvoiceItemDto invoiceItemDto = new InvoiceItemDtoFactory().newDto(invoiceItem);
        // Then
        assertThat(invoiceItemDto.getFixedAssetReference()).isEqualTo("UN");

    }

}