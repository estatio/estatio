package org.estatio.dom.lease.invoicing.docfragments;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;

import org.incode.module.docfragment.dom.impl.DocFragment;

public class VetoDocFragmentDeleteSubscriber_on_Test {

    private DocFragment.DeleteDomainEvent ev;
    private VetoDocFragmentDeleteSubscriber vetoDocFragmentDeleteSubscriber;

    @Before
    public void setUp() throws Exception {
        ev = new DocFragment.DeleteDomainEvent();
        vetoDocFragmentDeleteSubscriber = new VetoDocFragmentDeleteSubscriber();


    }

    @Test
    public void when_hide_phase() throws Exception {

        ev.setEventPhase(AbstractDomainEvent.Phase.HIDE);

        vetoDocFragmentDeleteSubscriber.on(ev);

        Assertions.assertThat(ev.isHidden()).isTrue();
    }

    @Test
    public void when_not_hide_phase() throws Exception {

        // not that this really happens, but want to exercise the other branch...

        ev.setEventPhase(AbstractDomainEvent.Phase.DISABLE);

        vetoDocFragmentDeleteSubscriber.on(ev);

        Assertions.assertThat(ev.isHidden()).isFalse();
    }
}