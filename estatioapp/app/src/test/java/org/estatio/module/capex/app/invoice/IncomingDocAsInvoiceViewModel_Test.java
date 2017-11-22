package org.estatio.module.capex.app.invoice;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.capex.dom.documents.BuyerFinder;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.Party;

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

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock OrganisationRepository mockOrganisationRepo;

    @Mock BankAccountRepository mockBankAccountRepository;

    @Test
    public void bankaccount_is_set_when_creating_seller(){

        // given
        IncomingDocAsInvoiceViewModel viewModel = new IncomingDocAsInvoiceViewModel();
        viewModel.organisationRepository = mockOrganisationRepo;
        viewModel.bankAccountRepository = mockBankAccountRepository;

        Country country = new Country();
        Organisation seller = new Organisation();
        BankAccount bankAccount = new BankAccount();
        String sellerName = "some name";
        String iban = "NL02RABO0313246581";

        // expect
        context.checking(new Expectations(){{

            oneOf(mockOrganisationRepo).newOrganisation(null, true, sellerName, country);
            will(returnValue(seller));
            oneOf(mockBankAccountRepository).newBankAccount(seller,iban, null);
            will(returnValue(bankAccount));
            oneOf(mockBankAccountRepository).getFirstBankAccountOfPartyOrNull(seller);
            will(returnValue(bankAccount));

        }});

        // when
        viewModel.createSeller(sellerName, country, iban);

        // then
        Assertions.assertThat(viewModel.getSeller()).isEqualTo(seller);
        Assertions.assertThat(viewModel.getBankAccount()).isEqualTo(bankAccount);

    }

}