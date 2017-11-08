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
package org.estatio.module.tax.integtests;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRepository;
import org.estatio.module.tax.fixtures.data.Tax_data;

import static org.assertj.core.api.Assertions.assertThat;

public class TaxRepository_IntegTest extends TaxModuleIntegTestAbstract {

    @Inject
    TaxRepository taxRepository;

    public static class AllTaxes extends TaxRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            final int size = Tax_data.values().length;

            // when
            final List<Tax> taxList = taxRepository.allTaxes();

            // then
            assertThat(taxList.size()).isEqualTo(size);
        }
    }

    public static class FindByReference extends TaxRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            final Tax_data data = fakeDataService.enums().anyOf(Tax_data.class);

            // when
            final Tax tax = taxRepository.findByReference(data.getReference());

            // then
            assertThat(tax.getReference()).isEqualTo(data.getReference());
        }
    }

    public static class FindByApplicationTenancy extends TaxRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final Tax_data data = fakeDataService.enums().anyOf(Tax_data.class);
            final Tax tax = data.findUsing(serviceRegistry);
            final ApplicationTenancy applicationTenancy = tax.getApplicationTenancy();

            // when
            final Collection<Tax> taxCollection = taxRepository.findByApplicationTenancy(applicationTenancy);

            // then
            assertThat(taxCollection.size()).isEqualTo(1);
        }
    }



}