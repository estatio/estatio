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
package org.estatio.fixturescripts;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.Lease;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.fixturescripts.EstatioFixtureScripts",
        menuOrder = "93"
)
@DomainServiceLayout(
        named = "Prototyping",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "20.1"
)
public class EstatioFixtureScripts {

    @Action(
            restrictTo = RestrictTo.PROTOTYPING
    )
    @MemberOrder(sequence = "2")
    public List<FixtureResult> createRetroInvoicesForProperty(
            final Property property,
            @ParameterLayout(
                    named = "Start due date"
            )
            final LocalDate startDueDate,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(
                    named = "Nextdue date"
            )
            final LocalDate nextDueDate) {
        final CreateRetroInvoices creator = container.newTransientInstance(CreateRetroInvoices.class);
        final FixtureScript.ExecutionContext executionContext = fixtureScripts.newExecutionContext(null);
        creator.createProperty(property, startDueDate, nextDueDate, executionContext);
        return executionContext.getResults();
    }

    @Action(
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-bolt"
    )
    @MemberOrder(sequence = "3")
    public List<FixtureResult> createRetroInvoicesForLease(
            final Lease lease,
            @ParameterLayout(
                    named = "Start due date"
            )
            final LocalDate startDueDate,
            @ParameterLayout(
                    named = "Nextdue date"
            )
            final LocalDate nextDueDate) {
        final CreateRetroInvoices creator = container.newTransientInstance(CreateRetroInvoices.class);
        final FixtureScript.ExecutionContext executionContext = fixtureScripts.newExecutionContext(null);
        creator.createLease(lease, startDueDate, nextDueDate, executionContext);
        return executionContext.getResults();
    }

    public boolean hideCreateRetroInvoicesForLease() {
        return !container.getUser().hasRole("superuser_role");
    }


    // //////////////////////////////////////

    @Inject
    FixtureScripts fixtureScripts;
    @Inject
    DomainObjectContainer container;

}
