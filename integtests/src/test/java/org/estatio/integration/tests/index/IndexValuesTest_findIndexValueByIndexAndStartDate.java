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
package org.estatio.integration.tests.index;

import java.math.BigDecimal;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexValues;
import org.estatio.dom.index.Indices;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IndexValuesTest_findIndexValueByIndexAndStartDate extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new EstatioBaseLineFixture());
    }

    private Indices indices;
    private IndexValues indexValues;

    @Before
    public void setup() {
        indices = service(Indices.class);
        indexValues = service(IndexValues.class);
    }

    @Test
    public void forValidIndexAndStartDate() throws Exception {
        // given
        Index index = indices.findIndex("ISTAT-FOI");
        // when, then
        assertThat(indexValues.findIndexValueByIndexAndStartDate(index, new LocalDate(2013, 1, 1)).getValue(), is(new BigDecimal("106.7000")));
        assertThat(indexValues.findIndexValueByIndexAndStartDate(index, new LocalDate(2013, 10, 1)).getValue(), is(new BigDecimal("107.1000")));
    }

}
