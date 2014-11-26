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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.CssClass;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.Lease;

@DomainService(menuOrder = "93")
public class EstatioFixtureScripts extends FixtureScripts {

    public EstatioFixtureScripts() {
        super("org.estatio");
    }

    @MemberOrder(name = "Administration", sequence = "9")
    @Override
    public List<FixtureResult> runFixtureScript(
            final FixtureScript fixtureScript,
            final String parameters) {
        return super.runFixtureScript(fixtureScript, parameters);
    }

    @Override
    public List<FixtureScript> choices0RunFixtureScript() {
        return super.choices0RunFixtureScript();
    }

    @MemberOrder(name = "Administration", sequence = "9.2")
    @Prototype
    public List<FixtureResult> createRetroInvoicesForProperty(
            final Property property,
            final @Named("Start due date") LocalDate startDueDate,
            final @Named("Next due date") @Optional LocalDate nextDueDate) {
        final CreateRetroInvoices creator = getContainer().newTransientInstance(CreateRetroInvoices.class);
        final FixtureScript.ExecutionContext executionContext = newExecutionContext(null);
        creator.createProperty(property, startDueDate, nextDueDate, executionContext);
        return executionContext.getResults();
    }

    @MemberOrder(name = "Administration", sequence = "9.3")
    @CssClass("danger")
    // application.css
    public List<FixtureResult> createRetroInvoicesForLease(
            final Lease lease,
            final @Named("Start due date") LocalDate startDueDate,
            final @Named("Next due date") LocalDate nextDueDate) {
        final CreateRetroInvoices creator = getContainer().newTransientInstance(CreateRetroInvoices.class);
        final FixtureScript.ExecutionContext executionContext = newExecutionContext(null);
        creator.createLease(lease, startDueDate, nextDueDate, executionContext);
        return executionContext.getResults();
    }

    public boolean hideCreateRetroInvoicesForLease() {
        return !getContainer().getUser().hasRole("superuser_role");
    }

}
