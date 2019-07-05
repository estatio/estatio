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
package org.estatio.module.lease.dom.invoicing;

import java.math.BigInteger;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.countryapptenancy.dom.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;
import org.estatio.module.party.dom.Party;

@DomainService(nature = NatureOfService.DOMAIN)
public class NumeratorForOutgoingInvoicesRepository extends UdoDomainService<NumeratorForOutgoingInvoicesRepository> {

    public static final String COLLECTION_NUMBER = "Collection number";
    public static final String INVOICE_NUMBER = "Invoice number";

    public NumeratorForOutgoingInvoicesRepository() {
        super(NumeratorForOutgoingInvoicesRepository.class);
    }


    public Numerator findCollectionNumberNumerator() {
        return numeratorRepository.find(COLLECTION_NUMBER, null, null, null);
    }

    public Numerator createCollectionNumberNumerator(
            final String format,
            final BigInteger lastValue) {

        final ApplicationTenancy globalAppTenancy = ApplicationTenancy_enum.Global.findUsing(serviceRegistry);
        return numeratorRepository.create(
                COLLECTION_NUMBER, null, null, null, format, lastValue, globalAppTenancy);
    }

    /**
     * Same as {@link #findInvoiceNumberNumeratorExact(Property, Party)}, but if there is no numerator for this
     * property, then will look at that {@link Property}'s {@link Property#getNumeratorProperty() numerator property}
     * and use that instead.
     */
    public Numerator findInvoiceNumberNumerator(
            final Property property,
            final Party seller) {

        final Numerator numeratorIfAny = findInvoiceNumberNumeratorExact(property, seller);
        if (numeratorIfAny != null) {
            return numeratorIfAny;
        }

        // otherwise, if this property has a "numeratorProperty", then use that instead.
        final Property numeratorProperty = property.getNumeratorProperty();
        if(numeratorProperty != null) {
            return findInvoiceNumberNumeratorExact(numeratorProperty, seller);
        }

        return null;
    }

    public Numerator findInvoiceNumberNumeratorExact(
            final Property property,
            final Party seller) {

        final Country country = property.getCountry();
        return numeratorRepository.find(INVOICE_NUMBER, country, property, seller);
    }

    public Numerator createInvoiceNumberNumerator(
            final Property property,
            final Party seller,
            final String format,
            final BigInteger lastIncrement) {

        final Numerator numerator = findInvoiceNumberNumeratorExact(property, seller);
        if (numerator != null) {
            return numerator;
        }

        final Country country = property.getCountry();
        final ApplicationTenancy countryTenancy =
                estatioApplicationTenancyRepositoryForCountry.findOrCreateTenancyFor(country);

        return numeratorRepository.create(
                INVOICE_NUMBER, country, property, seller, format, lastIncrement, countryTenancy);
    }


    @javax.inject.Inject
    EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepositoryForCountry;

    @javax.inject.Inject
    CountryRepository countryRepository;

    @javax.inject.Inject
    NumeratorRepository numeratorRepository;

    @Inject
    ServiceRegistry2 serviceRegistry;

}
