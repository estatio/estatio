package org.estatio.dom.lease.invoicing.docfragments;

import com.google.common.eventbus.Subscribe;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;

import org.incode.module.docfragment.dom.impl.DocFragment;

@DomainService(nature = NatureOfService.DOMAIN)
public class VetoDocFragmentDeleteSubscriber  extends AbstractSubscriber {

    @Subscribe
    public void on(final DocFragment.DeleteDomainEvent ev) {
        if(ev.getEventPhase() == AbstractDomainEvent.Phase.HIDE) {
            ev.hide();
        }
    }

}
