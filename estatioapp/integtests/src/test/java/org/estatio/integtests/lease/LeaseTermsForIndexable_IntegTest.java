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
package org.estatio.integtests.lease;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.index.dom.Index;
import org.estatio.module.index.dom.IndexRepository;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermForIndexableRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.index.IndexRefData;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LeaseTermsForIndexable_IntegTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
            }
        });
    }

    @Inject
    IndexRepository indexes;

    @Inject
    LeaseTermForIndexableRepository leaseTerms;

    @Before
    public void setUp() throws Exception {
    }

    public static class FindByIndexAndDate extends LeaseTermsForIndexable_IntegTest {

        Index index;

        @Before
        public void setUp() throws Exception {
            index = indexes.findByReference(IndexRefData.IT_REF);
        }

        @Test
        public void findByIndexAndDate() throws Exception {
            // Given
            List<LeaseTermForIndexable> results = leaseTerms.findByIndexAndDate(index, new LocalDate(2011, 1, 1));
            assertThat(results.size(), is(1));
            // When
            results.get(0).verifyUntil(new LocalDate(2014, 1, 1));
            // Then
            assertThat(leaseTerms.findByIndexAndDate(index, new LocalDate(2011, 1, 1)).size(), is(2));
        }

    }

}