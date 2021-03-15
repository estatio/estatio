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
package org.estatio.module.base.dom;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.isis.applib.AbstractService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.memento.MementoService;

import org.incode.module.base.services.calendar.CalendarService;

public abstract class UdoDomainService<T> extends AbstractService {

    private final Class<? extends UdoDomainService<T>> serviceType;

    protected UdoDomainService(final Class<? extends UdoDomainService<T>> serviceType) {
        this.serviceType = serviceType;
    }

    public String iconName() {
        // eg "AgreementRole";
        return serviceType.getSimpleName();
    }

    // //////////////////////////////////////

    protected Class<? extends UdoDomainService<T>> getServiceType() {
        return serviceType;
    }

    // //////////////////////////////////////

    /**
     * Domain services ARE automatically registered with the {@link EventBusService};
     * Isis guarantees that there will be an instance of each domain service in memory when events are {@link EventBusService#post(Object) post}ed.
     */
    @Programmatic
    @PostConstruct
    public void init(final Map<String, String> properties) {
        getEventBusService().register(this);
    }

    @Programmatic
    @PreDestroy
    public void shutdown() {
        getEventBusService().unregister(this);
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private ClockService clockService;

    protected ClockService getClockService() {
        return clockService;
    }

    @javax.inject.Inject
    private CalendarService calendarService;

    protected CalendarService getCalendarService() {
        return calendarService;
    }

    @javax.inject.Inject
    private EventBusService eventBusService;

    protected EventBusService getEventBusService() {
        return eventBusService;
    }

    @javax.inject.Inject
    private MementoService mementoService;

    protected MementoService getMementoService() {
        return mementoService;
    }

    @Inject public BookmarkService bookmarkService;

    protected BookmarkService getBookmarkService() {
        return bookmarkService;
    }

}
