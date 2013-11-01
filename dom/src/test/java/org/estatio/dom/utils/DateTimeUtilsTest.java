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
package org.estatio.dom.utils;

import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Test;

public class DateTimeUtilsTest {

    @Test
    public void testParse() {
        Period period = JodaPeriodUtils.asPeriod("6y6m3d");
        LocalDate startDate = new LocalDate(2000, 1, 1);
        Assert.assertThat(startDate.plus(period), Is.is(new LocalDate(2006, 7, 4)));
    }

    @Test
    public void testParseWithSpaces() {
        Period period = JodaPeriodUtils.asPeriod("  6Y  6m  ");
        LocalDate startDate = new LocalDate(2000, 1, 1);
        Assert.assertThat(startDate.plus(period), Is.is(new LocalDate(2006, 7, 1)));
    }

    @Test
    public void testParseMalformed() {
        Period period = JodaPeriodUtils.asPeriod("6x6y");
        LocalDate startDate = new LocalDate(2000, 1, 1);
        Assert.assertThat(startDate.plus(period), Is.is(new LocalDate(2000, 1, 1)));
    }

    @Test
    public void testPeriodtoString() throws Exception {
        Period period = new Period(new LocalDate(2000, 1, 1), new LocalDate(2006, 7, 2));
        Assert.assertThat(JodaPeriodUtils.asString(period), Is.is("6 years, 6 months & 1 day"));
    }

    @Test
    public void testPeriodtoSimpleString() throws Exception {
        Period period = new Period(new LocalDate(2000, 1, 1), new LocalDate(2006, 7, 2));
        Assert.assertThat(JodaPeriodUtils.asSimpleString(period), Is.is("6y6m1d"));
    }

    
}
