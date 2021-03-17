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
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemRepository;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseItemRepository_IntegTest extends LeaseModuleIntegTestAbstract {

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

                executionContext.executeChild(this, Lease_enum.OxfPoison003Gb.builder());
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
            Charge charge = Charge_enum.GbRent.findUsing(serviceRegistry);
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
