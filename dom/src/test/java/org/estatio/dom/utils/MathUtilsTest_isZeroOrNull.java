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

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MathUtilsTest_isZeroOrNull {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testIsZeroOrNull() {
        Assert.assertTrue(MathUtils.isZeroOrNull(null));
        Assert.assertTrue(MathUtils.isZeroOrNull(BigDecimal.valueOf(0)));
        Assert.assertFalse(MathUtils.isZeroOrNull(BigDecimal.valueOf(100)));
        Assert.assertFalse(MathUtils.isNotZeroOrNull(null));
        Assert.assertFalse(MathUtils.isNotZeroOrNull(BigDecimal.valueOf(0)));
        Assert.assertTrue(MathUtils.isNotZeroOrNull(BigDecimal.valueOf(100)));
    }

}
