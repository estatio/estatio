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
package org.estatio.module.event.dom;

import java.util.List;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.*;
import org.estatio.module.base.dom.UdoDomainService;

@Mixin(method="coll")
public class EventSource_events extends UdoDomainService<EventSource_events> {

    private final EventSource eventSource;

    public EventSource_events(EventSource eventSource) {
        super(EventSource_events.class);
        this.eventSource = eventSource;
    }

    //region > events (contributed association)
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<Event> coll() {
        return eventRepository.findBySource(eventSource);
    }
    //endregion

    @Inject
    private EventRepository eventRepository;
}
