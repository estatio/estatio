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
package org.estatio.dom.lease;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.ApplicationTenancyInvariantsService;

@DomainService(repositoryFor = LeaseType.class)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "80.1")
public class LeaseTypes extends UdoDomainRepositoryAndFactory<LeaseType> {

    public LeaseTypes() {
        super(LeaseTypes.class, LeaseType.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public LeaseType newLeaseType(
            final @Parameter(regexPattern = RegexValidation.REFERENCE, regexPatternReplacement = RegexValidation.REFERENCE_DESCRIPTION) String reference,
            final @Parameter(optionality = Optionality.OPTIONAL) String name,
            final @Parameter(optionality = Optionality.OPTIONAL) ApplicationTenancy applicationTenancy) {
        final LeaseType leaseType = newTransientInstance();
        leaseType.setReference(reference);
        leaseType.setName(name);
        leaseType.setApplicationTenancyPath(applicationTenancy != null ? applicationTenancy.getPath() : ApplicationTenancyInvariantsService.GLOBAL_APPLICATION_TENANCY_PATH);
        persistIfNotAlready(leaseType);
        return leaseType;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<LeaseType> allLeaseTypes() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public LeaseType findByReference(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    // //////////////////////////////////////

    @Programmatic
    public LeaseType findOrCreate(final String reference, final String name, final ApplicationTenancy applicationTenancy) {
        LeaseType leaseType = findByReference(reference);
        if (leaseType == null) {
            leaseType = newLeaseType(reference, name == null ? reference : name, applicationTenancy);
        }
        return leaseType;
    }

}
