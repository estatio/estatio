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
package org.estatio.dom.tax;

import java.util.List;
import javax.inject.Inject;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RegEx;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.Dflt;
import org.estatio.dom.apptenancy.EstatioApplicationTenancies;

@DomainService(repositoryFor = Tax.class)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "80.7")
public class Taxes extends UdoDomainRepositoryAndFactory<Tax> {

    public Taxes() {
        super(Taxes.class, Tax.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Tax newTax(
            final @ParameterLayout(named = "Reference") @Parameter(regexPattern = RegexValidation.REFERENCE) String reference,
            final @Named("Name") @Optional String name) {
            final ApplicationTenancy applicationTenancy) {
        final Tax tax = newTransientInstance();
        tax.setReference(reference);
        tax.setName(name);
        tax.setApplicationTenancyPath(applicationTenancy.getPath());
        persist(tax);
        return tax;
    }

    public List<ApplicationTenancy> choices2NewTax() {
        return estatioApplicationTenancies.countryTenanciesForCurrentUser();
    }

    public ApplicationTenancy default2NewTax() {
        return Dflt.of(choices2NewTax());
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<Tax> allTaxes() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public Tax findByReference(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public Tax findOrCreate(final String reference, final String name, final ApplicationTenancy applicationTenancy) {
        Tax tax =  findByReference(reference);
        if (tax == null) {
            tax = newTax(reference, name, applicationTenancy);
        }
        return tax;
    }

    
    // //////////////////////////////////////

    @Inject
    private EstatioApplicationTenancies estatioApplicationTenancies;


}
