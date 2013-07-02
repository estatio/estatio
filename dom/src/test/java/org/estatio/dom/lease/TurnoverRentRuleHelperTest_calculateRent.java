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
package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

public class TurnoverRentRuleHelperTest_calculateRent {

    @Test
    public void testAll() throws Exception {
        test(null, 1000000d, 0.0);
        test("0", 1000000d, 0.0);
        test("5", 1000000d, 50000d);
        test("500000;5;750000;6;7", 500000d, 25000d);
        test("500000;5;750000;6;7", 600000d, 31000d);
        test("500000;5;750000;6;7", 1000000d, 57500d);
        test("", 1000000d, 0d);
        test("500000;5;750000;6;7", 0.0, 0.0);
        test("500000;5;750000;6;7", null, 0.0);

    }

    protected void test(String rule, Double in, Double expected) {
        final BigDecimal input = in == null ? null : BigDecimal.valueOf(in);
        final BigDecimal result = new TurnoverRentRuleHelper(rule).calculateRent(input);
        assertThat(result, is(BigDecimal.valueOf(expected).setScale(2)));
    }

}
