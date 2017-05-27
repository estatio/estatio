package org.estatio.dom.document;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.DocumentAbstract;

@DomainService(nature = NatureOfService.DOMAIN)
public class Document_hideAtPath extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(DocumentAbstract.AtPathDomainEvent ev) {
        ev.hide();
    }

}
