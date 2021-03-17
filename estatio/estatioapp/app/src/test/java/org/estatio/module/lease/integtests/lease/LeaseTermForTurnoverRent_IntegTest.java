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
package org.estatio.module.lease.integtests.lease;

import java.math.BigDecimal;
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemRepository;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermForTurnoverRent;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

public class LeaseTermForTurnoverRent_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    LeaseItemRepository leaseItemRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
            }
        });
    }

    private Lease lease;
    private LeaseItem sourceItem, torItem1, torItem2;
    private BigDecimal baseValue;
    private LeaseTermForTurnoverRent torTerm1OfItem1, torTerm1OfItem2;
    private LocalDate rentItemStartDate;

    @Before
    public void setup() {
        lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        rentItemStartDate = new LocalDate(2020, 1, 1);
        sourceItem = lease.newItem(LeaseItemType.RENT, LeaseAgreementRoleTypeEnum.LANDLORD,
                Charge_enum.GbRent.findUsing(serviceRegistry), InvoicingFrequency.QUARTERLY_IN_ADVANCE,
                PaymentMethod.DIRECT_DEBIT,
                rentItemStartDate);
        final LeaseTermForIndexable sourceTerm1 = (LeaseTermForIndexable) sourceItem.newTerm(rentItemStartDate, null);
        baseValue = new BigDecimal("170000.00");
        sourceTerm1.setBaseValue(baseValue);

    }

    @Test
    public void turnover_rent_term_calculates_base_value_with_prorata_correctly_when_rent_quarterly() throws Exception {

        // given
        Assertions.assertThat(sourceItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.QUARTERLY_IN_ADVANCE);

        sourceItem.verifyUntil(new LocalDate(2021,1,2));
        final SortedSet<LeaseTerm> sourceItemTerms = sourceItem.getTerms();
        Assertions.assertThat(sourceItemTerms).hasSize(2);
        Lists.newArrayList(sourceItemTerms).forEach(t->{
            Assertions.assertThat(t.getEffectiveValue()).isEqualTo(baseValue);
        });

        torItem1 = lease.newItem(LeaseItemType.TURNOVER_RENT, LeaseAgreementRoleTypeEnum.LANDLORD,
                Charge_enum.GbTurnoverRent.findUsing(serviceRegistry), InvoicingFrequency.YEARLY_IN_ARREARS,
                PaymentMethod.DIRECT_DEBIT,
                new LocalDate(2020, 11, 27));
        torItem1.setEndDate(new LocalDate(2021, 11,26));
        torItem1.newSourceItem(sourceItem);
        torTerm1OfItem1 = (LeaseTermForTurnoverRent) torItem1.newTerm(new LocalDate(2020, 11, 27), new LocalDate(2021, 11, 26));

        torItem2 = lease.newItem(LeaseItemType.TURNOVER_RENT, LeaseAgreementRoleTypeEnum.LANDLORD,
                Charge_enum.GbTurnoverRent.findUsing(serviceRegistry), InvoicingFrequency.YEARLY_IN_ARREARS,
                PaymentMethod.DIRECT_DEBIT,
                new LocalDate(2021, 11, 27));
        torItem2.newSourceItem(sourceItem);
        torTerm1OfItem2 = (LeaseTermForTurnoverRent) torItem2.newTerm(new LocalDate(2021, 1, 1), new LocalDate(2021, 12, 31));

        // when
        Assertions.assertThat(torTerm1OfItem1.getContractualRent()).isEqualTo(baseValue);
        Assertions.assertThat(torTerm1OfItem2.getContractualRent()).isEqualTo(baseValue);


    }

    @Test
    public void turnover_rent_term_calculates_base_value_with_prorata_with_rounding_error_when_rent_monthly() throws Exception {

        // given
        sourceItem.setInvoicingFrequency(InvoicingFrequency.MONTHLY_IN_ADVANCE);
        sourceItem.verifyUntil(new LocalDate(2021,1,2));
        final SortedSet<LeaseTerm> sourceItemTerms = sourceItem.getTerms();
        Assertions.assertThat(sourceItemTerms).hasSize(2);
        Lists.newArrayList(sourceItemTerms).forEach(t->{
            Assertions.assertThat(t.getEffectiveValue()).isEqualTo(baseValue);
        });

        torItem1 = lease.newItem(LeaseItemType.TURNOVER_RENT, LeaseAgreementRoleTypeEnum.LANDLORD,
                Charge_enum.GbTurnoverRent.findUsing(serviceRegistry), InvoicingFrequency.YEARLY_IN_ARREARS,
                PaymentMethod.DIRECT_DEBIT,
                new LocalDate(2020, 11, 27));
        torItem1.setEndDate(new LocalDate(2021, 11,26));
        torItem1.newSourceItem(sourceItem);
        torTerm1OfItem1 = (LeaseTermForTurnoverRent) torItem1.newTerm(new LocalDate(2020, 11, 27), new LocalDate(2021, 11, 26));

        torItem2 = lease.newItem(LeaseItemType.TURNOVER_RENT, LeaseAgreementRoleTypeEnum.LANDLORD,
                Charge_enum.GbTurnoverRent.findUsing(serviceRegistry), InvoicingFrequency.YEARLY_IN_ARREARS,
                PaymentMethod.DIRECT_DEBIT,
                new LocalDate(2021, 11, 27));
        torItem2.newSourceItem(sourceItem);
        torTerm1OfItem2 = (LeaseTermForTurnoverRent) torItem2.newTerm(new LocalDate(2021, 1, 1), new LocalDate(2021, 12, 31));

        // when
        BigDecimal roundingErrorInThisParticularCase = new BigDecimal("0.04");
        Assertions.assertThat(torTerm1OfItem1.getContractualRent()).isEqualTo(baseValue.add(roundingErrorInThisParticularCase));
        Assertions.assertThat(torTerm1OfItem2.getContractualRent()).isEqualTo(baseValue.add(roundingErrorInThisParticularCase));

    }



}
