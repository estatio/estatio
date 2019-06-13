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
package org.estatio.module.lease.integtests.app;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.fixtures.enums.Country_enum;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.lease.app.NumeratorForOutgoingInvoicesMenu;
import org.estatio.module.lease.dom.invoicing.NumeratorForOutgoingInvoicesRepository;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class NumeratorForOutgoingInvoicesMenu_IntegTest extends LeaseModuleIntegTestAbstract {

    private final String format = "GBFO-%04d";
    private final BigInteger lastIncrement = BigInteger.ZERO;


    @Inject
    NumeratorForOutgoingInvoicesMenu numeratorForOutgoingInvoicesMenu;

    @Inject
    NumeratorRepository numeratorRepository;

    Property propertyRonIt;
    Property propertyGraIt;
    Organisation orgPartyA;
    Organisation orgPartyB;
    Country countryIta;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext ec) {
                ec.executeChildren(this,
                        Property_enum.RonIt,
                        Property_enum.GraIt,
                        Organisation_enum.HelloWorldIt,
                        Organisation_enum.HelloWorldIt01
                );
            }
        });

        countryIta = Country_enum.ITA.findUsing(serviceRegistry);
        propertyRonIt = Property_enum.RonIt.findUsing(serviceRegistry);
        propertyGraIt = Property_enum.GraIt.findUsing(serviceRegistry);
        orgPartyA = Organisation_enum.HelloWorldIt.findUsing(serviceRegistry);
        orgPartyB = Organisation_enum.HelloWorldIt01.findUsing(serviceRegistry);
    }


    @Test
    public void create_and_find_invoice_numerators() {

        // given
        final List<Numerator> numerators = numeratorRepository.allNumerators();
        assertThat(numerators).isEmpty();


        // when
        final Numerator numeratorRonPartyA = wrap(numeratorForOutgoingInvoicesMenu)
                .createInvoiceNumberNumerator(propertyRonIt, orgPartyA, format, lastIncrement);

        // then
        assertThat(numeratorRonPartyA).isNotNull();
        assertThat(numeratorRonPartyA.getName()).isEqualTo(NumeratorForOutgoingInvoicesRepository.INVOICE_NUMBER);
        assertThat(numeratorRonPartyA.getCountry()).isEqualTo(countryIta);
        assertThat(numeratorRonPartyA.getObject()).isEqualTo(propertyRonIt);
        assertThat(numeratorRonPartyA.getObject2()).isEqualTo(orgPartyA);
        assertThat(numeratorRonPartyA.getFormat()).isEqualTo(format);
        assertThat(numeratorRonPartyA.getLastIncrement()).isEqualTo(lastIncrement);
        assertThat(numeratorRonPartyA.getApplicationTenancyPath()).isEqualTo("/ITA");

        final List<Numerator> numeratorsFromRepoAfter = numeratorRepository.allNumerators();
        assertThat(numeratorsFromRepoAfter).hasSize(1);
        assertThat(numeratorsFromRepoAfter.get(0)).isSameAs(numeratorRonPartyA);



        // when
        // ... create another numerator for the same property, but different owner
        final Numerator numeratorRonPartyB = wrap(numeratorForOutgoingInvoicesMenu)
                .createInvoiceNumberNumerator(propertyRonIt, orgPartyB, format, lastIncrement);

        // then
        assertThat(numeratorRonPartyB).isNotNull();
        assertThat(numeratorRonPartyB.getName()).isEqualTo(NumeratorForOutgoingInvoicesRepository.INVOICE_NUMBER);
        assertThat(numeratorRonPartyB.getCountry()).isEqualTo(countryIta);
        assertThat(numeratorRonPartyB.getObject()).isEqualTo(propertyRonIt);
        assertThat(numeratorRonPartyB.getObject2()).isEqualTo(orgPartyB);
        assertThat(numeratorRonPartyB.getApplicationTenancyPath()).isEqualTo("/ITA");

        List<Numerator> numeratorsFromRepoAfter2 = numeratorRepository.allNumerators();
        assertThat(numeratorsFromRepoAfter2).hasSize(2);
        assertThat(numeratorsFromRepoAfter2.get(1)).isSameAs(numeratorRonPartyB);



        // when
        // ... create another numerator for the different property, same owner
        final Numerator numeratorGraPartyA = wrap(numeratorForOutgoingInvoicesMenu)
                .createInvoiceNumberNumerator(propertyGraIt, orgPartyA, format, lastIncrement);

        // then
        assertThat(numeratorGraPartyA).isNotNull();
        assertThat(numeratorGraPartyA.getName()).isEqualTo(NumeratorForOutgoingInvoicesRepository.INVOICE_NUMBER);
        assertThat(numeratorGraPartyA.getCountry()).isEqualTo(countryIta);
        assertThat(numeratorGraPartyA.getObject()).isEqualTo(propertyGraIt);
        assertThat(numeratorGraPartyA.getObject2()).isEqualTo(orgPartyA);
        assertThat(numeratorGraPartyA.getApplicationTenancyPath()).isEqualTo("/ITA");

        final List<Numerator> numeratorsFromRepoAfter3 = numeratorRepository.allNumerators();
        assertThat(numeratorsFromRepoAfter3).hasSize(3);
        assertThat(numeratorsFromRepoAfter3.get(2)).isSameAs(numeratorGraPartyA);



        // when
        // ... create attempt to create another numerator for the same property, same owner
        final Numerator numeratorGraPartyA_2 = wrap(numeratorForOutgoingInvoicesMenu)
                .createInvoiceNumberNumerator(propertyGraIt, orgPartyA, format, lastIncrement);

        // then
        assertThat(numeratorGraPartyA_2).isNotNull();
        assertThat(numeratorGraPartyA_2).isSameAs(numeratorGraPartyA_2);

        final List<Numerator> numeratorsFromRepoAfter3_2 = numeratorRepository.allNumerators();
        assertThat(numeratorsFromRepoAfter3_2).hasSize(3);


        // when
        // ... retrieve an existing
        final Numerator numeratorGraPartyARetrieve = wrap(numeratorForOutgoingInvoicesMenu)
                .findInvoiceNumberNumerator(propertyGraIt, orgPartyA);
        // then
        assertThat(numeratorGraPartyARetrieve).isSameAs(numeratorGraPartyA);


        // when
        // ... retrieve an non-existent
        final Numerator numeratorGraPartyBRetrieve = wrap(numeratorForOutgoingInvoicesMenu)
                .findInvoiceNumberNumerator(propertyGraIt, orgPartyB);
        // then
        assertThat(numeratorGraPartyBRetrieve).isNull();
    }


}