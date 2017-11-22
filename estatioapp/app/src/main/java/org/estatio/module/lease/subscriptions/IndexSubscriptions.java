package org.estatio.module.lease.subscriptions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.index.dom.IndexValue;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermForIndexableRepository;

@DomainService(nature = NatureOfService.DOMAIN)
@DomainServiceLayout(menuOrder = "1")
public class IndexSubscriptions extends org.apache.isis.applib.AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final IndexValue.UpdateEvent ev) {
        for (LeaseTermForIndexable term :
                leaseTermForIndexableRepository.findByIndexAndDate(ev.getSource().getIndexBase().getIndex(), ev.getSource().getStartDate())) {
            term.verify();
        }
    }

    @Inject
    LeaseTermForIndexableRepository leaseTermForIndexableRepository;
}
