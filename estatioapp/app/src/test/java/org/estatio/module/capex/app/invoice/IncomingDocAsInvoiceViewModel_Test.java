package org.estatio.module.capex.app.invoice;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.documents.BuyerFinder;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.app.services.OrganisationNameNumberViewModel;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.role.PartyRoleRepository;
import org.estatio.module.tax.dom.Tax;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingDocAsInvoiceViewModel_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock OrganisationRepository mockOrganisationRepo;

    @Mock BankAccountRepository mockBankAccountRepository;

    @Mock PartyRoleRepository mockPartyRoleRepository;

    @Mock InvoiceRepository mockInvoiceRepository;

    @Mock OrderItemRepository mockOrderItemRepository;

    @Mock MessageService mockMessageService;

    @Mock StateTransitionService mockStateTransitionService;

    @Mock OrderItemInvoiceItemLinkRepository mockOrderItemInvoiceItemLinkRepository;

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
        IncomingInvoice incomingInvoice4 = new IncomingInvoice();
        incomingInvoice4.setPaymentMethod(null);

        // expecting
        context.checking(new Expectations() {{
            oneOf(mockInvoiceRepository).findBySeller(seller);
            will(returnValue(Arrays.asList(incomingInvoice1, incomingInvoice2, incomingInvoice3, incomingInvoice4)));
            oneOf(mockMessageService).warnUser("WARNING: payment method is set to bank transfer, but previous invoices from this seller have used the following payment methods: Direct Debit, Cash ");
        }});

        // when
        notification = viewModel.getNotification();

        // then
        assertThat(notification).isEqualTo("WARNING: payment method is set to bank transfer, but previous invoices from this seller have used the following payment methods: Direct Debit, Cash ");

        // and expecting
        context.checking(new Expectations() {{
            oneOf(mockInvoiceRepository).findBySeller(seller);
            will(returnValue(Arrays.asList(incomingInvoice2))); // All historical invoices use payment method BANK_TRANSFER...
        }});

        // when
        notification = viewModel.getNotification();

        // then
        assertThat(notification).isNull(); // ... so no warning
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
        assertThat(notification).isEqualTo("Buyer does not match barcode (document name); ");

        // and given (buyers matching)
        viewModel.setBuyer(buyerDerived);
        // when
        notification = viewModel.getNotification();
        // then
        assertThat(notification).isNull();

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
        assertThat(notification).isNull();
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
        assertThat(viewModel.getSeller()).isEqualTo(seller);
        assertThat(viewModel.getBankAccount()).isEqualTo(bankAccount);

    }

    @Test
    public void choices_orderItem_filters_discarded() {

        // given
        IncomingDocAsInvoiceViewModel viewModel = new IncomingDocAsInvoiceViewModel();
        viewModel.orderItemRepository = mockOrderItemRepository;
        Organisation seller = new Organisation();
        viewModel.setSeller(seller);

        Order order = new Order();
        order.setApprovalState(OrderApprovalState.DISCARDED);
        OrderItem item = new OrderItem();
        item.setOrdr(order);

        // expect
        context.checking(new Expectations() {{

            allowing(mockOrderItemRepository).findBySeller(seller);
            will(returnValue(Arrays.asList(item)));

        }});

        // when discarded
        List<OrderItem> orderItemChoices = viewModel.choicesOrderItem();
        // then
        assertThat(orderItemChoices).isEmpty();

        // and when not discarded
        order.setApprovalState(OrderApprovalState.APPROVED);
        orderItemChoices = viewModel.choicesOrderItem();
        // then
        assertThat(orderItemChoices).contains(item);

        // and when order has no approval state
        order.setApprovalState(null);
        orderItemChoices = viewModel.choicesOrderItem();
        // then
        assertThat(orderItemChoices).contains(item);

    }

    @Test
    public void save_works() {
        // given
        IncomingInvoice incomingInvoice = new IncomingInvoice();
        IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem();
        invoiceItem.setInvoice(incomingInvoice);
        invoiceItem.setIncomingInvoiceType(IncomingInvoiceType.CAPEX);
        incomingInvoice.getItems().add(invoiceItem);
        IncomingDocAsInvoiceViewModel viewModel = new IncomingDocAsInvoiceViewModel();
        viewModel.setDomainObject(incomingInvoice);
        viewModel.stateTransitionService = mockStateTransitionService;
        viewModel.orderItemRepository = mockOrderItemRepository;
        viewModel.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;

        IncomingInvoiceType newIncomingInvoiceType = IncomingInvoiceType.CAPEX;
        Property newProperty = new Property();
        Organisation newBuyer = new Organisation();
        Organisation newSeller = new Organisation();
        BankAccount newBankAccount = new BankAccount();
        String newInvoiceNumber = "1234";
        LocalDate newDateReceived = new LocalDate();
        LocalDate newInvoiceDate = new LocalDate();
        LocalDate newDueDate = new LocalDate();
        PaymentMethod newPaymentMethod = PaymentMethod.DIRECT_DEBIT;
        Currency newCurrency = new Currency();
        Order order = new Order();
        OrderItem newOrderItem = new OrderItem();
        newOrderItem.setOrdr(order);
        String newDescription = "Description";
        BigDecimal newNetAmount = BigDecimal.ONE;
        BigDecimal newVatAmount = BigDecimal.ONE;
        Tax newTax = new Tax();
        BigDecimal newGrossAmount = BigDecimal.ONE;
        Charge newCharge = new Charge();
        newOrderItem.setCharge(newCharge);
        String newPeriod = "F2019";

        viewModel.setIncomingInvoiceType(newIncomingInvoiceType);
        viewModel.setProperty(newProperty);
        viewModel.setBuyer(newBuyer);
        viewModel.setSeller(newSeller);
        viewModel.setBankAccount(newBankAccount);
        viewModel.setInvoiceNumber(newInvoiceNumber);
        viewModel.setDateReceived(newDateReceived);
        viewModel.setInvoiceDate(newInvoiceDate);
        viewModel.setDueDate(newDueDate);
        viewModel.setPaymentMethod(newPaymentMethod);
        viewModel.setCurrency(newCurrency);
        viewModel.setOrderItem(newOrderItem);
        viewModel.setDescription(newDescription);
        viewModel.setNetAmount(newNetAmount);
        viewModel.setVatAmount(newVatAmount);
        viewModel.setTax(newTax);
        viewModel.setGrossAmount(newGrossAmount);
        viewModel.setCharge(newCharge);
        viewModel.setPeriod(newPeriod);

        // expecting
        context.checking(new Expectations() {{
            oneOf(mockStateTransitionService).trigger(incomingInvoice, IncomingInvoiceApprovalStateTransition.class, null, null, null);
            oneOf(mockOrderItemRepository).findUnique(order, newCharge, 0);
            will(returnValue(newOrderItem));
            oneOf(mockOrderItemInvoiceItemLinkRepository).findOrCreateLink(newOrderItem, invoiceItem, BigDecimal.ONE);
        }});

        // when
        viewModel.save();

        // then
        assertThat(incomingInvoice.getType()).isEqualTo(newIncomingInvoiceType);
        assertThat(incomingInvoice.getProperty()).isEqualTo(newProperty);
        assertThat(incomingInvoice.getBuyer()).isEqualTo(newBuyer);
        assertThat(incomingInvoice.getSeller()).isEqualTo(newSeller);
        assertThat(incomingInvoice.getBankAccount()).isEqualTo(newBankAccount);
        assertThat(incomingInvoice.getInvoiceNumber()).isEqualTo(newInvoiceNumber);
        assertThat(incomingInvoice.getDateReceived()).isEqualTo(newDateReceived);
        assertThat(incomingInvoice.getInvoiceDate()).isEqualTo(newInvoiceDate);
        assertThat(incomingInvoice.getDueDate()).isEqualTo(newDueDate);
        assertThat(incomingInvoice.getPaymentMethod()).isEqualTo(newPaymentMethod);
        assertThat(incomingInvoice.getCurrency()).isEqualTo(newCurrency);
        assertThat(incomingInvoice.getDescriptionSummary()).isEqualTo(newDescription);
        assertThat(incomingInvoice.getItems().first().getDescription()).isEqualTo(newDescription);
        assertThat(incomingInvoice.getItems().first().getNetAmount()).isEqualTo(newNetAmount);
        assertThat(incomingInvoice.getItems().first().getVatAmount()).isEqualTo(newVatAmount);
        assertThat(incomingInvoice.getItems().first().getTax()).isEqualTo(newTax);
        assertThat(incomingInvoice.getItems().first().getGrossAmount()).isEqualTo(newGrossAmount);
        assertThat(incomingInvoice.getItems().first().getCharge()).isEqualTo(newCharge);

    }

}