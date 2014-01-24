/*
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
package org.estatio.services.clock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ClockServiceTest_beginningOfMonth {

    private ClockService clockService;
    protected LocalDate now;
    private LocalDate expected;

    @Parameters
    public static Collection<Object[]> data() {
      return Arrays.asList(
              new Object[][] { 
                      { new LocalDate(2013,4,15), new LocalDate(2013,4,1)}, 
                      { new LocalDate(2013,4,1),  new LocalDate(2013,4,1)}, 
                      { new LocalDate(2013,4,30),  new LocalDate(2013,4,1)}, 
              });
    }
    
    public ClockServiceTest_beginningOfMonth(LocalDate date, LocalDate expected) {
        this.now = date;
        this.expected = expected;
    }
    
    @Before
    public void setUp() throws Exception {
        clockService = new ClockService() {
            @Override
            public LocalDate now() {
                return now;
            }
        };
    }
    
    @Test
    public void test() throws Exception {
        assertThat(clockService.beginningOfMonth(), is(expected));
    }


}
