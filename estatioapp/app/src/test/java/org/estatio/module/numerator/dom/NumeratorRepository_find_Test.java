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
package org.estatio.module.numerator.dom;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Ignoring;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;

import org.estatio.module.party.dom.Organisation;

import static org.assertj.core.api.Assertions.assertThat;

public class NumeratorRepository_find_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    FinderInteraction finderInteraction;

    NumeratorRepository numeratorRepository;

    @Mock
    BookmarkService mockBookmarkService;

    @Ignoring
    @Mock
    Country mockCountry;

    @Ignoring
    @Mock
    PropertyForTest mockProperty;

    @Ignoring
    @Mock
    Organisation mockSeller;

    Bookmark propertyBookmark;
    Bookmark sellerBookmark;

    @Before
    public void setup() {

        propertyBookmark = new Bookmark("PROP", "123");
        sellerBookmark = new Bookmark("ORG", "456");

        context.checking(new Expectations() {
            {
                allowing(mockBookmarkService).bookmarkFor(mockProperty);
                will(returnValue(propertyBookmark));

                allowing(mockBookmarkService).bookmarkFor(mockSeller);
                will(returnValue(sellerBookmark));
            }
        });
        numeratorRepository = new NumeratorRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<Numerator> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };
        numeratorRepository.bookmarkService = mockBookmarkService;
    }

    public static class FindNumerator extends NumeratorRepository_find_Test {

        private static final String INVOICE_NUMBER_NUMERATOR_NAME = "Invoice number";

        @Test
        public void findNumerator() {

            numeratorRepository.find(INVOICE_NUMBER_NUMERATOR_NAME, mockCountry, mockProperty, mockSeller);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.FIRST_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(Numerator.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByNameAndCountryAndObjectAndObject2");
            assertThat(finderInteraction.getArgumentsByParameterName().get("name")).isEqualTo((Object) INVOICE_NUMBER_NUMERATOR_NAME);
            assertThat(finderInteraction.getArgumentsByParameterName().get("country")).isSameAs(mockCountry);
            assertThat(finderInteraction.getArgumentsByParameterName().get("objectType")).isEqualTo("PROP");
            assertThat(finderInteraction.getArgumentsByParameterName().get("objectIdentifier")).isEqualTo("123");
            assertThat(finderInteraction.getArgumentsByParameterName().get("objectType2")).isEqualTo("ORG");
            assertThat(finderInteraction.getArgumentsByParameterName().get("objectIdentifier2")).isEqualTo("456");

            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(6);
        }
    }

    public static class AllNumerators extends NumeratorRepository_find_Test {

        @Test
        public void allNumerators() {

            numeratorRepository.allNumerators();

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_INSTANCES);
        }
    }

}
