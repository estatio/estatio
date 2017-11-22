/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.lease.app;

import org.junit.Before;
import org.junit.Test;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.Lease;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceMenuTest {

    InvoiceMenu invoiceMenu;

    @Before
    public void setUp() throws Exception {
        invoiceMenu = new InvoiceMenu();
    }

    public static class NewInvoiceForLease extends InvoiceMenuTest {

        @Test
        public void when_lease_has_an_occupancy() throws Exception {

            // given
            final Lease lease = new Lease() {
                @Override public Property getProperty() {
                    return new Property();
                }
            };

            // when
            final String reason = invoiceMenu.validateNewInvoiceForLease(lease, null, PaymentMethod.DIRECT_DEBIT, null);

            // then
            assertThat(reason).isNull();
        }

        @Test
        public void when_lease_does_not_have_an_occupancy() throws Exception {

            // given
            final Lease lease = new Lease() {
                @Override public Property getProperty() {
                    return null;
                }
            };

            // when
            final String reason = invoiceMenu.validateNewInvoiceForLease(lease, null, PaymentMethod.DIRECT_DEBIT, null);

            // then
            assertThat(reason).contains("Can only create invoices for leases that have an occupancy");
        }

        @Test
        public void when_lease_does_have_defaultPaymentMethod_and_no_paymentmethod_given() throws Exception {

            // given
            final Lease lease = new Lease() {
                @Override public Property getProperty() {
                    return new Property();
                }
                @Override public PaymentMethod defaultPaymentMethod() {
                    return PaymentMethod.DIRECT_DEBIT;
                }
            };

            // when
            final String reason = invoiceMenu.validateNewInvoiceForLease(lease, null, null, null);

            // then
            assertThat(reason).isNull();
        }

        @Test
        public void when_lease_does_not_have_defaultPaymentMethod_and_no_paymentmethod_given() throws Exception {

            // given
            final Lease lease = new Lease() {
                @Override public Property getProperty() {
                    return new Property();
                }
                @Override public PaymentMethod defaultPaymentMethod() {
                    return null;
                }
            };

            // when
            final String reason = invoiceMenu.validateNewInvoiceForLease(lease, null, null, null);

            // then
            assertThat(reason).contains("A payment method has to be provided");
        }
    }

}