package org.estatio.capex.dom.invoice.approval.triggers;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeService;

@Mixin(method = "act")
public class IncomingInvoice_complete extends IncomingInvoice_triggerAbstract {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_complete(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.COMPLETE);
        this.incomingInvoice = incomingInvoice;
    }

    @Action()
    public IncomingInvoice act(
            Person personToAssignTo,
            @Nullable final String comment) {
        trigger(personToAssignTo, comment);
        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public Person default0Act() {
        IPartyRoleType partyRoleType = nextPartyRoleType();
        return partyRoleTypeService.firstMemberOf(partyRoleType, incomingInvoice);
    }

    public List<Person> choices0Act() {
        IPartyRoleType partyRoleType = nextPartyRoleType();
        return partyRoleTypeService.membersOf(partyRoleType);
    }

    private IPartyRoleType nextPartyRoleType() {
        return stateTransitionService
                .peekTaskRoleAssignToAfter(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.COMPLETE);
    }


    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    PartyRoleTypeService partyRoleTypeService;
}
