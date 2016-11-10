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

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class IndexTest {

    LocalDate baseDate;
    LocalDate nextDate;

    Index index;
    IndexBase ib1990;
    IndexBase ib2000;
    IndexBase ib2010;
    IndexValue iv1;
    IndexValue iv2;

    @Mock
    IndexValueRepository mockIndexValueRepository;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {
        baseDate = new LocalDate(2001, 1, 1);
        nextDate = new LocalDate(2011, 1, 1);
        index = new Index();
        index.indexValueRepository = mockIndexValueRepository;

        ib1990 = new IndexBase();
        ib1990.setStartDate(new LocalDate(1990, 1, 1));

        ib2000 = new IndexBase();
        ib2000.setPrevious(ib1990);
        ib2000.setFactor(BigDecimal.valueOf(1.345));
        ib2000.setStartDate(new LocalDate(2000, 1, 1));

        ib2010 = new IndexBase();
        ib2010.setPrevious(ib2000);
        ib2010.setFactor(BigDecimal.valueOf(1.234));
        ib2010.setStartDate(new LocalDate(2010, 1, 1));

        iv1 = new IndexValue();
        iv1.setIndexBase(ib2000);
        iv1.setStartDate(baseDate);
        iv1.setValue(BigDecimal.valueOf(122.2));

        iv2 = new IndexValue();
        iv2.setIndexBase(ib2010);
        iv2.setStartDate(nextDate);
        iv2.setValue(BigDecimal.valueOf(111.1));

        BigDecimal result = BigDecimal.valueOf(111.1).divide(BigDecimal.valueOf(122.2), 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1.234));
    }

    public static class FactorForDate extends IndexTest {

        @Test
        public void happyCase() {
            assertEquals(ib2010.factorForDate(baseDate), new BigDecimal("1.234"));
            assertEquals(new BigDecimal("1.659730"), ib2010.factorForDate(new LocalDate(1999, 1, 1)));
        }

    }

    public static class GetIndexValueFactorForDate extends IndexTest {

        @Test
        public void happyCase() {
            context.checking(new Expectations() {
                {
                    oneOf(mockIndexValueRepository).findByIndexAndStartDate(with(equal(index)), with(equal(new LocalDate(2001, 1, 1))));
                    will(returnValue(iv1));
                    oneOf(mockIndexValueRepository).findByIndexAndStartDate(with(equal(index)), with(equal(new LocalDate(2011, 1, 1))));
                    will(returnValue(iv2));
                }
            });
            assertEquals(BigDecimal.valueOf(122.2), index.getIndexValueForDate(baseDate));
            assertEquals(BigDecimal.valueOf(111.1), index.getIndexValueForDate(nextDate));
        }

        @Test
        public void withNulls() {
            assertNull(index.getIndexValueForDate(null));
        }

    }

    public static class GetRebaseFactorForDates extends IndexTest {

        @Test
        public void happyCase() {
            context.checking(new Expectations() {
                {
                    oneOf(mockIndexValueRepository).findByIndexAndStartDate(with(equal(index)), with(equal(new LocalDate(2011, 1, 1))));
                    will(returnValue(iv2));
                }
            });
            assertEquals(BigDecimal.valueOf(1.234), index.getRebaseFactorForDates(baseDate, nextDate));
        }

        @Test
        public void withNull() {
            context.checking(new Expectations() {
                {
                    oneOf(mockIndexValueRepository).findByIndexAndStartDate(with(equal(index)), with(equal(new LocalDate(2011, 1, 1))));
                    will(returnValue(null));
                }
            });
            assertEquals(null, index.getRebaseFactorForDates(baseDate, nextDate));
        }
    }

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new Index());
        }
    }

}
