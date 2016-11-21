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
package org.estatio.dom.tax;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;

import static org.assertj.core.api.Assertions.assertThat;

public class TaxRateRepositoryTest {

    FinderInteraction finderInteraction;

    TaxRateRepository taxRateRepository;

    Tax tax;
    LocalDate date;

    @Before
    public void setup() {

        tax = new Tax();
        date = new LocalDate(2013, 4, 1);

        taxRateRepository = new TaxRateRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<TaxRate> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };

    }

    public static class FindTaxRateByTaxAndDate extends TaxRateRepositoryTest {
        @Test
        public void happyCase() {

            taxRateRepository.findTaxRateByTaxAndDate(tax, date);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.FIRST_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(TaxRate.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByTaxAndDate");
            assertThat(finderInteraction.getArgumentsByParameterName().get("tax")).isEqualTo((Object) tax);
            assertThat(finderInteraction.getArgumentsByParameterName().get("date")).isEqualTo((Object) date);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(2);

        }
    }

}
