package org.estatio.capex.dom.invoice.approval.triggers;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.viewmodel.IncomingInvoice_switchView;

@DomainService(nature = NatureOfService.DOMAIN)
public class SwitchViewService {

    @Inject
    FactoryService factoryService;

    @Inject
    WrapperFactory wrapperFactory;

    @Programmatic
    public Object switchViewIfPossible(final IncomingInvoice incomingInvoice) {
        final IncomingInvoice_switchView switchView =
                factoryService.mixin(IncomingInvoice_switchView.class, incomingInvoice);

        try {
            // attempt to switch view, but..
            return wrapperFactory.wrap(switchView).act();
        } catch (Exception e) {
            // fallback to the incoming invoice otherwise.
            return incomingInvoice;
        }
    }
}
