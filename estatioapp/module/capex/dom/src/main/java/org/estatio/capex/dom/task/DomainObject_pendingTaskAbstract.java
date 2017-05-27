package org.estatio.capex.dom.task;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransitionAbstract;
import org.estatio.capex.dom.state.StateTransitionRepositoryGeneric;
import org.estatio.capex.dom.state.StateTransitionType;

/**
 * Subclasses should be annotated using: @Mixin(method = "prop")
 */
public abstract class DomainObject_pendingTaskAbstract<
        DO,
        ST extends StateTransitionAbstract<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    protected final DO domainObject;
    private final Class<ST> stateTransitionClass;

    public DomainObject_pendingTaskAbstract(final DO domainObject, final Class<ST> stateTransitionClass) {
        this.domainObject = domainObject;
        this.stateTransitionClass = stateTransitionClass;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public Task prop() {
        return queryResultsCache.execute(
                this::doProp,
                DomainObject_pendingTaskAbstract.class,
                "prop", domainObject);
    }

    private Task doProp() {
        final ST transition = stateTransitionRepositoryGeneric.findFirstByDomainObject(domainObject, stateTransitionClass);
        if(transition == null || transition.isCompleted()) {
            return null;
        }
        return transition.getTask();
    }

    @Inject
    protected StateTransitionRepositoryGeneric stateTransitionRepositoryGeneric;

    @Inject
    QueryResultsCache queryResultsCache;
}
