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
package org.estatio.dnplugins;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.estatio.dnplugins.EstatioIdentifierFactory;
import org.junit.Test;

public class EstatioIdentifierFactoryTest_stripOidSuffixIfPresent {


    @Test
    public void whenIs() {
        final String actual = EstatioIdentifierFactory.stripOidSuffixIfPresent("ABC_ID_OID");
        assertThat(actual, is("ABC_ID"));
    }


    @Test
    public void whenIsNot() {
        final String actual = EstatioIdentifierFactory.stripOidSuffixIfPresent("ABC_ID_FOO");
        assertThat(actual, is("ABC_ID_FOO"));
    }

}
