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
package org.estatio.module.lease.integtests.migrations;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.factory.FactoryService;

import org.incode.module.base.integtests.VT;

import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceRunType;
import org.estatio.module.lease.app.InvoiceServiceMenu;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTermForTurnoverRent;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationSelection;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationService;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDeposit_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForEntryFee_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForMarketing_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTax_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.lease.migrations.CreateRetroInvoices;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CreateRetroInvoices_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb_TA.builder());
                executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForDiscount_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForEntryFee_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForTax_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForDeposit_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForMarketing_enum.OxfTopModel001Gb.builder());

            }
        });
    }

    @Inject
    InvoiceRepository invoiceRepository;

    @Inject
    InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    FactoryService factoryService;

    @Inject
    InvoiceCalculationService invoiceCalculationService;

    @Inject
    InvoiceServiceMenu invoiceService;

    CreateRetroInvoices createRetroInvoices;

    Lease lease;

    @Before
    public void setup() {
        createRetroInvoices = new CreateRetroInvoices() {
            {
                leaseRepository = CreateRetroInvoices_IntegTest.this.leaseRepository;
                invoiceForLeaseRepository = CreateRetroInvoices_IntegTest.this.invoiceForLeaseRepository;
                invoiceRepository = CreateRetroInvoices_IntegTest.this.invoiceRepository;
                propertyRepository = CreateRetroInvoices_IntegTest.this.propertyRepository;
                invoiceCalculationService = CreateRetroInvoices_IntegTest.this.invoiceCalculationService;
                factoryService = CreateRetroInvoices_IntegTest.this.factoryService;
            }
        };

        lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
    }

    public static class FindDueDatesForLease extends CreateRetroInvoices_IntegTest {

        @Test
        public void whenPresent() {
            // when
            SortedSet<LocalDate> dueDates = lease.dueDatesInRange(VT.ld(2012, 1, 1), VT.ld(2014, 1, 1));
            // then
            assertThat(dueDates.size(), is(10));
        }

    }

    public static class Terminate_and_Calculate extends CreateRetroInvoices_IntegTest {

        @Test
        public void step1_retroRun() {
            // given
            SortedSet<LocalDate> dueDates = lease.dueDatesInRange(VT.ld(2012, 1, 1), VT.ld(2014, 1, 1));
            assertThat(dueDates.size(), is(10));

            // when
            createRetroInvoices.createLease(lease, VT.ld(2012, 1, 1), VT.ld(2014, 1, 1), FixtureScript.ExecutionContext.NOOP);

            // then
            final List<InvoiceForLease> invoicesAfterCreate = invoiceForLeaseRepository.findByLease(lease);
            assertThat(invoicesAfterCreate.size(), is(8));

            // and given
            lease.terminate(VT.ld(2013, 10, 1));

            // when
            invoiceService.calculateLegacy(lease, InvoiceRunType.NORMAL_RUN, InvoiceCalculationSelection.ALL_RENT_AND_SERVICE_CHARGE, VT.ld(2014, 2, 1), VT.ld(2012, 1, 1), VT.ld(2014, 1, 1));

            // then
            List<InvoiceForLease> invoicessAfterCreateLegacy = invoiceForLeaseRepository.findByLease(lease);
            assertThat(invoicessAfterCreateLegacy.size(), is(9));
            Invoice invoice = invoicessAfterCreateLegacy.get(0);
            assertThat(invoice.getDueDate(), is(VT.ld(2014, 2, 1)));
            assertThat(invoice.getTotalGrossAmount(), is(VT.bd("-8170.01")));

            // and also
            LeaseItem leaseItem = lease.findFirstItemOfType(LeaseItemType.TURNOVER_RENT);
            LeaseTermForTurnoverRent term = (LeaseTermForTurnoverRent) leaseItem.findTerm(VT.ld(2012, 1, 1));
            assertThat(term.getContractualRent(), is(VT.bd("21058.27")));
        }
    }

}