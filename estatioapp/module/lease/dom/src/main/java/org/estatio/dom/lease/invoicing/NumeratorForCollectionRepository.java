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
package org.estatio.dom.lease.invoicing;

import java.math.BigInteger;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.Constants;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class NumeratorForCollectionRepository extends UdoDomainService<NumeratorForCollectionRepository> {

    public NumeratorForCollectionRepository() {
        super(NumeratorForCollectionRepository.class);
    }



    @Programmatic
    public Numerator findCollectionNumberNumerator() {
        return numeratorRepository.findGlobalNumerator(Constants.NumeratorName.COLLECTION_NUMBER, null);
    }




    @Programmatic
    public Numerator createCollectionNumberNumerator(
            final String format,
            final BigInteger lastValue,
            final ApplicationTenancy applicationTenancy) {

        return numeratorRepository
                .createGlobalNumerator(Constants.NumeratorName.COLLECTION_NUMBER, format, lastValue, applicationTenancy);
    }





    @Programmatic
    public Numerator findInvoiceNumberNumerator(
            final FixedAsset fixedAsset,
            final ApplicationTenancy applicationTenancy) {
        return numeratorRepository
                .findScopedNumeratorIncludeWildCardMatching(Constants.NumeratorName.INVOICE_NUMBER, fixedAsset, applicationTenancy);
    }




    @Programmatic
    public Numerator createInvoiceNumberNumerator(
            final Property property,
            final String format,
            final BigInteger lastIncrement,
            final ApplicationTenancy applicationTenancy) {
        return numeratorRepository.createScopedNumerator(
                Constants.NumeratorName.INVOICE_NUMBER, property, format, lastIncrement, applicationTenancy);
    }





    @javax.inject.Inject
    NumeratorRepository numeratorRepository;


}
