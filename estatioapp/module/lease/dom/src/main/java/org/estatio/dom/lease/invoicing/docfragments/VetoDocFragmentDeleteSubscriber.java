package org.estatio.dom.lease.invoicing.docfragments;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;

import org.incode.module.docfragment.dom.impl.DocFragment;

@DomainService(nature = NatureOfService.DOMAIN)
public class VetoDocFragmentDeleteSubscriber  extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final DocFragment.DeleteDomainEvent ev) {
        if(ev.getEventPhase() == AbstractDomainEvent.Phase.HIDE) {
            ev.hide();
        }
    }

}
