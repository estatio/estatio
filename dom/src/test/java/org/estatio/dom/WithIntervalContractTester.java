/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;

import org.estatio.dom.valuetypes.LocalDateInterval;


public class WithIntervalContractTester<T extends WithInterval> {

    private Class<T> cls;

    public WithIntervalContractTester(Class<T> cls) {
        this.cls = cls;
    }

    private final LocalDate startDate = new LocalDate(1900,1,1);
    private final LocalDate endDateInclusive = new LocalDate(2900,1,1);
    
    public void test() {
        System.out.println("WithIntervalContractTester: " + cls.getName());

        WithInterval t = newWithInterval();
        
        t.setStartDate(startDate);
        t.setEndDate(endDateInclusive);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(interval.startDate(), is(startDate));
        assertThat(interval.endDateExcluding(), is(endDateInclusive.plusDays(1)));
    }
    
    private T newWithInterval() {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
