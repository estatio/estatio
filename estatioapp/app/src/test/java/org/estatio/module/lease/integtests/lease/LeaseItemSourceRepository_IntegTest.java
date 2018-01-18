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
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemSourceRepository;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDeposit_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForEntryFee_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForMarketing_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceChargeBudgeted_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTax_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

public class LeaseItemSourceRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb_TA.builder());
                executionContext.executeChild(this, LeaseItemForServiceChargeBudgeted_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForDiscount_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForEntryFee_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForTax_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForDeposit_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForMarketing_enum.OxfTopModel001Gb.builder());

                executionContext.executeChild(this, Lease_enum.OxfPoison003Gb.builder());
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
        LeaseItem newDeposit = lease.newItem(
                LeaseItemType.DEPOSIT, LeaseAgreementRoleTypeEnum.LANDLORD,
                Charge_enum.GbDeposit.findUsing(serviceRegistry),
                InvoicingFrequency.QUARTERLY_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT, new LocalDate(2016, 1, 1));

        // when
        newDeposit.newSourceItem(serviceChargeItem);

        // then
        Assertions.assertThat(leaseItemSourceRepository.findByItem(newDeposit).size()).isEqualTo(1);

    }

}
