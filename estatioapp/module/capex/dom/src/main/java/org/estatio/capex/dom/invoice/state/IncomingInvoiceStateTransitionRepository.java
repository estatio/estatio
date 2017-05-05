package org.estatio.capex.dom.invoice.state;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.state.StateTransitionRepositoryAbstract;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = IncomingInvoiceStateTransition.class
)
public class IncomingInvoiceStateTransitionRepository
        extends StateTransitionRepositoryAbstract<
                IncomingInvoice,
                IncomingInvoiceStateTransition,
                IncomingInvoiceStateTransitionType,
                IncomingInvoiceState> {

    public IncomingInvoiceStateTransitionRepository() {
        super(IncomingInvoiceStateTransition.class);
    }

}
