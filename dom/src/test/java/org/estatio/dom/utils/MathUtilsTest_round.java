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
package org.estatio.dom.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

public class MathUtilsTest_round {

    @Test
    public void roundUp() throws Exception {
        assertThat(new BigDecimal("4.6"), is(MathUtils.round(new BigDecimal("4.55"), 1)));
    }

    @Test
    public void roundDown() throws Exception {
        assertThat(new BigDecimal("4.5"), is(MathUtils.round(new BigDecimal("4.54"), 1)));
    }

    @Test
    public void noRounding() throws Exception {
        assertThat(new BigDecimal("4.54"), is(MathUtils.round(new BigDecimal("4.54"), 2)));
    }

}
