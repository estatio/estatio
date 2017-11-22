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
package org.estatio.module.lease.dom;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.index.dom.Index;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = LeaseTermForIndexable.class)
public class LeaseTermForIndexableRepository extends UdoDomainRepositoryAndFactory<LeaseTermForIndexable> {

    public LeaseTermForIndexableRepository() {
        super(LeaseTermForIndexableRepository.class, LeaseTermForIndexable.class);
    }

    // //////////////////////////////////////

    public List<LeaseTermForIndexable> findByIndexAndDate(final Index index, final LocalDate date) {
        return allMatches("findByIndexAndDate", "index", index, "date", date);
    }


}
