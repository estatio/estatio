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

import org.datanucleus.store.rdbms.identifier.IdentifierCase;
import org.estatio.dnplugins.EstatioIdentifierFactory;
import org.junit.Test;

public class EstatioIdentifierFactoryTest_generateIdentifierNameForJavaName {


    @Test
    public void whenContainsPeriod() {
        final String actual = EstatioIdentifierFactory.generateIdentifierNameForJavaName("nextTerm.LEASETERM_ID", IdentifierCase.UPPER_CASE, "_");
        //previously was: 
        // assertThat(actual, is("NEXTTERM_LEASETERM_ID"));
        assertThat(actual, is("NEXTTERM_ID"));
    }

    @Test
    public void whenDoesNotContainPeriod() {
        final String actual = EstatioIdentifierFactory.generateIdentifierNameForJavaName("LEASETERM_ID", IdentifierCase.UPPER_CASE, "_");
        assertThat(actual, is("LEASETERM_ID"));
    }

}
