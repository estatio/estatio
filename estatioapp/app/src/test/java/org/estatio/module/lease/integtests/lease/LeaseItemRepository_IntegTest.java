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

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.charges.refdata.ChargeRefData;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemRepository;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
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

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseItemRepository_IntegTest extends LeaseModuleIntegTestAbstract {

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

                executionContext.executeChild(this, Lease_enum.OxfPoison003Gb.toFixtureScript());
            }
        });
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    LeaseItemRepository leaseItemRepository;

    @Inject ChargeRepository chargeRepository;

    Lease lease;

    @Before
    public void setUp() throws Exception {
        lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
    }

    public static class FindLeaseItem extends LeaseItemRepository_IntegTest {

        @Test
        public void findLeaseItem() throws Exception {
            LeaseItem leaseItem = leaseItemRepository.findLeaseItem(lease, LeaseItemType.RENT, lease.getStartDate(), BigInteger.valueOf(1));
            assertThat(lease.getItems().contains(leaseItem)).isTrue();
        }
    }

    public static class FindLeaseItemByType extends LeaseItemRepository_IntegTest {

        @Test
        public void findLeaseItemByType() throws Exception {
            // given
            LeaseItem currentItem = leaseItemRepository.findLeaseItem(lease, LeaseItemType.RENT, lease.getStartDate(), BigInteger.valueOf(1));

            // when
            final ApplicationTenancy firstLocalAppTenancy = lease.getApplicationTenancy().getChildren().first();
            LeaseItem newItem = leaseItemRepository.newLeaseItem(lease, currentItem.getType(), LeaseAgreementRoleTypeEnum.LANDLORD, currentItem.getCharge(), currentItem.getInvoicingFrequency(), currentItem.getPaymentMethod(), currentItem.getStartDate().plusYears(1));
            lease.getItems().add(newItem);

            // then
            List<LeaseItem> results = leaseItemRepository.findLeaseItemsByType(lease, LeaseItemType.RENT);
            assertThat(results.size()).isEqualTo(2);
        }

    }
    public static class FindByLeaseAndTypeAndStartDateAndInvoicedBy extends LeaseItemRepository_IntegTest {

        @Test
        public void happy_case() throws Exception {
            //given
            Charge charge = chargeRepository.findByReference(ChargeRefData.GB_RENT);
            Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            final LeaseItemType leaseItemType = LeaseItemType.RENT;
            final LeaseAgreementRoleTypeEnum agreementRoleType = LeaseAgreementRoleTypeEnum.TENANTS_ASSOCIATION;
            final InvoicingFrequency invoicingFrequency = InvoicingFrequency.QUARTERLY_IN_ADVANCE;
            final PaymentMethod paymentMethod = PaymentMethod.BANK_TRANSFER;

            lease.newItem(leaseItemType, agreementRoleType, charge, invoicingFrequency, paymentMethod, lease.getStartDate());

            // When
            final LeaseItem foundItem = leaseItemRepository.findByLeaseAndTypeAndStartDateAndInvoicedBy(lease, leaseItemType, lease.getStartDate(), agreementRoleType);

            //Then
            assertThat(foundItem.getInvoicedBy()).isEqualTo(agreementRoleType);
        }
    }

}
