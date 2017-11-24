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
package org.estatio.module.index.integtests;

import javax.inject.Inject;

import org.junit.Test;

import org.incode.module.base.integtests.VT;

import org.estatio.module.index.dom.Index;
import org.estatio.module.index.dom.IndexRepository;
import org.estatio.module.index.dom.IndexValue;
import org.estatio.module.index.dom.IndexValueRepository;
import org.estatio.module.index.fixtures.IndexRefData;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IndexValueRepository_IntegTest extends IndexModuleIntegTestAbstract {

    @Inject
    IndexRepository indexRepository;
    @Inject
    IndexValueRepository indexValueRepository;

    public static class FindIndexValueByIndexAndStartDate extends IndexValueRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            Index index = indexRepository.findByReference(IndexRefData.IT_REF);
            // when, then
            assertThat(indexValueRepository.findByIndexAndStartDate(index, VT.ld(2013, 1, 1)).getValue(), is(VT.bd("106.7000")));
            assertThat(indexValueRepository.findByIndexAndStartDate(index, VT.ld(2013, 10, 1)).getValue(), is(VT.bd("107.1000")));
        }

    }

    public static class FindLastByIndex extends IndexValueRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            Index index = indexRepository.findByReference(IndexRefData.IT_REF);
            final IndexValue indexValue = indexValueRepository.findLastByIndex(index);
            assertThat(indexValue.getStartDate(), is(VT.ld(2013, 12, 01)));
        }

    }

}