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
package org.estatio.fixture.guarantee;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.estatio.dom.guarantee.Guarantee;
import org.estatio.dom.guarantee.GuaranteeType;
import org.estatio.dom.guarantee.Guarantees;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseMenu;
import org.estatio.fixture.EstatioFixtureScript;

public abstract class GuaranteeAbstract extends EstatioFixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    protected Guarantee newGuarantee(
            final Lease lease,
            final String reference, final String name, final GuaranteeType guaranteeType,
            final LocalDate startDate, final LocalDate endDate,
            final String description,
            final BigDecimal maximumAmount,
            final ExecutionContext executionContext) {

        final Guarantee guarantee = guarantees.newGuarantee(lease, reference, name, guaranteeType, startDate, endDate, description, maximumAmount, null);

        return executionContext.addResult(this, guarantee);

    }

    // //////////////////////////////////////

    @Inject
    protected LeaseMenu leaseMenu;

    @Inject
    protected Guarantees guarantees;

}
