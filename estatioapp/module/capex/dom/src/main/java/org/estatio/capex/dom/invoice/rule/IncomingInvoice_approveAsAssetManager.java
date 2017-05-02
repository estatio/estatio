package org.estatio.capex.dom.invoice.rule;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;

@Mixin
public class IncomingInvoice_approveAsAssetManager extends IncomingInvoice_transitionAbstract {

    public IncomingInvoice_approveAsAssetManager(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceTransition.APPROVE_AS_ASSET_MANAGER);
    }

}
