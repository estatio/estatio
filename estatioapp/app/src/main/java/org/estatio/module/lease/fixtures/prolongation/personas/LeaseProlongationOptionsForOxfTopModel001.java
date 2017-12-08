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
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDeposit_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForEntryFee_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForMarketing_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForPercentage_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceChargeBudgeted_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTax_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;

public class LeaseProlongationOptionsForOxfTopModel001 extends FixtureScript {

    public static final String LEASE_REF = Lease_enum.OxfTopModel001Gb.getRef();

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb_TA.builder());
        executionContext.executeChild(this, LeaseItemForServiceChargeBudgeted_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForPercentage_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForDiscount_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForEntryFee_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForTax_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForDeposit_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForMarketing_enum.OxfTopModel001Gb.builder());


        // exec
        final Lease lease = leaseRepository.findLeaseByReference(LEASE_REF);

        prolongationOptionRepository.newProlongationOption(lease, "5y", "6m", "Some description");
    }

    @Inject
    protected LeaseRepository leaseRepository;

    @Inject
    protected ProlongationOptionRepository prolongationOptionRepository;

}
