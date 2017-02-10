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
package org.estatio.index.dom.indexation;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainService;
import org.estatio.index.dom.Index;
import org.estatio.index.dom.Indexable;

@DomainService(menuOrder = "60")
public class IndexationService extends UdoDomainService<IndexationService> {


    public IndexationService() {
        super(IndexationService.class);
    }

    @Programmatic
    public void indexate(final Indexable input) {
        final IndexationResult indexationResult;
        indexationResult = indexateToResult(input);
        indexationResult.apply(input);
    }

    private IndexationResult indexateToResult(final Indexable input) {
        if (input.getIndex() == null ||
                input.getBaseIndexStartDate() == null ||
                input.getNextIndexStartDate() == null ||
                input.getBaseIndexStartDate().compareTo(input.getNextIndexStartDate()) > 0) {
            return IndexationResult.NULL;
        }
        final Index index = input.getIndex();
        index.initialize(input);
        return IndexationCalculationMethod.calculate(input);
    }

}
