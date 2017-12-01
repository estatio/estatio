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

import org.estatio.module.index.dom.Index;
import org.estatio.module.index.dom.IndexRepository;
import org.estatio.module.index.fixtures.enums.Index_enum;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IndexRepository_IntegTest extends IndexModuleIntegTestAbstract {

    public static class FindIndex extends IndexRepository_IntegTest {

        @Inject
        private IndexRepository indexRepository;

        @Test
        public void whenExists() throws Exception {
            final Index_enum index_d = Index_enum.IStatFoi;
            final Index index = indexRepository.findByReference(index_d.getReference());
            assertThat(index.getReference(), is(index_d.getReference()));
        }

    }
}