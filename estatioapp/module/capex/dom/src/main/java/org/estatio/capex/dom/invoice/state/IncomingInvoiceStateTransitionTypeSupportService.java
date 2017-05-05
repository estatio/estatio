package org.estatio.capex.dom.invoice.state;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.state.StateTransitionRepository;
import org.estatio.capex.dom.state.StateTransitionServiceSupportAbstract;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoiceStateTransitionTypeSupportService extends StateTransitionServiceSupportAbstract<
        IncomingInvoice, IncomingInvoiceStateTransition, IncomingInvoiceStateTransitionType, IncomingInvoiceState> {

    public IncomingInvoiceStateTransitionTypeSupportService() {
        super(IncomingInvoiceStateTransitionType.class, IncomingInvoiceStateTransition.class,
                IncomingInvoiceState.NEW);
    }

    @Override
    protected StateTransitionRepository<
            IncomingInvoice,
            IncomingInvoiceStateTransition,
            IncomingInvoiceStateTransitionType,
            IncomingInvoiceState
            > getRepository() {
        return repository;
    }

    @Inject
    IncomingInvoiceStateTransitionRepository repository;

}
