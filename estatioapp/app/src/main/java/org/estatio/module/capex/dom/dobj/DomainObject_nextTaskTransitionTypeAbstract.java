package org.estatio.module.capex.dom.dobj;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.util.Enums;

import org.estatio.module.capex.dom.state.State;
import org.estatio.module.capex.dom.state.StateTransitionAbstract;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.state.StateTransitionType;

/**
 * Subclasses should be annotated using: @Mixin(method = "prop")
 */
public abstract class DomainObject_nextTaskTransitionTypeAbstract<
        DO,
        ST extends StateTransitionAbstract<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    protected final DO domainObject;
    private final Class<ST> stateTransitionClass;

    public DomainObject_nextTaskTransitionTypeAbstract(final DO domainObject, final Class<ST> stateTransitionClass) {
        this.domainObject = domainObject;
        this.stateTransitionClass = stateTransitionClass;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public String prop() {
        return queryResultsCache.execute(
                this::doProp,
                getClass(),
                "prop", domainObject);
    }

    private String doProp() {
        STT stt = stateTransitionService.nextTaskTransitionTypeFor(domainObject, stateTransitionClass);
        return stt != null ? Enums.getFriendlyNameOf((Enum<?>) stt) : null;
    }

    protected boolean hideProp() {
        return stateTransitionService.nextTaskTransitionTypeFor(domainObject, stateTransitionClass) == null;
    }

    @Inject
    protected StateTransitionService stateTransitionService;

    @Inject
    QueryResultsCache queryResultsCache;
}
