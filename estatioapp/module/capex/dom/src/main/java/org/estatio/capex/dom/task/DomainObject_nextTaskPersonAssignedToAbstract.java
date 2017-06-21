package org.estatio.capex.dom.task;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransitionAbstract;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeService;

/**
 * Subclasses should be annotated using: @Mixin(method = "prop")
 */
public abstract class DomainObject_nextTaskPersonAssignedToAbstract<
        DO,
        ST extends StateTransitionAbstract<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    protected final DO domainObject;
    private final Class<ST> stateTransitionClass;

    public DomainObject_nextTaskPersonAssignedToAbstract(final DO domainObject, final Class<ST> stateTransitionClass) {
        this.domainObject = domainObject;
        this.stateTransitionClass = stateTransitionClass;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public Person prop() {
        return queryResultsCache.execute(
                this::doProp,
                DomainObject_nextTaskPersonAssignedToAbstract.class,
                "prop", domainObject);
    }

    private Person doProp() {
        IPartyRoleType iPartyRoleType = stateTransitionService
                .nextTaskRoleAssignToFor(domainObject, stateTransitionClass);
        if(iPartyRoleType == null) {
            return null;
        }
        return partyRoleTypeService.firstMemberOf(iPartyRoleType, domainObject);
    }

    protected boolean hideProp() {
        return stateTransitionService.nextTaskTransitionTypeFor(domainObject, stateTransitionClass) == null;
    }

    @Inject
    PartyRoleTypeService partyRoleTypeService;


    @Inject
    protected StateTransitionService stateTransitionService;

    @Inject
    QueryResultsCache queryResultsCache;
}
