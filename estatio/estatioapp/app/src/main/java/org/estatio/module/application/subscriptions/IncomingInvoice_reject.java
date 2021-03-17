package org.estatio.module.application.subscriptions;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;
import org.apache.isis.applib.services.linking.DeepLinkService;

import org.estatio.module.application.spiimpl.email.EmailServiceForEstatio;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.party.dom.role.IPartyRoleType;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoice_reject extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_reject.ActionDomainEvent ev) {
        if (ev.getEventPhase()== AbstractDomainEvent.Phase.EXECUTED){
            final org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_reject source = ev.getSource();
            final IPartyRoleType iPartyRoleType = source.default0Act();
            final IncomingInvoice invoice = (IncomingInvoice) ev.getMixedIn();
            String href;
            try {
                href = String.format("<li>%s</li>", deepLinkService.deepLinkFor(invoice));
            } catch (Exception e){
                // This is just for problems with integration testing
                return;
            }
            final String message = "Please be informed that the following invoice was rejected : " + href;
            String userAtPath = null; // just in case ...
            if (invoice.getAtPath()==null) return;
            if (invoice.getAtPath().startsWith("/ITA")){
                userAtPath = "/ITA";
            }
            if (invoice.getAtPath().startsWith("/FRA") || invoice.getAtPath().startsWith("/BEL")){
                userAtPath = "/FRA";
            }
            if (userAtPath!=null && iPartyRoleType!=null) {
                emailServiceForEstatio
                        .sendToUsersWithRoleTypeAndAtPath(iPartyRoleType, userAtPath,
                                "Estatio info message - Invoice rejected", message);
            }
        }
    }

    @Inject EmailServiceForEstatio emailServiceForEstatio;

    @Inject DeepLinkService deepLinkService;

}
