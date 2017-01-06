package org.estatio.dom.lease.leaseinvoicing.canonical.v1;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.estatio.canonical.invoice.v1.InvoiceItemDto;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.lease.leaseinvoicing.InvoiceForLease;
import org.estatio.dom.lease.leaseinvoicing.InvoiceItemForLease;
import org.estatio.dom.tax.Tax;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceItemForLeaseDtoFactory_Test {

    private InvoiceItemForLease invoiceItem;

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

        Brand brand = new Brand();
        brand.setName("BRAND");

        Occupancy occupancy = new Occupancy();
        occupancy.setEndDate(new LocalDate(2013, 12, 31));
        occupancy.setUnit(unit);
        occupancy.setBrand(brand);

        Lease lease = new Lease();
        lease.getOccupancies().add(occupancy);

        InvoiceForLease invoice = new InvoiceForLease();
        invoice.setLease(lease);

        invoiceItem = new InvoiceItemForLease();
        invoiceItem.setInvoice(invoice);
        invoiceItem.setCharge(charge);
        invoiceItem.setTax(tax);

    }

    @Ignore // TODO: See ECP-196
    @Test(expected = IllegalArgumentException.class)
    public void throw_error_when_outside_scope_of_occupancies() throws Exception {
        //Given
        invoiceItem.setEffectiveStartDate(new LocalDate(2014, 1, 1));
        // When
        InvoiceItemDto invoiceItemDto = new InvoiceItemForLeaseDtoFactory().newDto(invoiceItem);
    }

    @Test
    public void occupancy_is_used_when_in_scope() throws Exception {
        //Given
        invoiceItem.setEffectiveStartDate(new LocalDate(2013, 10, 1));
        // When
        InvoiceItemDto invoiceItemDto = new InvoiceItemForLeaseDtoFactory().newDto(invoiceItem);
        // Then
        assertThat(invoiceItemDto.getFixedAssetReference()).isEqualTo("UN");
        assertThat(invoiceItemDto.getOccupancyBrand()).isEqualTo("BRAND");
    }

    @Test
    public void occupancy_is_ignored_when_invoice_item_has_unit() throws Exception {
        //Given
        invoiceItem.setEffectiveStartDate(new LocalDate(2013, 10, 1));
        Unit unitOnItem = new Unit();
        unitOnItem.setReference("XXX");
        invoiceItem.setFixedAsset(unitOnItem);
        // When
        InvoiceItemDto invoiceItemDto = new InvoiceItemForLeaseDtoFactory().newDto(invoiceItem);
        // Then
        assertThat(invoiceItemDto.getFixedAssetReference()).isEqualTo("XXX");
        assertThat(invoiceItemDto.getOccupancyBrand()).isEqualTo("BRAND");
    }

}