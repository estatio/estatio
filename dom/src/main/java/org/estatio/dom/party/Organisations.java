/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.dom.party;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

public class Organisations extends EstatioDomainService<Organisation> {

    public Organisations() {
        super(Organisations.class, Organisation.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name="Parties", sequence = "2")
    public Organisation newOrganisation(
            final @Named("Reference") String reference, 
            final @Named("Name") String name) {
        final Organisation organisation = newTransientInstance(Organisation.class);
        organisation.setReference(reference);
        organisation.setName(name);
        persist(organisation);
        return organisation;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Parties", sequence = "5")
    public Organisation findOrganisation(
            final @Named("Reference or Name") @DescribedAs("May include wildcards '*' and '?'") String referenceOrName) {
        return firstMatch("findByReferenceOrName", "referenceOrName", StringUtils.wildcardToCaseInsensitiveRegex(referenceOrName));
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Parties", sequence = "99.1")
    public List<Organisation> allOrganisations() {
        return allInstances();
    }

}
