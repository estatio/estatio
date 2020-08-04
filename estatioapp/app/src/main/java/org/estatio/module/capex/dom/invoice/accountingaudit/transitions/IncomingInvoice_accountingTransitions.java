package org.estatio.module.capex.dom.invoice.accountingaudit.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingState;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingStateTransition;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingStateTransitionType;
import org.estatio.module.task.dom.dobj.DomainObject_transitionsAbstract;

/**
 * This mixin cannot (easily) be inlined because it inherits functionality from its superclass, and in any case
 * this follows a common pattern applicable for all domain objects that have an associated state transition machine.
 */
@Mixin(method = "coll")
public class IncomingInvoice_accountingTransitions
        extends DomainObject_transitionsAbstract<
                                        IncomingInvoice,
                                        IncomingInvoiceAccountingStateTransition,
                                        IncomingInvoiceAccountingStateTransitionType,
                                        IncomingInvoiceAccountingState> {

    public IncomingInvoice_accountingTransitions(final IncomingInvoice incomingInvoice) {
        super(incomingInvoice,
                IncomingInvoiceAccountingStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public List<IncomingInvoiceAccountingStateTransition> coll() {
        return super.coll();
    }

}
