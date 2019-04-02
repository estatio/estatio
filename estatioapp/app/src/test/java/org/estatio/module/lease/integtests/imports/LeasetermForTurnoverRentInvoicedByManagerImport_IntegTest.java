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
package org.estatio.module.lease.integtests.imports;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.xactn.TransactionService3;

import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTermForTurnoverRent;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.imports.LeaseTermForTurnoverRentInvoicedByManagerImport;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class LeasetermForTurnoverRentInvoicedByManagerImport_IntegTest extends LeaseModuleIntegTestAbstract {

    List<FixtureResult> fixtureResults;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfTopModel001Gb.builder());
                fixtureResults = executionContext.getResults();
            }
        });
    }

    @Test
    public void import_test() throws Exception {

        // given
        Lease leaseForTopmodel = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2);
        final LocalDate term1StartDate = new LocalDate(2011, 1, 1);
        final BigDecimal term1NetAmount = new BigDecimal("123.45");
        LeaseTermForTurnoverRentInvoicedByManagerImport leaseTermImport1 = new LeaseTermForTurnoverRentInvoicedByManagerImport(
                leaseForTopmodel.getReference(),
                Charge_enum.GbTurnoverRent.findUsing(serviceRegistry2).getReference(),
                term1StartDate,
                term1NetAmount
        );
        final LocalDate term2StartDate = new LocalDate(2013, 1, 1);
        final BigDecimal term2NetAmount = new BigDecimal("234.56");
        LeaseTermForTurnoverRentInvoicedByManagerImport leaseTermImport2 = new LeaseTermForTurnoverRentInvoicedByManagerImport(
                leaseForTopmodel.getReference(),
                Charge_enum.GbTurnoverRent.findUsing(serviceRegistry2).getReference(),
                term2StartDate,
                term2NetAmount
        );

        serviceRegistry2.injectServicesInto(leaseTermImport1);
        serviceRegistry2.injectServicesInto(leaseTermImport2);

        Assertions.assertThat(leaseForTopmodel.getItems()).hasSize(2);

        // when
        leaseTermImport1.importData();
        leaseTermImport2.importData();
        transactionService2.nextTransaction();

        // then
        assertThat(leaseForTopmodel.getItems()).hasSize(3);
        LeaseItem createdItem = leaseForTopmodel.findItem(LeaseItemType.TURNOVER_RENT, leaseForTopmodel.getStartDate(), LeaseAgreementRoleTypeEnum.MANAGER);
        assertThat(createdItem.getTerms()).hasSize(3);
        assertThat(createdItem.getCharge()).isEqualTo(Charge_enum.GbTurnoverRent.findUsing(serviceRegistry2));
        assertThat(createdItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.YEARLY_IN_ARREARS);
        assertThat(createdItem.getInvoicedBy()).isEqualTo(LeaseAgreementRoleTypeEnum.MANAGER);
        assertThat(createdItem.getPaymentMethod()).isEqualTo(PaymentMethod.MANUAL_PROCESS);
        final LeaseTermForTurnoverRent firstTerm = (LeaseTermForTurnoverRent) createdItem.getTerms().first();
        assertThat(firstTerm.getStartDate()).isEqualTo(term1StartDate);
        assertThat(firstTerm.getEndDate()).isEqualTo(new LocalDate(2011, 12, 31));
        assertThat(firstTerm.getManualTurnoverRent()).isEqualTo(term1NetAmount);
        final LeaseTermForTurnoverRent lastTerm = (LeaseTermForTurnoverRent) createdItem.getTerms().last();
        assertThat(lastTerm.getStartDate()).isEqualTo(term2StartDate);
        assertThat(lastTerm.getEndDate()).isEqualTo(new LocalDate(2013, 12, 31));
        assertThat(lastTerm.getManualTurnoverRent()).isEqualTo(term2NetAmount);

    }

    @Inject
    ServiceRegistry2 serviceRegistry2;


    @Inject
    TransactionService3 transactionService2;


}