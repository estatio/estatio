package org.estatio.capex.dom.invoice.viewmodel;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.capex.dom.documents.BuyerFinder;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;

public class IncomingDocAsInvoiceViewModel_Test {

    @Test
    public void notification_BuyerBarcodeMatchValidation_works(){

        String notification;

        // given
        IncomingDocAsInvoiceViewModel viewModel = new IncomingDocAsInvoiceViewModel();
        Party buyerDerived = new Organisation();
        BuyerFinder buyerFinder = new BuyerFinder(){
            @Override
            public Party buyerDerivedFromDocumentName(final IncomingInvoice incomingInvoice){
                return buyerDerived;
            }
        };
        IncomingInvoice invoice = new IncomingInvoice();
        Party buyerOnViewmodel = new Organisation();
        viewModel.setDomainObject(invoice);
        viewModel.buyerFinder = buyerFinder;
        viewModel.setBuyer(buyerOnViewmodel);
        // when
        notification = viewModel.getNotification();
        // then
        Assertions.assertThat(notification).isEqualTo("Buyer does not match barcode (document name); ");


        // and given (buyers matching)
        viewModel.setBuyer(buyerDerived);
        // when
        notification = viewModel.getNotification();
        // then
        Assertions.assertThat(notification).isNull();


        // and given (no buyer derived)
        BuyerFinder buyerFinderReturningNull = new BuyerFinder(){
            @Override
            public Party buyerDerivedFromDocumentName(final IncomingInvoice incomingInvoice){
                return null;
            }
        };
        viewModel.buyerFinder = buyerFinderReturningNull;
        // when
        notification = viewModel.getNotification();
        // then
        Assertions.assertThat(notification).isNull();
    }

}