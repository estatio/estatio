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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.invoice.dom.Constants;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorAtPathRepository;
import org.estatio.module.numerator.dom.NumeratorRepository;
import org.estatio.module.party.dom.Party;

@DomainService(nature = NatureOfService.DOMAIN)
public class NumeratorForOutgoingInvoicesRepository extends UdoDomainService<NumeratorForOutgoingInvoicesRepository> {

    public NumeratorForOutgoingInvoicesRepository() {
        super(NumeratorForOutgoingInvoicesRepository.class);
    }



    @Programmatic
    public Numerator findCollectionNumberNumerator() {
        return numeratorAtPathRepository.findGlobalNumerator(Constants.NumeratorName.COLLECTION_NUMBER, null);
    }




    @Programmatic
    public Numerator createCollectionNumberNumerator(
            final String format,
            final BigInteger lastValue,
            final ApplicationTenancy applicationTenancy) {

        return numeratorAtPathRepository
                .createGlobalNumerator(Constants.NumeratorName.COLLECTION_NUMBER, format, lastValue, applicationTenancy);
    }



    @Programmatic
    public Numerator findInvoiceNumberNumerator(
            final FixedAsset fixedAsset,
            final Party seller,
            final ApplicationTenancy applicationTenancy) {

        final Country country = countryRepository.findCountryByAtPath(applicationTenancy.getPath());
        return numeratorRepository.find(Constants.NumeratorName.INVOICE_NUMBER, country, fixedAsset, seller);
    }



    @Programmatic
    public Numerator createInvoiceNumberNumerator(
            final Property property,
            final String format,
            final BigInteger lastIncrement,
            final ApplicationTenancy applicationTenancy) {
        return numeratorAtPathRepository.createScopedNumerator(
                Constants.NumeratorName.INVOICE_NUMBER, property, format, lastIncrement, applicationTenancy);
    }

    @Programmatic
    public Numerator findOrCreateInvoiceNumberNumerator(
            final Property property,
            final Party seller,
            final String format,
            final BigInteger lastIncrement,
            final ApplicationTenancy applicationTenancy) {
        Country country = countryRepository.findCountryByAtPath(applicationTenancy.getPath());
        return numeratorRepository.findOrCreate(
                Constants.NumeratorName.INVOICE_NUMBER, country, property, seller, format, lastIncrement, applicationTenancy);
    }





    @javax.inject.Inject
    NumeratorAtPathRepository numeratorAtPathRepository;

    @javax.inject.Inject
    CountryRepository countryRepository;

    @javax.inject.Inject
    NumeratorRepository numeratorRepository;


}
