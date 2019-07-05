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

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.numerator.dom.NumeratorRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;

public class NumeratorForOutgoingInvoicesRepository_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    private String format = "0%6d";
    private BigInteger lastIncrement = BigInteger.TEN;

    private ApplicationTenancy globalApplicationTenancy = new ApplicationTenancy() {
        @Override public String getPath() {
            return ApplicationTenancy_enum.Global.getPath();
        }
    };


    Country propertyCountry = new Country();
    Property stubProperty = new Property(){

        @Override public Country getCountry() {
            return propertyCountry;
        }
    };
    Party stubSeller = new Organisation();

    @Mock
    NumeratorRepository mockNumeratorRepository;

    @Mock
    ServiceRegistry2 mockServiceRegistry2;

    @Mock
    ApplicationTenancyRepository mockApplicationTenancyRepository;

    @Mock
    CountryRepository mockCountryRepository;

    NumeratorForOutgoingInvoicesRepository numeratorForOutgoingInvoicesRepository;

    @Before
    public void setup() {

        numeratorForOutgoingInvoicesRepository = new NumeratorForOutgoingInvoicesRepository();
        numeratorForOutgoingInvoicesRepository.numeratorRepository = mockNumeratorRepository;
        numeratorForOutgoingInvoicesRepository.serviceRegistry = mockServiceRegistry2;
        numeratorForOutgoingInvoicesRepository.countryRepository = mockCountryRepository;

        context.checking(new Expectations() {
            {
                allowing(mockServiceRegistry2).lookupService(ApplicationTenancyRepository.class);
                will(returnValue(mockApplicationTenancyRepository));

                allowing(mockApplicationTenancyRepository).findByPath(ApplicationTenancy_enum.Global.getPath());
                will(returnValue(globalApplicationTenancy));

            }
        });
    }

    @Test
    public void findCollectionNumberNumerator() {
        context.checking(new Expectations() {
            {
                oneOf(mockNumeratorRepository).find(
                        NumeratorForOutgoingInvoicesRepository.COLLECTION_NUMBER, null, null, null);
            }
        });
        numeratorForOutgoingInvoicesRepository.findCollectionNumberNumerator();
    }

    @Test
    public void createCollectionNumberNumerator() {
        context.checking(new Expectations() {
            {
                oneOf(mockNumeratorRepository).create(
                        NumeratorForOutgoingInvoicesRepository.COLLECTION_NUMBER, null, null, null, format, lastIncrement, globalApplicationTenancy);
            }
        });
        numeratorForOutgoingInvoicesRepository.createCollectionNumberNumerator(format, lastIncrement);
    }

    @Test
    public void findInvoiceNumberNumerator() {
        context.checking(new Expectations() {
            {
                oneOf(mockNumeratorRepository).find(
                        NumeratorForOutgoingInvoicesRepository.INVOICE_NUMBER, propertyCountry, stubProperty, stubSeller);
            }
        });
        numeratorForOutgoingInvoicesRepository.findInvoiceNumberNumeratorExact(stubProperty, stubSeller);
    }


}
