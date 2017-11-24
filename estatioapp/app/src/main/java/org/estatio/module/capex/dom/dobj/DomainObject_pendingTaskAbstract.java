package org.estatio.module.capex.dom.dobj;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.estatio.module.capex.dom.state.State;
import org.estatio.module.capex.dom.state.StateTransitionAbstract;
import org.estatio.module.capex.dom.state.StateTransitionRepositoryGeneric;
import org.estatio.module.capex.dom.state.StateTransitionType;
import org.estatio.module.capex.dom.task.Task;

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
                getClass(),
                "prop", domainObject);
    }

    private Task doProp() {
        final ST transition = stateTransitionRepositoryGeneric.findByDomainObjectAndCompleted(
                                                                    domainObject, false, stateTransitionClass);
        if(transition == null) {
            return null;
        }
        return transition.getTask();
    }

    @Inject
    protected StateTransitionRepositoryGeneric stateTransitionRepositoryGeneric;

    @Inject
    QueryResultsCache queryResultsCache;
}
