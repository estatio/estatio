package org.estatio.capex.dom.task;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransitionAbstract;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.state.StateTransitionType;

/**
 * Subclasses should be annotated using: @Mixin(method = "prop")
 */
public abstract class DomainObject_reasonGuardNotSatisfiedAbstract<
        DO,
        ST extends StateTransitionAbstract<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    protected final DO domainObject;
    private final Class<ST> stateTransitionClass;

    public DomainObject_reasonGuardNotSatisfiedAbstract(final DO domainObject, final Class<ST> stateTransitionClass) {
        this.domainObject = domainObject;
        this.stateTransitionClass = stateTransitionClass;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public String prop() {
        return queryResultsCache.execute(
                this::doProp,
                DomainObject_reasonGuardNotSatisfiedAbstract.class,
                "prop", domainObject);
    }

    private String doProp() {
        ST pendingTransitionIfAny = stateTransitionService.pendingTransitionOf(domainObject, stateTransitionClass);
        return pendingTransitionIfAny != null
                ? pendingTransitionIfAny.getTransitionType().reasonGuardNotSatisified(domainObject, serviceRegistry)
                : null;
    }

    public boolean hideProp() {
        return prop() == null;
    }

    @Inject
    ServiceRegistry2 serviceRegistry;

    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    QueryResultsCache queryResultsCache;
}
