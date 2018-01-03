package org.estatio.module.lease.subscriptions;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.scratchpad.Scratchpad;

import org.estatio.module.index.dom.IndexValue;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermForIndexableRepository;

@DomainService(nature = NatureOfService.DOMAIN)
@DomainServiceLayout(menuOrder = "1")
public class IndexSubscriptions extends org.apache.isis.applib.AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final IndexValue.RemoveEvent ev) {
        List<LeaseTermForIndexable> terms;
            switch (ev.getEventPhase()) {
            case VALIDATE:
                terms = leaseTermForIndexableRepository.findByIndexAndDate(ev.getSource().getIndexBase().getIndex(), ev.getSource().getStartDate());
                scratchpad.put(onIndexValueRemoveScratchpadKey = UUID.randomUUID(), terms);
                break;
            case EXECUTED:
                terms = (List<LeaseTermForIndexable>) scratchpad.get(onIndexValueRemoveScratchpadKey);
                for (LeaseTermForIndexable term : terms) {
                    term.verify();
                }
                break;
            default:
                break;
            }
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final IndexValue.UpdateEvent ev) {
        for (LeaseTermForIndexable term :
                leaseTermForIndexableRepository.findByIndexAndDate(ev.getSource().getIndexBase().getIndex(), ev.getSource().getStartDate())) {
            term.verify();

        }
    }

    private transient UUID onIndexValueRemoveScratchpadKey;

    @Inject
    Scratchpad scratchpad;

    @Inject
    LeaseTermForIndexableRepository leaseTermForIndexableRepository;
}
