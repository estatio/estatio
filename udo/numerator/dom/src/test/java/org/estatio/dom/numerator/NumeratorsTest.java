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
package org.estatio.dom.numerator;

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

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;

import static org.assertj.core.api.Assertions.assertThat;

public class NumeratorsTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    FinderInteraction finderInteraction;

    NumeratorRepository numeratorRepository;

    @Mock
    BookmarkService mockBookmarkService;

    @Ignoring
    @Mock
    Property mockProperty;

    Bookmark propertyBookmark;

    ApplicationTenancy applicationTenancy;

    @Before
    public void setup() {

        propertyBookmark = new Bookmark("PROP", "123");

        applicationTenancy = new ApplicationTenancy();
        applicationTenancy.setPath("/");

        context.checking(new Expectations() {
            {
                allowing(mockBookmarkService).bookmarkFor(mockProperty);
                will(returnValue(propertyBookmark));
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

    public static class FindScopedNumerator extends NumeratorsTest {

        public static final String INVOICE_NUMBER_NUMERATOR_NAME = "Invoice number";

        @Test
        public void findNumeratorByType() {

            numeratorRepository.findScopedNumeratorIncludeWildCardMatching(INVOICE_NUMBER_NUMERATOR_NAME, mockProperty, applicationTenancy);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.FIRST_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(Numerator.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByNameAndObjectTypeAndApplicationTenancyPath");
            assertThat(finderInteraction.getArgumentsByParameterName().get("name")).isEqualTo((Object) INVOICE_NUMBER_NUMERATOR_NAME);
            assertThat(finderInteraction.getArgumentsByParameterName().get("objectType")).isEqualTo((Object) "PROP");

            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(3);
        }
    }

    public static class AllNumerators extends NumeratorsTest {

        @Test
        public void allNumerators() {

            numeratorRepository.allNumerators();

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_INSTANCES);
        }
    }

}
