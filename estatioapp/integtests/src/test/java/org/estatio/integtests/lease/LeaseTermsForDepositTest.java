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
package org.estatio.integtests.lease;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.assertj.core.api.Assertions;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.*;
import org.estatio.dom.lease.invoicing.InvoiceCalculationSelection;
import org.estatio.dom.lease.invoicing.InvoiceRunType;
import org.estatio.dom.lease.invoicing.InvoiceService;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.math.BigDecimal;

public class LeaseTermsForDepositTest extends EstatioIntegrationTest {

    @Inject
    Leases leases;

    @Inject
    InvoiceService invoiceService;

    @Inject
    Invoices invoices;

    public static class LeaseTermForDepositForOxf extends LeaseTermsForDepositTest {

        LeaseTermForIndexable indexTermFirst;
        LeaseTermForDeposit depositTerm;
        Lease topmodelLease;
        LocalDate startDate;

        @Before
        public void setUp() throws Exception {

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
                }
            });
        }

        @Test
        public void invoiceScenarioTest() throws Exception {

            // given
            startDate = new LocalDate(2010,10,1);
            topmodelLease = leases.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);

            // when
            invoiceService.calculate(
                    topmodelLease,
                    InvoiceRunType.NORMAL_RUN,
                    InvoiceCalculationSelection.DEPOSIT,
                    startDate, startDate, startDate.plusDays(1));

            // then
            Assertions.assertThat(invoices.findByLease(topmodelLease).size()).isEqualTo(1);
            Assertions.assertThat(invoices.findByLease(topmodelLease).get(0).getNetAmount()).isEqualTo(new BigDecimal("5000.00"));


            // and when (after couple of indexations of rent items)
            invoiceService.calculate(
                    topmodelLease,
                    InvoiceRunType.NORMAL_RUN,
                    InvoiceCalculationSelection.DEPOSIT,
                    startDate.plusYears(5), startDate.plusYears(5), startDate.plusYears(5).plusDays(1));

            // then
            Assertions.assertThat(invoices.findByLease(topmodelLease).size()).isEqualTo(2);
            Assertions.assertThat(invoices.findByLease(topmodelLease).get(0).getNetAmount()).isEqualTo(new BigDecimal("5000.00"));
            Assertions.assertThat(invoices.findByLease(topmodelLease).get(1).getNetAmount()).isEqualTo(new BigDecimal("652.51"));

            // and after approval of first invoice only the delta is invoiced
            invoices.findByLease(topmodelLease).get(0).approve();
            invoiceService.calculate(
                    topmodelLease,
                    InvoiceRunType.NORMAL_RUN,
                    InvoiceCalculationSelection.DEPOSIT,
                    startDate.plusYears(5), startDate.plusYears(5), startDate.plusYears(5).plusDays(1));


            // then
            Assertions.assertThat(invoices.findByLease(topmodelLease).size()).isEqualTo(2);
            Assertions.assertThat(invoices.findByLease(topmodelLease).get(1).getNetAmount()).isEqualTo(new BigDecimal("652.51"));

            // and after terminating the invoiced deposit is credited
            depositTerm = (LeaseTermForDeposit) topmodelLease.findFirstItemOfType(LeaseItemType.DEPOSIT).getTerms().first();
            depositTerm.terminate(startDate.plusYears(5).minusDays(1));
            invoices.findByLease(topmodelLease).get(1).approve();
            invoiceService.calculate(
                    topmodelLease,
                    InvoiceRunType.RETRO_RUN,
                    InvoiceCalculationSelection.DEPOSIT,
                    startDate.plusYears(5), startDate.plusYears(5), startDate.plusYears(5).plusDays(1));

            //then
            Assertions.assertThat(invoices.findByLease(topmodelLease).size()).isEqualTo(3);
            Assertions.assertThat(invoices.findByLease(topmodelLease).get(2).getNetAmount()).isEqualTo(new BigDecimal("-5652.51"));

        }

        @Test
        public void changeParametersTest() throws Exception {

            //given
            topmodelLease = leases.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            indexTermFirst = (LeaseTermForIndexable) topmodelLease.findFirstItemOfType(LeaseItemType.RENT).getTerms().first();
            depositTerm = (LeaseTermForDeposit) topmodelLease.findFirstItemOfType(LeaseItemType.DEPOSIT).getTerms().first();
            depositTerm.approve();
            Assertions.assertThat(depositTerm.getStatus()).isEqualTo(LeaseTermStatus.APPROVED);

            //when
            depositTerm.changeParameters(DepositType.QUARTER, new BigDecimal("12345.67"));

            //then
            Assertions.assertThat(depositTerm.getDepositType()).isEqualTo(DepositType.QUARTER);
            Assertions.assertThat(depositTerm.getExcludedAmount()).isEqualTo(new BigDecimal("12345.67"));
            Assertions.assertThat(depositTerm.getStatus()).isEqualTo(LeaseTermStatus.NEW);
        }

        @Test
        public void changeManualDepositValueTest() throws Exception {

            //given
            topmodelLease = leases.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            indexTermFirst = (LeaseTermForIndexable) topmodelLease.findFirstItemOfType(LeaseItemType.RENT).getTerms().first();
            depositTerm = (LeaseTermForDeposit) topmodelLease.findFirstItemOfType(LeaseItemType.DEPOSIT).getTerms().first();
            depositTerm.approve();
            Assertions.assertThat(depositTerm.getStatus()).isEqualTo(LeaseTermStatus.APPROVED);

            //when
            depositTerm.changeManualDepositValue(new BigDecimal("76543.21"));

            //then
            Assertions.assertThat(depositTerm.getManualDepositValue()).isEqualTo(new BigDecimal("76543.21"));
            Assertions.assertThat(depositTerm.getStatus()).isEqualTo(LeaseTermStatus.NEW);
        }

    }


}