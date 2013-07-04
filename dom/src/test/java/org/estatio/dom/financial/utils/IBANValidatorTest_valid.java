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
package org.estatio.dom.financial.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class IBANValidatorTest_valid {

    private IBANValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new IBANValidator();
    }

    @Test
    public void happyCase() {
        assertThat(validator.valid("NL26INGB0680433600"), is(true));
        assertThat(validator.valid("NL07INGB0697694704"), is(true));
        assertThat(validator.valid("IT69N0347501601000051986922"), is(true));
        assertThat(validator.valid("IT93Q0347501601000051768165"), is(true));
    }
    
    @Test
    public void sadCase() {
        assertThat(validator.valid("NLXXINGB0680433600"), is(false));
    }
}
