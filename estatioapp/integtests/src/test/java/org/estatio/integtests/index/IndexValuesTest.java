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
package org.estatio.integtests.index;

import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexValue;
import org.estatio.dom.index.IndexValues;
import org.estatio.dom.index.Indices;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.EstatioOperationalTeardownFixture;
import org.estatio.fixture.EstatioRefDataTeardownFixture;
import org.estatio.fixture.index.IndexRefData;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IndexValuesTest extends EstatioIntegrationTest {

    @Inject
    Indices indices;
    @Inject
    IndexValues indexValues;


    public static class FindIndexValueByIndexAndStartDate extends IndexValuesTest {

        @Before
        public void setupData() {
            runFixtureScript(new EstatioBaseLineFixture());
        }

        @Test
        public void happyCase() throws Exception {
            // given
            Index index = indices.findIndex(IndexRefData.IT_REF);
            // when, then
            assertThat(indexValues.findIndexValueByIndexAndStartDate(index, VT.ld(2013, 1, 1)).getValue(), is(VT.bd("106.7000")));
            assertThat(indexValues.findIndexValueByIndexAndStartDate(index, VT.ld(2013, 10, 1)).getValue(), is(VT.bd("107.1000")));
        }

    }

    public static class FindLastByIndex extends IndexValuesTest {

        @Before
        public void setupData() {
            runFixtureScript(
                    // tearing down because of a failure which suggests that one of the other tests is creating new index values...
                    // (not sure which one though :-( )
                    new EstatioOperationalTeardownFixture(),
                    new EstatioRefDataTeardownFixture(),
                    new EstatioBaseLineFixture()
            );
        }

        @Test
        public void happyCase() throws Exception {
            Index index = indices.findIndex(IndexRefData.IT_REF);
            final IndexValue indexValue = indexValues.findLastByIndex(index);
            assertThat(indexValue.getStartDate(), is(VT.ld(2013, 12, 01)));
        }

    }

}