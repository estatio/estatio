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
package org.estatio.fixture.lease;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.breaks.prolongation.ProlongationOptionRepository;

public class LeaseProlongationOptionsForOxfTopModel001 extends FixtureScript {

    public static final String LEASE_REF = LeaseForOxfTopModel001Gb.REF;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());

        // exec
        final Lease lease = leaseRepository.findLeaseByReference(LEASE_REF);

        prolongationOptionRepository.newProlongationOption(lease, "5y", "6m", "Some description");
    }

    @Inject
    protected LeaseRepository leaseRepository;

    @Inject
    protected ProlongationOptionRepository prolongationOptionRepository;

}
