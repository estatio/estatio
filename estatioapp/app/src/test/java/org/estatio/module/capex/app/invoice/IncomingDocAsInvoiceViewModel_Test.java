package org.estatio.module.capex.app.invoice;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.capex.dom.documents.BuyerFinder;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.app.services.OrganisationNameNumberViewModel;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.role.PartyRoleRepository;

public class IncomingDocAsInvoiceViewModel_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock OrganisationRepository mockOrganisationRepo;

    @Mock BankAccountRepository mockBankAccountRepository;

    @Mock PartyRoleRepository mockPartyRoleRepository;

    @Mock InvoiceRepository mockInvoiceRepository;

    @Mock OrderItemRepository mockOrderItemRepo;

    @Mock MessageService mockMessageService;

    @Test
    public void notification_historicalPaymentMethod_works() throws Exception {
        String notification;

        // given
        IncomingDocAsInvoiceViewModel viewModel = new IncomingDocAsInvoiceViewModel() {
            @Override
            public String doubleInvoiceCheck() {
                return null;
            }

            @Override
            public String buyerBarcodeMatchValidation() {
                return null;
            }
        };
        Party seller = new Organisation();
        viewModel.setSeller(seller);
        viewModel.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        viewModel.invoiceRepository = mockInvoiceRepository;
        viewModel.messageService = mockMessageService;

        IncomingInvoice incomingInvoice1 = new IncomingInvoice();
        incomingInvoice1.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        IncomingInvoice incomingInvoice2 = new IncomingInvoice();
        incomingInvoice2.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        IncomingInvoice incomingInvoice3 = new IncomingInvoice();
        incomingInvoice3.setPaymentMethod(PaymentMethod.CASH);

        // expecting
        context.checking(new Expectations() {{
            oneOf(mockInvoiceRepository).findBySeller(seller);
            will(returnValue(Arrays.asList(incomingInvoice1, incomingInvoice2, incomingInvoice3)));
            oneOf(mockMessageService).warnUser("WARNING: payment method is set to bank transfer, but previous invoices from this seller have used the following payment methods: Direct Debit, Cash ");
        }});

        // when
        notification = viewModel.getNotification();

        // then
        Assertions.assertThat(notification).isEqualTo("WARNING: payment method is set to bank transfer, but previous invoices from this seller have used the following payment methods: Direct Debit, Cash ");

        // and expecting
        context.checking(new Expectations() {{
            oneOf(mockInvoiceRepository).findBySeller(seller);
            will(returnValue(Arrays.asList(incomingInvoice2))); // All historical invoices use payment method BANK_TRANSFER...
        }});

        // when
        notification = viewModel.getNotification();

        // then
        Assertions.assertThat(notification).isNull(); // ... so no warning
    }

    @Test
    public void notification_BuyerBarcodeMatchValidation_works() {

        String notification;

        // given
        IncomingDocAsInvoiceViewModel viewModel = new IncomingDocAsInvoiceViewModel();
        Party buyerDerived = new Organisation();
        BuyerFinder buyerFinder = new BuyerFinder() {
            @Override
            public Party buyerDerivedFromDocumentName(final IncomingInvoice incomingInvoice) {
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
        BuyerFinder buyerFinderReturningNull = new BuyerFinder() {
            @Override
            public Party buyerDerivedFromDocumentName(final IncomingInvoice incomingInvoice) {
                return null;
            }
        };
        viewModel.buyerFinder = buyerFinderReturningNull;
        // when
        notification = viewModel.getNotification();
        // then
        Assertions.assertThat(notification).isNull();
    }

    @Test
    public void bankaccount_is_set_when_creating_seller() {

        // given
        IncomingDocAsInvoiceViewModel viewModel = new IncomingDocAsInvoiceViewModel();
        viewModel.organisationRepository = mockOrganisationRepo;
        viewModel.bankAccountRepository = mockBankAccountRepository;
        viewModel.partyRoleRepository = mockPartyRoleRepository;

        Country country = new Country();
        Organisation seller = new Organisation();
        BankAccount bankAccount = new BankAccount();
        String sellerName = "some name";
        OrganisationNameNumberViewModel candidate = new OrganisationNameNumberViewModel(sellerName, null);
        String iban = "NL02RABO0313246581";

        // expect
        context.checking(new Expectations() {{

            oneOf(mockOrganisationRepo).newOrganisation(null, true, sellerName, country);
            will(returnValue(seller));
            oneOf(mockPartyRoleRepository).findOrCreate(seller, IncomingInvoiceRoleTypeEnum.SUPPLIER);
            oneOf(mockBankAccountRepository).newBankAccount(seller, iban, null);
            will(returnValue(bankAccount));
            oneOf(mockBankAccountRepository).getFirstBankAccountOfPartyOrNull(seller);
            will(returnValue(bankAccount));

        }});

        // when
        viewModel.createSeller(candidate, country, iban);

        // then
        Assertions.assertThat(viewModel.getSeller()).isEqualTo(seller);
        Assertions.assertThat(viewModel.getBankAccount()).isEqualTo(bankAccount);

    }

    @Test
    public void choices_orderItem_filters_discarded() {

        // given
        IncomingDocAsInvoiceViewModel viewModel = new IncomingDocAsInvoiceViewModel();
        viewModel.orderItemRepository = mockOrderItemRepo;
        Organisation seller = new Organisation();
        viewModel.setSeller(seller);

        Order order = new Order();
        order.setApprovalState(OrderApprovalState.DISCARDED);
        OrderItem item = new OrderItem();
        item.setOrdr(order);

        // expect
        context.checking(new Expectations() {{

            allowing(mockOrderItemRepo).findBySeller(seller);
            will(returnValue(Arrays.asList(item)));

        }});

        // when discarded
        List<OrderItem> orderItemChoices = viewModel.choicesOrderItem();
        // then
        Assertions.assertThat(orderItemChoices).isEmpty();

        // and when not discarded
        order.setApprovalState(OrderApprovalState.APPROVED);
        orderItemChoices = viewModel.choicesOrderItem();
        // then
        Assertions.assertThat(orderItemChoices).contains(item);

        // and when order has no approval state
        order.setApprovalState(null);
        orderItemChoices = viewModel.choicesOrderItem();
        // then
        Assertions.assertThat(orderItemChoices).contains(item);

    }

}