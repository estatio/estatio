package org.estatio.dom;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.util.ObjectContracts;

import org.estatio.dom.agreement.Agreement;
import org.estatio.services.clock.ClockService;

public abstract class EstatioDomainObject<T extends EstatioDomainObject<T>> extends AbstractDomainObject implements Comparable<T> {

    private static ObjectContracts estatioObjectContracts = 
            new ObjectContracts()
                .with(WithCodeGetter.ToString.evaluator())
                .with(WithDescriptionGetter.ToString.evaluator())
                .with(WithNameGetter.ToString.evaluator())
                .with(WithReferenceGetter.ToString.evaluator())
                .with(WithTitleGetter.ToString.evaluator());

    private final String keyProperties;

    public EstatioDomainObject(String keyProperties) {
        this.keyProperties = keyProperties;
    }

    protected String keyProperties() {
        return keyProperties;
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
     * Unlike domain services, domain objects are NOT automatically registered
     * with the {@link EventBusService}; Isis makes no guarantees as to whether
     * a subscribing domain object is in memory or not to receive the event.
     */
    public void injectEventBusService(EventBusService eventBusService) {
        this.eventBusService = eventBusService;
    }
    
    // //////////////////////////////////////

    @Override
    public String toString() {
        return estatioObjectContracts.toStringOf(this, keyProperties());
    }

    @Override
    public int compareTo(T other) {
        return ObjectContracts.compare(this, other, keyProperties);
    }

}
