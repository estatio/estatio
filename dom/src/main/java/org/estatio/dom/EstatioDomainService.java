package org.estatio.dom;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.core.commons.lang.StringUtils;

import org.estatio.services.clock.ClockService;

public abstract class EstatioDomainService<T> extends AbstractFactoryAndRepository {

    private final Class<? extends EstatioDomainService<T>> serviceType;
    private final Class<T> entityType;

    protected EstatioDomainService(Class<? extends EstatioDomainService<T>> serviceType, Class<T> objectType) {
        this.serviceType = serviceType;
        this.entityType = objectType;
    }
    
    @Override
    public String getId() {
        // eg "agreementRoles";
        return StringUtils.camelLowerFirst(serviceType.getSimpleName());
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
    
    protected List<T> allMatches(final String queryName, final Object... paramArgs) {
        return allMatches(newQueryDefault(queryName, paramArgs));
    }

    protected List<T> allInstances() {
        return allInstances(getEntityType());
    }

    // //////////////////////////////////////

    private ClockService clockService;
    protected ClockService getClockService() {
        return clockService;
    }
    public void injectClockService(ClockService clockService) {
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
    public void injectEventBusService(EventBusService eventBusService) {
        this.eventBusService = eventBusService;
        eventBusService.register(this);
    }

}
