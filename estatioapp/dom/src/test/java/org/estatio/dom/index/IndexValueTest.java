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
import org.junit.Test;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

public class IndexValueTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(IndexBase.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new IndexValue());
        }
    }

    public static class CompareTo extends ComparableContractTest_compareTo<IndexValue> {

        private IndexBase ib1;
        private IndexBase ib2;

        @Before
        public void setUp() throws Exception {
            ib1 = new IndexBase();
            ib2 = new IndexBase();

            ib1.setStartDate(new LocalDate(2012,4,1));
            ib2.setStartDate(new LocalDate(2012,3,1));
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<IndexValue>> orderedTuples() {
            return listOf(
                    listOf(
                            // natural order
                            newIndexValue(null, null),
                            newIndexValue(ib1, null),
                            newIndexValue(ib1, null),
                            newIndexValue(ib2, null)
                    ),
                    listOf(
                            // natural order
                            newIndexValue(ib1, null),
                            newIndexValue(ib1, new LocalDate(2012,4,4)),
                            newIndexValue(ib1, new LocalDate(2012,4,4)),
                            newIndexValue(ib1, new LocalDate(2012,4,3))
                    )
            );
        }

        private IndexValue newIndexValue(
                IndexBase indexBase,
                LocalDate startDate) {
            final IndexValue ib = new IndexValue();
            ib.setIndexBase(indexBase);
            ib.setStartDate(startDate);
            return ib;
        }

    }

}