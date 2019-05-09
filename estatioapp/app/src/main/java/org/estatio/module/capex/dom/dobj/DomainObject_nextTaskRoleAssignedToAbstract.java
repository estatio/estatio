package org.estatio.module.capex.dom.dobj;

import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Joiner;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.estatio.module.capex.dom.state.State;
import org.estatio.module.capex.dom.state.StateTransitionAbstract;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.state.StateTransitionType;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;

/**
 * Subclasses should be annotated using: @Mixin(method = "prop")
 */
public abstract class DomainObject_nextTaskRoleAssignedToAbstract<
        DO,
        ST extends StateTransitionAbstract<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    protected final DO domainObject;
    private final Class<ST> stateTransitionClass;

    public DomainObject_nextTaskRoleAssignedToAbstract(final DO domainObject, final Class<ST> stateTransitionClass) {
        this.domainObject = domainObject;
        this.stateTransitionClass = stateTransitionClass;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public Object prop() {
        return queryResultsCache.execute(
                this::doProp,
                getClass(),
                "prop", domainObject);
    }

    private Object doProp() {
        List<IPartyRoleType> iPartyRoleTypes = stateTransitionService
                .nextTaskRoleAssignToFor(domainObject, stateTransitionClass);
        if(iPartyRoleTypes == null) {
            return null;
        }
        switch (iPartyRoleTypes.size()){
            case 0:
                return null;
            case 1:
                return iPartyRoleTypes.get(0).findUsing(partyRoleTypeRepository);
            default:
                return Joiner.on(",").join(iPartyRoleTypes);
        }
    }

    protected boolean hideProp() {
        return stateTransitionService.nextTaskTransitionTypeFor(domainObject, stateTransitionClass) == null;
    }

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    QueryResultsCache queryResultsCache;
}
