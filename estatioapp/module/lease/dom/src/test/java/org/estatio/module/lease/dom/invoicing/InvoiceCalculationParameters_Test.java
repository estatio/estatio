package org.estatio.module.lease.dom.invoicing;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.asset.Property;
import org.estatio.module.invoice.dom.InvoiceRunType;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForTesting;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceCalculationParameters_Test {

    public static class ToString extends InvoiceCalculationParameters_Test {

        private Property property;

        private Lease lease;
        private LeaseItem leaseItem;
        private  LeaseTerm leaseTerm;


        @Before
        public void setup() {
            property = new Property();
            property.setReference("HELLO");

            lease = new Lease() {
                @Override public Property getProperty() {
                    return property;
                }
            };

            leaseItem = new LeaseItem() {
                @Override public Lease getLease() {
                    return lease;
                }

                @Override public LeaseItemType getType() {
                    return LeaseItemType.RENT;
                }
            };

            leaseTerm = new LeaseTermForTesting() {
                @Override public LeaseItem getLeaseItem() {
                    return leaseItem;
                }
            };
        }

        @Test
        public void test() {
            InvoiceCalculationParameters parameters =
                    InvoiceCalculationParameters.builder()
                            .property(property)
                            .leaseItemTypes(InvoiceCalculationSelection.ALL_RENT.selectedTypes())
                            .invoiceRunType( InvoiceRunType.NORMAL_RUN)
                            .invoiceDueDate(new LocalDate(2012, 1, 1))
                            .startDueDate(new LocalDate(2012, 1, 1))
                            .nextDueDate(new LocalDate(2012, 1, 1)).build();

            assertThat(parameters.property()).isNotNull();
            assertThat(parameters.toString()).isEqualTo("HELLO - [RENT, RENT_FIXED, RENT_DISCOUNT] - 2012-01-01 - 2012-01-01/2012-01-01");

        }

        @Test
        public void due_date_range_test() throws Exception {
            //Given
            InvoiceCalculationParameters parameters =
                    InvoiceCalculationParameters.builder()
                            .property(property)
                            .leaseItemTypes(InvoiceCalculationSelection.ALL_RENT.selectedTypes())
                            .invoiceRunType( InvoiceRunType.NORMAL_RUN)
                            .invoiceDueDate(new LocalDate(2012, 1, 1))
                            .startDueDate(new LocalDate(2012, 1, 1))
                            .nextDueDate(new LocalDate(2012, 4, 1)).build();

            //Then
            assertThat(parameters.dueDateRange().endDateExcluding()).isEqualTo(new LocalDate(2012,4,1));
        }

        @Test
        public void lease() throws Exception {
            //Given, When
            InvoiceCalculationParameters parameters =
                    InvoiceCalculationParameters.builder()
                            .lease(lease)
                            .build();

            //Then
            assertThat(parameters.property()).isEqualTo(property);
            assertThat(parameters.leases()).contains(lease);
            assertThat(parameters.leaseItem()).isNull();
            assertThat(parameters.leaseTerm()).isNull();
        }

        @Test
        public void lease_item() throws Exception {
            //Given, When
            InvoiceCalculationParameters parameters =
                    InvoiceCalculationParameters.builder()
                            .leaseItem(leaseItem)
                            .build();

            //Then
            assertThat(parameters.property()).isEqualTo(property);
            assertThat(parameters.leases()).contains(lease);
            assertThat(parameters.leaseItem()).isEqualTo(leaseItem);
            assertThat(parameters.leaseTerm()).isNull();
            assertThat(parameters.leaseItemTypes()).contains(LeaseItemType.RENT);
        }

        @Test
        public void lease_term() throws Exception {
            //Given, When
            InvoiceCalculationParameters parameters =
                    InvoiceCalculationParameters.builder()
                            .leaseTerm(leaseTerm)
                            .build();

            //Then
            assertThat(parameters.leaseTerm()).isEqualTo(leaseTerm);
            assertThat(parameters.leaseItem()).isEqualTo(leaseItem);
            assertThat(parameters.leases()).contains(lease);
            assertThat(parameters.property()).isEqualTo(property);
            assertThat(parameters.leaseItemTypes()).contains(LeaseItemType.RENT);
        }

    }
}