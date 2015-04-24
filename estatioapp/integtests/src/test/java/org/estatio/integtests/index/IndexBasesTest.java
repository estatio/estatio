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
import org.estatio.dom.index.IndexBase;
import org.estatio.dom.index.IndexBases;
import org.estatio.dom.index.Indices;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.index.IndexRefData;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IndexBasesTest extends EstatioIntegrationTest {

    public static class FindByIndexAndDate extends IndexBasesTest {

        @Before
        public void setupData() {
            runFixtureScript(new EstatioBaseLineFixture());
        }

        @Inject
        private Indices indices;
        @Inject
        private IndexBases indexBases;

        @Test
        public void happyCase() throws Exception {
            // given
            Index index = indices.findIndex(IndexRefData.IT_REF);
            // when
            final IndexBase indexBase = indexBases.findByIndexAndDate(index, VT.ld(2013, 1, 1));
            // then
            assertThat(indexBase.getStartDate(), is(VT.ld(2011, 1, 1)));
        }

    }
}