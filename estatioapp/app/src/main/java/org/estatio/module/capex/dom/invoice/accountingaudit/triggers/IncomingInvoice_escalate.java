package org.estatio.module.capex.dom.invoice.accountingaudit.triggers;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.app.user.MeService;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingStateTransitionType;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;

@Mixin(method = "act")
public class IncomingInvoice_escalate extends IncomingInvoice_triggerAbstract {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_escalate(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceAccountingStateTransitionType.ESCALATE);
        this.incomingInvoice = incomingInvoice;
    }

    public static class ActionDomainEvent
            extends IncomingInvoice_triggerAbstract.ActionDomainEvent<IncomingInvoice_escalate> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "fa-check-circle")
    public Object act(
            final String reason) {
        trigger(null, null, reason, reason);
        return incomingInvoice;
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        final Person meAsPerson = personRepository.findByUsername(meService.me().getUsername());
        if (meAsPerson==null || !meAsPerson.hasPartyRoleType(PartyRoleTypeEnum.ESCALATOR)){
            return "You need role ESCALATOR";
        }
        return reasonGuardNotSatisified();
    }

    @Inject
    MeService meService;

    @Inject PersonRepository personRepository;

}
