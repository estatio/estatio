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

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.Test;

import org.incode.module.base.integtests.VT;

import org.estatio.module.index.dom.Index;
import org.estatio.module.index.dom.IndexRepository;
import org.estatio.module.index.dom.IndexValue;
import org.estatio.module.index.dom.IndexValueRepository;
import org.estatio.module.index.fixtures.enums.Index_enum;

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
            Index index = Index_enum.IStatFoi.findUsing(serviceRegistry);
            // when, then
            assertThat(indexValueRepository.findByIndexAndStartDate(index, VT.ld(2013, 1, 1)).getValue(), is(VT.bd("106.7000")));
            assertThat(indexValueRepository.findByIndexAndStartDate(index, VT.ld(2013, 10, 1)).getValue(), is(VT.bd("107.1000")));
        }

    }

    public static class FindLastByIndex extends IndexValueRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final List<IndexValue> all = indexValueRepository.all();
            Index index = Index_enum.IStatFoi.findUsing(serviceRegistry);
            final List<IndexValue> indexReverseSortedValues =
                    all.stream()
                        .filter(value -> value.getIndexBase().getIndex() == index)
                        .sorted((o1, o2) -> o2.getStartDate().compareTo(o1.getStartDate()))
                        .collect(Collectors.toList());
            final IndexValue lastIndex = indexReverseSortedValues.get(0);

            // when
            final IndexValue indexValue = indexValueRepository.findLastByIndex(index);

            // then
            assertThat(indexValue, is(lastIndex));
        }

    }

}