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
import org.estatio.module.index.dom.IndexBase;
import org.estatio.module.index.dom.IndexBaseRepository;
import org.estatio.module.index.dom.IndexRepository;
import org.estatio.module.index.fixtures.IndexRefData;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IndexBaseRepository_IntegTest extends IndexModuleIntegTestAbstract {

    public static class FindByIndexAndDate extends IndexBaseRepository_IntegTest {

        @Inject
        private IndexRepository indexRepository;
        @Inject
        private IndexBaseRepository indexBaseRepository;

        @Test
        public void happyCase() throws Exception {
            // given
            Index index = indexRepository.findByReference(IndexRefData.IT_REF);
            // when
            final IndexBase indexBase = indexBaseRepository.findByIndexAndActiveOnDate(index, VT.ld(2013, 1, 1));
            // then
            assertThat(indexBase.getStartDate(), is(VT.ld(2011, 1, 1)));
        }

    }
}