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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ValueUtilsTest_coalesce {

    @Test
    public void firstNonNull() throws Exception {
        assertThat(ValueUtils.coalesce("foo", "bar"), is("foo"));
    }
    
    @Test
    public void secondNonNull() throws Exception {
        assertThat(ValueUtils.coalesce(null, "bar"), is("bar"));
    }
    
    @Test
    public void bothNull() throws Exception {
        assertThat(ValueUtils.<String>coalesce(null, null), is((String)null));
    }
}
