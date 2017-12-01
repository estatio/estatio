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
package org.estatio.module.lease.fixtures.prolongation.personas;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.breaks.prolongation.ProlongationOptionRepository;
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

public class LeaseProlongationOptionsForOxfTopModel001 extends FixtureScript {

    public static final String LEASE_REF = Lease_enum.OxfTopModel001Gb.getRef();

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
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


        // exec
        final Lease lease = leaseRepository.findLeaseByReference(LEASE_REF);

        prolongationOptionRepository.newProlongationOption(lease, "5y", "6m", "Some description");
    }

    @Inject
    protected LeaseRepository leaseRepository;

    @Inject
    protected ProlongationOptionRepository prolongationOptionRepository;

}
