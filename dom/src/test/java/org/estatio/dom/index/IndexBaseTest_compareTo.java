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
package org.estatio.dom.index;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;


public class IndexBaseTest_compareTo extends ComparableContractTest_compareTo<IndexBase> {

    private Index ib1;
    private Index ib2;
    
    @Before
    public void setUp() throws Exception {
        ib1 = new Index();
        ib2 = new Index();
        
        ib1.setReference("A");
        ib2.setReference("B");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<IndexBase>> orderedTuples() {
        return listOf(
                listOf(
                        // reverse order
                        newIndexBase(null, null),
                        newIndexBase(ib1, null),
                        newIndexBase(ib1, null),
                        newIndexBase(ib2, null)
                        ),
                listOf(
                        // reverse order
                        newIndexBase(ib1, null),
                        newIndexBase(ib1, new LocalDate(2012,4,3)),
                        newIndexBase(ib1, new LocalDate(2012,4,3)),
                        newIndexBase(ib1, new LocalDate(2012,3,1))
                        )
                );
    }

    private IndexBase newIndexBase(
            Index index, 
            LocalDate startDate) {
        final IndexBase ib = new IndexBase();
        ib.setIndex(index);
        ib.setStartDate(startDate);
        return ib;
    }

}
