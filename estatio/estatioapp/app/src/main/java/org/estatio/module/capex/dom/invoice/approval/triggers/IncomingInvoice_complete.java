package org.estatio.module.capex.dom.invoice.approval.triggers;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.capex.dom.invoice.inference.PartyRoleMemberInferenceServiceForFixedAssetRoleAndIncomingInvoice;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.IPartyRoleType;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method = "act")
public class IncomingInvoice_complete extends IncomingInvoice_triggerAbstract {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_complete(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceApprovalStateTransitionType.COMPLETE);
        this.incomingInvoice = incomingInvoice;
    }

    public static class ActionDomainEvent
            extends IncomingInvoice_triggerAbstract.ActionDomainEvent<IncomingInvoice_complete> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "fa-flag-checkered")
    public IncomingInvoice act(
            final IPartyRoleType role,
            @Nullable final Person personToAssignNextTo,
            @Nullable final String comment) {
        trigger(role, personToAssignNextTo, comment, comment);

        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }

    public IPartyRoleType default0Act() {
        return choices0Act().stream().findFirst().orElse(null);
    }

    public List<? extends IPartyRoleType> choices0Act() {
        return enumPartyRoleType();
    }

    public Person default1Act(final IPartyRoleType roleType) {
        if (roleType == FixedAssetRoleTypeEnum.PROPERTY_MANAGER && incomingInvoice.getProperty()!=null){
            final List<Person> list = partyRoleMemberInferenceServiceForFixedAssetRoleAndIncomingInvoice
                    .inferMembersOf(FixedAssetRoleTypeEnum.PROPERTY_MANAGER, incomingInvoice);
            return list.size()==1 ? list.get(0) : null;
        }
        return defaultPersonToAssignNextTo(roleType);
    }

    public List<Person> choices1Act(final IPartyRoleType roleType) {
        if (roleType == FixedAssetRoleTypeEnum.PROPERTY_MANAGER && incomingInvoice.getProperty()!=null){
            return partyRoleMemberInferenceServiceForFixedAssetRoleAndIncomingInvoice.inferMembersOf(FixedAssetRoleTypeEnum.PROPERTY_MANAGER, incomingInvoice);
        }
        return choicesPersonToAssignNextTo(roleType);
    }

    @Inject PartyRoleMemberInferenceServiceForFixedAssetRoleAndIncomingInvoice partyRoleMemberInferenceServiceForFixedAssetRoleAndIncomingInvoice;

}
