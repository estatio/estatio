/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexBase;
import org.estatio.dom.index.IndexBases;
import org.estatio.dom.index.IndexValue;
import org.estatio.dom.index.IndexValues;
import org.estatio.dom.index.Indices;
import org.estatio.fixture.EstatioRefDataObjectsFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;

public class IndicesTest_finders extends EstatioIntegrationTest {

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioRefDataObjectsFixture());
    }

    private Indices indices;
    private IndexValues indexValues;
    private IndexBases indexBases;

    
    @Before
    public void setup() {
        indices = service(Indices.class);
        indexBases = service(IndexBases.class);
        indexValues = service(IndexValues.class);
    }

    @Test
    public void findIndex() throws Exception {
        final Index index = indices.findIndex("ISTAT-FOI");
        assertThat(index.getReference(), is("ISTAT-FOI"));
    }

    @Test
    public void findByIndexAndDate() throws Exception {
        Index index = indices.findIndex("ISTAT-FOI");
        final IndexBase indexBase = indexBases.findByIndexAndDate(index, new LocalDate(2013,1,1));
        assertThat(indexBase.getStartDate(), is(new LocalDate(2011,1,1)));
    }
   
    @Test
    public void findIndexValueByIndexAndStartDate() throws Exception {
        Index index = indices.findIndex("ISTAT-FOI");
        assertThat(indexValues.findIndexValueByIndexAndStartDate(index, new LocalDate(2013,1,1)).getValue(), is(new BigDecimal("106.7000")));
        assertThat(indexValues.findIndexValueByIndexAndStartDate(index, new LocalDate(2013,10,1)).getValue(), is(new BigDecimal("107.1000")));
    }

    @Test
    public void findLastByIndex() throws Exception {
        Index index = indices.findIndex("ISTAT-FOI");
        final IndexValue indexValue = indexValues.findLastByIndex(index);
        assertThat(indexValue.getStartDate(), is(new LocalDate(2013,10,1)));
    }
    
}
