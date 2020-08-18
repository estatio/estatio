package org.estatio.module.capex.dom.invoice.accountingaudit.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingState;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingStateTransition;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingStateTransitionType;
import org.estatio.module.task.dom.dobj.DomainObject_pendingTaskAbstract;

@Mixin(method="prop")
public class IncomingInvoice_pendingAccountingTask
        extends DomainObject_pendingTaskAbstract<
        IncomingInvoice,
        IncomingInvoiceAccountingStateTransition,
        IncomingInvoiceAccountingStateTransitionType,
        IncomingInvoiceAccountingState> {

    public IncomingInvoice_pendingAccountingTask(final IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceAccountingStateTransition.class);
    }

}
