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
package org.estatio.module.asset.dom;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.datanucleus.query.typesafe.TypesafeQuery;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.StringUtils;
import org.incode.module.country.dom.impl.Country;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

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


    @Programmatic
    public void ping() {

        final TypesafeQuery<Property> q = isisJdoSupport.newTypesafeQuery(Property.class);
        final QProperty cand = QProperty.candidate();
        q.orderBy(cand.reference.asc());
        q.range(0,2);

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

        final String refRegex = StringUtils.wildcardToCaseInsensitiveRegex("*".concat(searchPhrase).concat("*"));
        return allMatches("findByReferenceOrName",
                "referenceOrName", refRegex
                );
    }

    // //////////////////////////////////////

    @Inject
    MeService meService;

    @Inject
    IsisJdoSupport isisJdoSupport;

    @Inject
    EstatioApplicationTenancyRepositoryForProperty estatioApplicationTenancyRepository;

    /**
     * For testing
     */
    public void setEstatioApplicationTenancyRepository(final EstatioApplicationTenancyRepositoryForProperty estatioApplicationTenancyRepository) {
        this.estatioApplicationTenancyRepository = estatioApplicationTenancyRepository;
    }


}
