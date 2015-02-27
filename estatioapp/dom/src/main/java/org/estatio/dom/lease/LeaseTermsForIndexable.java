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
package org.estatio.dom.lease;

import java.util.List;

import com.google.common.eventbus.Subscribe;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexValue;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = LeaseTermForIndexable.class)
public class LeaseTermsForIndexable extends UdoDomainRepositoryAndFactory<LeaseTermForIndexable> {

    public LeaseTermsForIndexable() {
        super(LeaseTermsForIndexable.class, LeaseTermForIndexable.class);
    }

    // //////////////////////////////////////

    public List<LeaseTermForIndexable> findByIndexAndDate(final Index index, final LocalDate date) {
        return allMatches("findByIndexAndDate", "index", index, "date", date);
    }

    // //////////////////////////////////////

    @Programmatic
    @Subscribe
    public void on(final IndexValue.UpdateEvent ev) {
        for (LeaseTermForIndexable term : findByIndexAndDate(ev.getSource().getIndexBase().getIndex(), ev.getSource().getStartDate())) {
            term.verify();
        }
    }

}
