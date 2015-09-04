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
package org.estatio.dom.asset;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.Dflt;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.ApplicationTenancyRepository;
import org.estatio.dom.geography.Country;

@DomainService(repositoryFor = Property.class)
@DomainServiceLayout(
        named = "Fixed Assets",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "10.1")
public class PropertyMenu extends UdoDomainRepositoryAndFactory<Property> {

    public PropertyMenu() {
        super(PropertyMenu.class, Property.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Property newProperty(
            final @Named("Reference") @RegEx(validation = RegexValidation.Property.REFERENCE, caseSensitive = true) String propertyReference,
            final @ParameterLayout(named = "Name") String name,
            final PropertyType propertyType,
            final @ParameterLayout(named = "City") @Parameter(optionality = Optionality.OPTIONAL) String city,
            final @Parameter(optionality = Optionality.OPTIONAL) Country country,
            final @Named("Acquire date") @Optional LocalDate acquireDate,
            final @Named("Country-level Application Tenancy") ApplicationTenancy countryApplicationTenancy) {
        return propertyRepository.newProperty(
                propertyReference, name,
                propertyType,
                city, country,
                acquireDate, countryApplicationTenancy);
    }

    public List<ApplicationTenancy> choices6NewProperty() {
        return applicationTenancyRepository.countryTenanciesForCurrentUser();
    }

    public PropertyType default2NewProperty() {
        return PropertyType.MIXED;
    }

    public ApplicationTenancy default6NewProperty() {
        return Dflt.of(choices6NewProperty());
    }


    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<Property> findProperties(
            @ParameterLayout(named = "Reference or Name") final String referenceOrName) {
        return propertyRepository.findProperties(referenceOrName);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public List<Property> allProperties() {
        return propertyRepository.allProperties();
    }


    // //////////////////////////////////////

    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;

    @Inject
    PropertyRepository propertyRepository;


}
