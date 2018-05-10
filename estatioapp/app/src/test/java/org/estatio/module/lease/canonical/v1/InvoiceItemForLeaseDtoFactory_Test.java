package org.estatio.module.lease.canonical.v1;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.estatio.canonical.invoice.v1.InvoiceItemDto;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeGroup;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;
import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.tax.dom.Tax;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceItemForLeaseDtoFactory_Test {

    InvoiceItemForLease invoiceItem;

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

    public static class Occupancies_Test extends InvoiceItemForLeaseDtoFactory_Test {
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

    public static class EffectiveStartDate_Test extends InvoiceItemForLeaseDtoFactory_Test {

        @Test
        public void fall_back_to_date_when_effective_is_empty() throws Exception {
            //Given
            assertThat(invoiceItem.getEffectiveStartDate()).isNull();
            assertThat(invoiceItem.getStartDate()).isNull();
            final LocalDate startDate = new LocalDate(2016, 1, 1);

            // When
            invoiceItem.setStartDate(startDate);
            InvoiceItemDto invoiceItemDto = new InvoiceItemForLeaseDtoFactory().newDto(invoiceItem);
            // Then
            assertThat(invoiceItemDto.getStartDate().toString()).isEqualTo("2016-01-01T00:00:00.000Z");
            assertThat(invoiceItemDto.getEffectiveStartDate().toString()).isEqualTo("2016-01-01T00:00:00.000Z");
        }

    }

    public static class Adjustments_Test extends InvoiceItemForLeaseDtoFactory_Test {

        @Test
        public void when_not_specified() throws Exception {
            //Given
            invoiceItem.setAdjustment(null);
            // When
            InvoiceItemDto invoiceItemDto = new InvoiceItemForLeaseDtoFactory().newDto(invoiceItem);
            // Then
            assertThat(invoiceItemDto.isAdjustment()).isFalse();
        }

        @Test
        public void when_not_an_adjustment() throws Exception {
            //Given
            invoiceItem.setAdjustment(false);
            // When
            InvoiceItemDto invoiceItemDto = new InvoiceItemForLeaseDtoFactory().newDto(invoiceItem);
            // Then
            assertThat(invoiceItemDto.isAdjustment()).isFalse();
        }

        @Test
        public void when_is_an_adjustment() throws Exception {
            //Given
            invoiceItem.setAdjustment(true);
            // When
            InvoiceItemDto invoiceItemDto = new InvoiceItemForLeaseDtoFactory().newDto(invoiceItem);
            // Then
            assertThat(invoiceItemDto.isAdjustment()).isTrue();
        }

    }



}