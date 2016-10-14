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

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.CountryRepository;
import org.incode.module.base.dom.utils.StringUtils;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Property.class
)
public class PropertyRepository extends UdoDomainRepositoryAndFactory<Property> {

    public PropertyRepository() {
        super(PropertyRepository.class, Property.class);
    }

    public Property newProperty(
            final String propertyReference,
            final String name,
            final PropertyType propertyType,
            final String city,
            final Country country,
            final LocalDate acquireDate) {
        final Property property = newTransientInstance();

        property.setReference(propertyReference);
        property.setName(name);
        property.setType(propertyType);

        property.setCity(city);
        property.setCountry(country);
        property.setAcquireDate(acquireDate);

        if (city != null && country != null && property.getLocation() == null) {
            property.lookupLocation(city.concat(", ").concat(country.getName()));
        }

        final ApplicationTenancy propertyApplicationTenancy = estatioApplicationTenancyRepository.findOrCreateTenancyFor(property);
        property.setApplicationTenancyPath(propertyApplicationTenancy.getPath());

        persistIfNotAlready(property);
        return property;
    }

    // //////////////////////////////////////

    public List<Property> findProperties(
            final String referenceOrName) {
        return allMatches("findByReferenceOrName",
                "referenceOrName", StringUtils.wildcardToCaseInsensitiveRegex(referenceOrName));
    }

    // //////////////////////////////////////

    public List<Property> allProperties() {
        List<Property> properties = allInstances();
        if(properties != null) Collections.sort(properties);
        return properties;
    }

    // //////////////////////////////////////

    public Property findPropertyByReference(final String reference) {
        return uniqueMatch("findByReference", "reference", reference);
    }

    /**
     * For {@link Property} as per {@link DomainObject#autoCompleteRepository()}.
     */
    public List<Property> autoComplete(final String searchPhrase) {
        return findProperties("*".concat(searchPhrase).concat("*"));
    }

    // //////////////////////////////////////

    @Inject
    EstatioApplicationTenancyRepositoryForProperty estatioApplicationTenancyRepository;

    /**
     * For testing
     */
    public void setEstatioApplicationTenancyRepository(final EstatioApplicationTenancyRepositoryForProperty estatioApplicationTenancyRepository) {
        this.estatioApplicationTenancyRepository = estatioApplicationTenancyRepository;
    }

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    CountryRepository countryRepository;


}
