/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.dom;

import java.util.List;

import javax.jdo.Query;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.core.commons.lang.StringExtensions;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

import org.estatio.services.clock.ClockService;

public abstract class EstatioDomainService<T> extends AbstractFactoryAndRepository {

    private final Class<? extends EstatioDomainService<T>> serviceType;
    private final Class<T> entityType;

    protected EstatioDomainService(final Class<? extends EstatioDomainService<T>> serviceType, final Class<T> objectType) {
        this.serviceType = serviceType;
        this.entityType = objectType;
    }
    
    @Override
    public String getId() {
        // eg "agreementRoles";
        return StringExtensions.asCamelLowerFirst(serviceType.getSimpleName());
    }

    public String iconName() {
        // eg "AgreementRole";
        return entityType.getSimpleName();
    }

    // //////////////////////////////////////

    protected Class<? extends EstatioDomainService<T>> getServiceType() {
        return serviceType;
    }
    
    protected Class<T> getEntityType() {
        return entityType;
    }
    
    protected QueryDefault<T> newQueryDefault(final String queryName, final Object... paramArgs) {
        return new QueryDefault<T>(getEntityType(), queryName, paramArgs);
    }
    
    // //////////////////////////////////////

    protected T newTransientInstance() {
        return newTransientInstance(getEntityType());
    }
    
    protected T firstMatch(final String queryName, final Object... paramArgs) {
        return firstMatch(newQueryDefault(queryName, paramArgs));
    }
    
    protected T uniqueMatch(final String queryName, final Object... paramArgs) {
        return uniqueMatch(newQueryDefault(queryName, paramArgs));
    }
    
    protected List<T> allMatches(final String queryName, final Object... paramArgs) {
        return allMatches(newQueryDefault(queryName, paramArgs));
    }

    protected List<T> allInstances() {
        return allInstances(getEntityType());
    }

    // //////////////////////////////////////
    
    protected Query newQuery(final String jdoql) {
        return isisJdoSupport.getJdoPersistenceManager().newQuery(jdoql);
    }



    // //////////////////////////////////////

    private ClockService clockService;
    protected ClockService getClockService() {
        return clockService;
    }
    public void injectClockService(final ClockService clockService) {
        this.clockService = clockService;
    }

    /**
     * a default value is used to prevent null pointers for objects 
     * being initialized where the service has not yet been injected into.
     */
    private EventBusService eventBusService = EventBusService.NOOP;
    protected EventBusService getEventBusService() {
        return eventBusService;
    }
    /**
     * Unlike domain objects, domain services ARE automatically registered
     * with the {@link EventBusService}; Isis guarantees that there will be
     * an instance of each domain service in memory when events are {@link EventBusService#post(Object) post}ed.
     */
    public void injectEventBusService(final EventBusService eventBusService) {
        this.eventBusService = eventBusService;
        eventBusService.register(this);
    }

    protected IsisJdoSupport isisJdoSupport;
    public final void injectIsisJdoSupport(final IsisJdoSupport isisJdoSupport) {
        this.isisJdoSupport = isisJdoSupport;
    }

}
