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
import com.google.common.base.Function;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RegEx;
import org.estatio.dom.Dflt;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.EstatioApplicationTenancies;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.Country;
import org.estatio.dom.utils.StringUtils;

@DomainService(repositoryFor = Property.class)
@DomainServiceLayout(
        named = "Fixed Assets",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "10.1")
public class Properties extends UdoDomainRepositoryAndFactory<Property> {

    public Properties() {
        super(Properties.class, Property.class);
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
        final Property property = newTransientInstance();

        final ApplicationTenancy propertyApplicationTenancy = estatioApplicationTenancies.findOrCreatePropertyTenancy(countryApplicationTenancy, propertyReference);
        estatioApplicationTenancies.findOrCreateLocalDefaultTenancy(propertyApplicationTenancy);
        estatioApplicationTenancies.findOrCreateLocalTaTenancy(propertyApplicationTenancy);

        property.setApplicationTenancyPath(propertyApplicationTenancy.getPath());
        property.setReference(propertyReference);
        property.setName(name);
        property.setType(propertyType);

        property.setCity(city);
        property.setCountry(country);
        property.setAcquireDate(acquireDate);

        if (city != null && country != null && property.getLocation() == null) {
            property.lookupLocation(city.concat(", ").concat(country.getName()));
        }

        persistIfNotAlready(property);
        return property;
    }

    public List<ApplicationTenancy> choices6NewProperty() {
        return estatioApplicationTenancies.countryTenanciesForCurrentUser();
    }

    public ApplicationTenancy default6NewProperty() {
        return Dflt.of(choices6NewProperty());
    }

    /**
     * Intended to be applied only to {@link org.isisaddons.module.security.dom.tenancy.ApplicationTenancy} which are
     * know to be {@link org.estatio.dom.valuetypes.ApplicationTenancyLevel#isCountry() countries}.
     */
    private static Function<ApplicationTenancy, String> countryCodeOf() {
        return new Function<ApplicationTenancy, String>() {
            @Override
            public String apply(final ApplicationTenancy input) {
                return input.getName().substring(1);
            }
        };
    }


    public PropertyType default2NewProperty() {
        return PropertyType.MIXED;
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "2")
    public List<Property> findProperties(
            @ParameterLayout(named = "Reference or Name") final String referenceOrName) {
        return allMatches("findByReferenceOrName",
                "referenceOrName", StringUtils.wildcardToCaseInsensitiveRegex(referenceOrName));
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "3")
    public List<Property> allProperties() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public Property findPropertyByReference(final String reference) {
        return mustMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public Property findPropertyByReferenceElseNull(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    // //////////////////////////////////////

    @CollectionLayout(hidden = Where.EVERYWHERE)
    public List<Property> autoComplete(final String searchPhrase) {
        return findProperties("*".concat(searchPhrase).concat("*"));
    }

    // //////////////////////////////////////

    @Inject
    EstatioApplicationTenancies estatioApplicationTenancies;

    @Inject
    Countries countries;


}
