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

import org.estatio.module.lease.app.NumeratorForOutgoingInvoicesMenu;
import org.estatio.module.lease.dom.invoicing.NumeratorForOutgoingInvoicesRepository;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class NumeratorForOutgoingInvoicesMenu_collection_IntegTest extends LeaseModuleIntegTestAbstract {

    private final String format = "GBFO-%04d";
    private final BigInteger lastIncrement = BigInteger.ZERO;


    @Inject
    NumeratorForOutgoingInvoicesRepository repository;

    @Inject
    NumeratorForOutgoingInvoicesMenu menu;

    @Inject
    NumeratorRepository numeratorRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext ec) {
            }
        });

    }


    @Test
    public void create_and_find_invoice_numerators() {

        // given
        final List<Numerator> numerators = numeratorRepository.allNumerators();
        assertThat(numerators).isEmpty();


        // when
        final Numerator numerator = wrap(menu).findCollectionNumberNumerator();

        // then
        assertThat(numerator).isNull();

        // when
        // ... nb: there is no public API for this, because there is just a global numerator.
        // ... we therefore need to go "under the covers"
        final Numerator numerator1 = repository.createCollectionNumberNumerator(format, lastIncrement);

        // then
        assertThat(numerator1).isNotNull();
        assertThat(numerator1.getName()).isEqualTo(NumeratorForOutgoingInvoicesRepository.COLLECTION_NUMBER);
        assertThat(numerator1.getCountry()).isNull();
        assertThat(numerator1.getObject()).isNull();
        assertThat(numerator1.getObject2()).isNull();
        assertThat(numerator1.getFormat()).isEqualTo(format);
        assertThat(numerator1.getLastIncrement()).isEqualTo(lastIncrement);
        assertThat(numerator1.getApplicationTenancyPath()).isEqualTo("/");

        final List<Numerator> numeratorsFromRepoAfter = numeratorRepository.allNumerators();
        assertThat(numeratorsFromRepoAfter).hasSize(1);
        assertThat(numeratorsFromRepoAfter.get(0)).isSameAs(numerator1);



        // when
        final Numerator numeratorViaMenu = wrap(menu).findCollectionNumberNumerator();

        // then
        assertThat(numeratorViaMenu).isSameAs(numerator1);
    }


}