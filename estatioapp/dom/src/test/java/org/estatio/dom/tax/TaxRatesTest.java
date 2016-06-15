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
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TaxRatesTest {

    FinderInteraction finderInteraction;

    TaxRateRepository taxRateRepository;

    TaxRateMenu taxRateMenu;

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

        taxRateMenu = new TaxRateMenu() {

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

    public static class FindTaxRateByTaxAndDate extends TaxRatesTest {
        @Test
        public void happyCase() {

            taxRateRepository.findTaxRateByTaxAndDate(tax, date);

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(TaxRate.class));
            assertThat(finderInteraction.getQueryName(), is("findByTaxAndDate"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("tax"), is((Object) tax));
            assertThat(finderInteraction.getArgumentsByParameterName().get("date"), is((Object) date));

            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
        }
    }

    public static class AllTaxRates extends TaxRatesTest {

        @Test
        public void happyCase() {

            taxRateMenu.allTaxRates();

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
        }
    }

}
