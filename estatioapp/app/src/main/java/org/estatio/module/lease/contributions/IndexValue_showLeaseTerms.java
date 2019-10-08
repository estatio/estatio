/*
 *
 *  Copyright 2015 Eurocommercial Properties NV
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
package org.estatio.module.lease.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.*;

import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.index.dom.IndexValue;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermForIndexableRepository;

@Mixin(method="act")
public class IndexValue_showLeaseTerms extends UdoDomainService<IndexValue_showLeaseTerms> {

    private final IndexValue indexValue;

    public IndexValue_showLeaseTerms(IndexValue indexValue) {
        super(IndexValue_showLeaseTerms.class);
        this.indexValue = indexValue;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public List<LeaseTermForIndexable> act() {
        return terms.findByIndexAndDate(indexValue.getIndexBase().getIndex(), indexValue.getStartDate());
    }

    @Inject
    LeaseTermForIndexableRepository terms;
}
