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
package org.estatio.dom.numerator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

public class NumeratorTest_title {

    private Numerator numerator;
    
    
    @Before
    public void setUp() throws Exception {
        numerator = new Numerator();
        numerator.setFormat("XXX-%05d");
        
    }

    
    @Test
    public void title_whenScoped() {
        numerator.setObjectType("CUS");
        numerator.setObjectIdentifier("123");
        numerator.setLastIncrement(BigInteger.ONE);
        
        assertThat(numerator.isScoped(), is(true));
        
        assertEquals("XXX-00001", numerator.title());
        assertEquals("XXX-00001", numerator.format(BigInteger.ONE));
    }
    
    @Test
    public void title_whenGlobal() {
        numerator.setName("Bananas");
        assertThat(numerator.isScoped(), is(false));
        
        assertEquals("Bananas", numerator.title());
    }

    @Test
    public void format() {
        assertEquals("XXX-00001", numerator.format(BigInteger.ONE));
    }

}
