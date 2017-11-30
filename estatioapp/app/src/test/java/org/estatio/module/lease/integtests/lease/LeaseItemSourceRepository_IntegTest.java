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

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.charges.refdata.ChargeRefData;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemSourceRepository;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.lease.personas.LeaseForOxfPoison003Gb;
import org.estatio.module.lease.fixtures.leaseitems.deposits.personas.LeaseItemAndLeaseTermForDepositForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.discount.personas.LeaseItemAndLeaseTermForDiscountForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.entryfee.personas.LeaseItemAndLeaseTermForEntryFeeForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.marketing.personas.LeaseItemAndLeaseTermForMarketingForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.percentage.personas.LeaseItemAndLeaseTermForPercentageForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.rent.personas.LeaseItemAndLeaseTermForRentForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.servicecharge.personas.LeaseItemAndLeaseTermForServiceChargeForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.svcchgbudgeted.personas.LeaseItemForServiceChargeBudgetedForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.tax.personas.LeaseItemAndLeaseTermForTaxForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.turnoverrent.personas.LeaseItemAndLeaseTermForTurnoverRentForOxfTopModel001Gb;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

public class LeaseItemSourceRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                executionContext.executeChild(this, new LeaseItemAndLeaseTermForRentForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForServiceChargeForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemForServiceChargeBudgetedForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForTurnoverRentForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForPercentageForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForDiscountForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForEntryFeeForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForTaxForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForDepositForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForMarketingForOxfTopModel001Gb());

                executionContext.executeChild(this, new LeaseForOxfPoison003Gb());
            }
        });
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    LeaseItemSourceRepository leaseItemSourceRepository;

    @Inject
    ChargeRepository chargeRepository;

    Lease lease;

    LeaseItem depositItem;

    LeaseItem serviceChargeItem;

    LeaseItem rentItem;

    @Before
    public void setUp() throws Exception {
        lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        depositItem = lease.findFirstItemOfType(LeaseItemType.DEPOSIT);
        rentItem = lease.findFirstItemOfType(LeaseItemType.RENT);
        serviceChargeItem = lease.findFirstItemOfType(LeaseItemType.SERVICE_CHARGE);
    }

    @Test
    public void findOrCreate_is_idempotent() throws Exception {

        // given
        Assertions.assertThat(leaseItemSourceRepository.findByItem(depositItem).size()).isEqualTo(1);

        // when
        depositItem.findOrCreateSourceItem(rentItem);

        // then still
        Assertions.assertThat(leaseItemSourceRepository.findByItem(depositItem).size()).isEqualTo(1);

    }

    @Test
    public void findUniqueTest() throws Exception {

        // given, when, then
        Assertions.assertThat(leaseItemSourceRepository.findUnique(depositItem, rentItem)).isNotNull();

    }

    @Test
    public void findByItemTest() throws Exception {

        // given
        LeaseItem newDeposit = lease.newItem(LeaseItemType.DEPOSIT, LeaseAgreementRoleTypeEnum.LANDLORD, chargeRepository.findByReference(ChargeRefData.GB_DEPOSIT), InvoicingFrequency.QUARTERLY_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT, new LocalDate(2016, 01, 01));

        // when
        newDeposit.newSourceItem(serviceChargeItem);

        // then
        Assertions.assertThat(leaseItemSourceRepository.findByItem(newDeposit).size()).isEqualTo(1);

    }

}
