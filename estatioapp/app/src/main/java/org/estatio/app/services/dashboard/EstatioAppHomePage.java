/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.app.services.dashboard;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.dom.event.Event;
import org.estatio.dom.event.EventRepository;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.dashboard.EstatioAppHomePage"
)
public class EstatioAppHomePage {

    private static final int MONTHS = 3;

    //region > title
    public String title() {
        return "Home Page";
    }
    //endregion

    public List<Lease> getLeasesAboutToExpire() {
        return leaseRepository.findExpireInDateRange(clockService.now(), clockService.now().plusMonths(MONTHS));
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findEventsInDateRange(clockService.now(), clockService.now().plusMonths(MONTHS));
    }

    //region > injected services
    @Inject
    private LeaseRepository leaseRepository;

    @Inject
    private EventRepository eventRepository;

    @Inject
    private ClockService clockService;
    //endregion

}
