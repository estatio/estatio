package org.estatio.integtests.capex;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.DisabledException;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.fixture.CountriesRefData;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.capex.dom.documents.HasDocumentAbstract;
import org.estatio.capex.dom.documents.HasDocument_categoriseAsInvoice;
import org.estatio.capex.dom.documents.HasDocument_categoriseAsOrder;
import org.estatio.capex.dom.documents.HasDocument_resetCategorisation;
import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.tasks.TaskIncomingDocumentService;
import org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewModel;
import org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewmodel_saveInvoice;
import org.estatio.capex.dom.documents.order.IncomingOrderViewModel;
import org.estatio.capex.dom.documents.order.IncomingOrderViewmodel_saveOrder;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.capex.dom.invoice.approval.triggers.IncomingInvoice_approveAsAssetManager;
import org.estatio.capex.dom.invoice.approval.triggers.IncomingInvoice_approveAsCountryDirector;
import org.estatio.capex.dom.invoice.payment.PaymentRepository;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.PaperclipForOrder;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.TaskRepository;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.paperclips.PaperclipForFixedAsset;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.currency.CurrencyRepository;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.invoice.paperclips.PaperclipForInvoice;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.roles.EstatioRole;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.currency.CurrenciesRefData;
import org.estatio.fixture.documents.incoming.IncomingPdfFixture;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState.NEW;
import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_ASSET_MANAGER;

public class IncomingDocumentCategorisation_scenario_IntegTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new PropertyForOxfGb());
                executionContext.executeChild(this, new IncomingPdfFixture());
            }
        });
    }

    @Before
    public void setupClock() {
        TickingFixtureClock.replaceExisting();
    }

    @After
    public void teardownClock() {
        TickingFixtureClock.reinstateExisting();
    }

    public static class ClassifyAndCreateFromIncomingDocuments extends
            IncomingDocumentCategorisation_scenario_IntegTest {

        Property propertyForOxf;
        Party buyer;
        Country greatBritain;
        Charge charge_for_works;
        org.estatio.dom.currency.Currency euro;
        DocumentType INCOMING;
        DocumentType INCOMING_ORDER;
        DocumentType INCOMING_INVOICE;
        String fakeIban;

        @Before
        public void setUp(){
            propertyForOxf = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            INCOMING = documentTypeRepository.findByReference(DocumentTypeData.INCOMING.getRef());
            INCOMING_ORDER = documentTypeRepository.findByReference(DocumentTypeData.INCOMING_ORDER.getRef());
            INCOMING_INVOICE = documentTypeRepository.findByReference(DocumentTypeData.INCOMING_INVOICE.getRef());
            buyer = partyRepository.findPartyByReference(PropertyForOxfGb.PARTY_REF_OWNER);
            greatBritain = countryRepository.findCountry(CountriesRefData.GBR);
            charge_for_works = chargeRepository.findByReference("WORKS");
            euro = currencyRepository.findCurrency(CurrenciesRefData.EUR);
            fakeIban = "NL05ABNA0214875743";
        }

        @Test
        public void complete_scenario_test(){
            findIncomingDocuments_works();
            categoriseAsOrder_works();
            categoriseAsInvoice_works();
            resetClassification_works();
            createOrder_works();
            createInvoice_works();
            stateTransition_works();
        }

        List<Task> tasks;
        Task task1;
        Task task2;

        private void findIncomingDocuments_works() {

            // given, when
            tasks = taskRepository.findTasksIncompleteFor(EstatioRole.MAIL_ROOM);
            task1 = tasks.get(0);
            task2 = tasks.get(1);

            // then
            assertThat(tasks.size()).isEqualTo(2);
            assertThat(documentFor(task1).getType()).isEqualTo(INCOMING);
            assertThat(documentFor(task2).getType()).isEqualTo(INCOMING);

        }

        private Document documentFor(final Task task) {
            return taskIncomingDocumentService.lookupFor(task);
        }

        IncomingOrderViewModel incomingOrderViewModel;

        private void categoriseAsOrder_works() {

            // given
            final Document document1 = documentFor(task1);
            IncomingDocumentCategorisationState state = stateTransitionService
                    .currentStateOf(document1, IncomingDocumentCategorisationStateTransition.class);
            assertThat(state).isEqualTo(IncomingDocumentCategorisationState.NEW);

            // when gotoNext is set to true
            Task nextTask = (Task)
                    wrap(mixin(HasDocument_categoriseAsOrder.class, task1))
                    .act(propertyForOxf, null, true);
            transactionService.nextTransaction();

            // then state has changed
            state = stateTransitionService
                    .currentStateOf(document1, IncomingDocumentCategorisationStateTransition.class);
            assertThat(state).isEqualTo(IncomingDocumentCategorisationState.CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY);

            // and also then next is second viewmodel
            assertThat(documentFor(nextTask)).isEqualTo(documentFor(task2));

            // and when
            transactionService.nextTransaction();
            tasks = taskRepository.findTasksIncompleteFor(EstatioRole.MAIL_ROOM);

            // then
            assertThat(tasks.size()).isEqualTo(1);

            List<HasDocumentAbstract> incomingOrders =
                    (List)factory.map(incomingDocumentRepository.findUnclassifiedIncomingOrders());
            assertThat(incomingOrders.size()).isEqualTo(1);

            incomingOrderViewModel = (IncomingOrderViewModel) incomingOrders.get(0);
            assertThat(incomingOrderViewModel.getFixedAsset()).isEqualTo(propertyForOxf);
            assertThat(incomingOrderViewModel.getDocument().getType()).isEqualTo(INCOMING_ORDER);

            // document is linked to property
            assertThat(paperclipRepository.findByAttachedTo(propertyForOxf).size()).isEqualTo(1);
            PaperclipForFixedAsset paperclip = (PaperclipForFixedAsset) paperclipRepository.findByAttachedTo(propertyForOxf).get(0);
            Document doc = documentFor(task1);
            assertThat(paperclip.getDocument()).isEqualTo(doc);

        }


        IncomingInvoiceViewModel incomingInvoiceViewModel;

        private void categoriseAsInvoice_works() {

            // given
            final Document document2 = documentFor(task2);
            IncomingDocumentCategorisationState state = stateTransitionService
                    .currentStateOf(document2, IncomingDocumentCategorisationStateTransition.class);
            assertThat(state).isEqualTo(IncomingDocumentCategorisationState.NEW);

            // when
            wrap(mixin(HasDocument_categoriseAsInvoice.class, task2))
                    .act(propertyForOxf, null, true);
            transactionService.nextTransaction();
            tasks = taskRepository.findTasksIncompleteFor(EstatioRole.MAIL_ROOM);

            // then state has changed
            state = stateTransitionService
                    .currentStateOf(document2, IncomingDocumentCategorisationStateTransition.class);
            assertThat(state).isEqualTo(IncomingDocumentCategorisationState.CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY);

            // and also then
            assertThat(tasks.size()).isEqualTo(0);

            List<HasDocumentAbstract> incomingInvoices =
                    (List) factory.map(incomingDocumentRepository.findUnclassifiedIncomingInvoices());
            assertThat(incomingInvoices.size()).isEqualTo(1);

            incomingInvoiceViewModel = (IncomingInvoiceViewModel) incomingInvoices.get(0);
            assertThat(incomingInvoiceViewModel.getFixedAsset()).isEqualTo(propertyForOxf);
            assertThat(incomingInvoiceViewModel.getDocument().getType()).isEqualTo(INCOMING_INVOICE);
            assertThat(incomingInvoiceViewModel.getDateReceived()).isNotNull();
            assertThat(incomingInvoiceViewModel.getDateReceived()).isEqualTo(incomingInvoiceViewModel.getDocument().getCreatedAt().toLocalDate());

            // document is linked to property
            assertThat(paperclipRepository.findByAttachedTo(propertyForOxf).size()).isEqualTo(2);
            PaperclipForFixedAsset paperclip = (PaperclipForFixedAsset) paperclipRepository.findByAttachedTo(propertyForOxf).get(1);
            Document doc = documentFor(task2);
            assertThat(paperclip.getDocument()).isEqualTo(doc);

        }


        private void resetClassification_works() {

            // given
            tasks = taskRepository.findTasksIncompleteFor(EstatioRole.MAIL_ROOM);
            assertThat(tasks.size()).isEqualTo(0);
            List<Document> unclassified = incomingDocumentRepository.findUnclassifiedIncomingInvoices();
            assertThat(unclassified.size()).isEqualTo(1);

            // when
            wrap(mixin(HasDocument_resetCategorisation.class, incomingInvoiceViewModel)).act(null);
            transactionService.nextTransaction();

            // then
            tasks = taskRepository.findTasksIncompleteFor(EstatioRole.MAIL_ROOM);
            assertThat(tasks.size()).isEqualTo(1);
            unclassified = incomingDocumentRepository.findUnclassifiedIncomingInvoices();
            assertThat(unclassified.size()).isEqualTo(0);
        }


        final String orderNumber = "123";
        Organisation seller;
        final String description = "Some order description";
        final BigDecimal netAmount = new BigDecimal("100.00");
        BigDecimal vatAmount;
        final BigDecimal grossAmount = new BigDecimal("120.00");
        final String period = "F2016";

        Order orderCreated;

        private void createOrder_works(){

            // given
            final Document document = incomingOrderViewModel.getDocument();
            IncomingDocumentCategorisationState state = stateTransitionService
                    .currentStateOf(document, IncomingDocumentCategorisationStateTransition.class);
            assertThat(state).isEqualTo(IncomingDocumentCategorisationState.CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY);

            // when
            try {
                wrap(mixin(IncomingOrderViewmodel_saveOrder.class, incomingOrderViewModel)).
                        act(null, false);
            } catch (DisabledException e){
                assertThat(e.getMessage()).contains("Reason: order number, seller, description, net amount, gross amount, charge, period required");
            }

            // and when
            wrap(incomingOrderViewModel).createSeller("SELLER-REF", false, "Some seller", greatBritain, fakeIban);
            seller = (Organisation) partyRepository.findPartyByReference("SELLER-REF");
            incomingOrderViewModel.changeOrderDetails(orderNumber, buyer, seller, null, null);
            incomingOrderViewModel.changeItemDetails(description, netAmount, null, null, grossAmount);

            incomingOrderViewModel.setCharge(charge_for_works);
            incomingOrderViewModel.setPeriod(period);

            this.orderCreated = (Order)
                    wrap(mixin(IncomingOrderViewmodel_saveOrder.class, incomingOrderViewModel))
                        .act(null, false);
            transactionService.nextTransaction();

            // then
            assertThat(orderCreated).isNotNull();
            assertThat(orderCreated.getOrderNumber()).isEqualTo(orderNumber);
            assertThat(orderCreated.getBuyer()).isEqualTo(buyer);
            assertThat(orderCreated.getSeller()).isEqualTo(seller);
            assertThat(orderCreated.getAtPath()).isEqualTo(buyer.getAtPath());
            assertThat(orderCreated.getOrderDate()).isNull();
            assertThat(orderCreated.getEntryDate()).isNotNull();

            assertThat(orderCreated.getItems().size()).isEqualTo(1);
            OrderItem item = orderCreated.getItems().first();
            assertThat(item.getDescription()).isEqualTo(description);
            assertThat(item.getNetAmount()).isEqualTo(netAmount);
            assertThat(item.getGrossAmount()).isEqualTo(grossAmount);
            assertThat(item.getAtPath()).isEqualTo(buyer.getAtPath());
            assertThat(item.getCharge()).isEqualTo(charge_for_works);
            assertThat(item.getStartDate()).isEqualTo(new LocalDate(2015,7,1));
            assertThat(item.getEndDate()).isEqualTo(new LocalDate(2016,6,30));

            // document state
            state = stateTransitionService
                    .currentStateOf(document, IncomingDocumentCategorisationStateTransition.class);
            assertThat(state).isEqualTo(IncomingDocumentCategorisationState.CLASSIFIED_AS_INVOICE_OR_ORDER);

            // calculated when using method changeItemDetails
            vatAmount = new BigDecimal("20.00");
            assertThat(item.getVatAmount()).isEqualTo(vatAmount);

            // already on viewmodel
            assertThat(item.getProperty()).isEqualTo(propertyForOxf);

            // document is linked to order
            assertThat(paperclipRepository.findByAttachedTo(orderCreated).size()).isEqualTo(1);
            PaperclipForOrder paperclip = (PaperclipForOrder) paperclipRepository.findByAttachedTo(orderCreated).get(0);

            Document doc = incomingOrderViewModel.getDocument();
            assertThat(paperclip.getDocument()).isEqualTo(doc);

            // incoming orders is empty
            List<Document> unclassifiedDocs = incomingDocumentRepository.findUnclassifiedIncomingOrders();
            assertThat(unclassifiedDocs.size()).isEqualTo(0);
        }


        IncomingInvoice invoiceCreated;

        private void createInvoice_works(){

            // given
            incomingInvoiceViewModel = (IncomingInvoiceViewModel)
                    wrap(mixin(HasDocument_categoriseAsInvoice.class, task2))
                    .act(propertyForOxf, null, false);
            transactionService.nextTransaction();

            final Document document = incomingInvoiceViewModel.getDocument();
            IncomingDocumentCategorisationState state = stateTransitionService
                    .currentStateOf(document, IncomingDocumentCategorisationStateTransition.class);
            assertThat(state).isEqualTo(IncomingDocumentCategorisationState.CATEGORISED_AND_ASSOCIATED_WITH_PROPERTY);

            // when
            try {
                wrap(mixin(IncomingInvoiceViewmodel_saveInvoice.class, incomingInvoiceViewModel))
                        .act(null, false);
            } catch (DisabledException e){
                // REVIEW: why are buyer, date received and due date populated already?
//                assertThat(e.getMessage()).contains("Reason: invoice number, buyer, seller, bank account, date received, due date, net amount, gross amount required");
                assertThat(e.getMessage()).contains("Reason: invoice number, seller, bank account, net amount, gross amount required");

            }

            // when
            try {
                wrap(incomingInvoiceViewModel).createBankAccount("123");
            } catch (DisabledException e){
                assertThat(e.getMessage()).contains("Reason: There is no seller specified");
            }

            // when
            // link to order item
            OrderItem orderItem = orderCreated.getItems().first();
            incomingInvoiceViewModel.modifyOrderItem(orderItem);

            // when
            try {
                wrap(incomingInvoiceViewModel).createBankAccount("123");
            } catch (Exception e){
                assertThat(e.getMessage()).contains("Reason: 123 is not a valid iban number");
            }

            // when
            try {
                wrap(incomingInvoiceViewModel).createBankAccount(fakeIban);
            } catch (Exception e){
                assertThat(e.getMessage()).contains("Reason: Some seller has already bank account with iban NL05ABNA0214875743");
            }

            // when
            try {
                wrap(mixin(IncomingInvoiceViewmodel_saveInvoice.class, incomingInvoiceViewModel))
                        .act(null, false);
            } catch (DisabledException e){
                // REVIEW: why are date received and due date populated already?
//                assertThat(e.getMessage()).contains("Reason: invoice number, date received, due date required");
                assertThat(e.getMessage()).contains("Reason: invoice number required");
            }

            // and when
            final String invoiceNumber = "321";
            incomingInvoiceViewModel.setInvoiceNumber(invoiceNumber);
            final LocalDate dueDate = new LocalDate(2016, 2, 14);
            incomingInvoiceViewModel.setDueDate(dueDate);
            final LocalDate dateReceivedDate = new LocalDate(2016, 2, 1);
            incomingInvoiceViewModel.setDateReceived(dateReceivedDate);

            invoiceCreated = (IncomingInvoice) wrap(
                    mixin(IncomingInvoiceViewmodel_saveInvoice.class, incomingInvoiceViewModel)).act(null, false);
            transactionService.nextTransaction();

            // then
            assertThat(invoiceCreated).isNotNull();
            assertThat(invoiceCreated.getInvoiceNumber()).isEqualTo(invoiceNumber);
            assertThat(invoiceCreated.getSeller()).isEqualTo(seller);
            BankAccount sellerBankAccount =  bankAccountRepository.findBankAccountsByOwner(seller).get(0);
            assertThat(sellerBankAccount.getIban()).isEqualTo(fakeIban);
            assertThat(invoiceCreated.getBankAccount()).isEqualTo(sellerBankAccount);
            assertThat(invoiceCreated.getBuyer()).isEqualTo(buyer);
            assertThat(invoiceCreated.getStatus()).isEqualTo(InvoiceStatus.NEW);
            assertThat(invoiceCreated.getAtPath()).isEqualTo(buyer.getAtPath());
            assertThat(invoiceCreated.getCurrency()).isEqualTo(euro);
            assertThat(invoiceCreated.getDueDate()).isEqualTo(dueDate);
            assertThat(invoiceCreated.getDateReceived()).isEqualTo(dateReceivedDate);
            assertThat(invoiceCreated.getPaymentMethod()).isEqualTo(PaymentMethod.BANK_TRANSFER); // set by default
            assertThat(invoiceCreated.getInvoiceDate()).isNull();

            assertThat(invoiceCreated.getItems().size()).isEqualTo(1);
            IncomingInvoiceItem invoiceItem = (IncomingInvoiceItem) invoiceCreated.getItems().first();

            assertThat(invoiceItem.getNetAmount()).isEqualTo(netAmount);
            assertThat(invoiceItem.getVatAmount()).isEqualTo(vatAmount);
            assertThat(invoiceItem.getGrossAmount()).isEqualTo(grossAmount);
            assertThat(invoiceItem.getDescription()).isEqualTo(description);
            assertThat(invoiceItem.getDueDate()).isEqualTo(dueDate);
            assertThat(invoiceItem.getAtPath()).isEqualTo(buyer.getAtPath());
            assertThat(invoiceItem.getCharge()).isEqualTo(charge_for_works);
            assertThat(invoiceItem.getStartDate()).isEqualTo(new LocalDate(2015, 7, 1));
            assertThat(invoiceItem.getEndDate()).isEqualTo(new LocalDate(2016, 6, 30));

            // already on viewmodel
            assertThat(invoiceItem.getFixedAsset()).isEqualTo(propertyForOxf);

            // document is linked to invoice
            assertThat(paperclipRepository.findByAttachedTo(invoiceCreated).size()).isEqualTo(1);
            PaperclipForInvoice paperclip = (PaperclipForInvoice) paperclipRepository.findByAttachedTo(invoiceCreated).get(0);

            Document doc = incomingInvoiceViewModel.getDocument();
            assertThat(paperclip.getDocument()).isEqualTo(doc);

            // document state
            state = stateTransitionService
                    .currentStateOf(document, IncomingDocumentCategorisationStateTransition.class);
            assertThat(state).isEqualTo(IncomingDocumentCategorisationState.CLASSIFIED_AS_INVOICE_OR_ORDER);

            // transitions
            final List<IncomingInvoiceApprovalStateTransition> transitions =
                    incomingInvoiceStateTransitionRepository.findByDomainObject(invoiceCreated);
            assertThat(transitions).hasSize(3);

            assertTransition(transitions.get(0), NEW, APPROVE_AS_ASSET_MANAGER, null);

            assertThat(transitions.get(0).getDomainObject()).isSameAs(invoiceCreated);
            assertThat(transitions.get(0).getCreatedOn()).isNotNull();
            assertThat(transitions.get(0).getCompletedOn()).isNull();
            assertThat(transitions.get(0).isCompleted()).isFalse();

            // task
            assertThat(transitions.get(0).getTask()).isNotNull();
            assertThat(transitions.get(0).getTask().getCompletedBy()).isNull();
            assertThat(transitions.get(0).getTask().isCompleted()).isFalse();

            // incoming invoices is empty
            List<Document> unclassifiedDocs = incomingDocumentRepository.findUnclassifiedIncomingInvoices();
            assertThat(unclassifiedDocs.size()).isEqualTo(0);

        }

        private void stateTransition_works() {

            List<IncomingInvoiceApprovalStateTransition> transitions =
                    incomingInvoiceStateTransitionRepository.findByDomainObject(invoiceCreated);
            assertThat(transitions).hasSize(3);
            final IncomingInvoiceApprovalStateTransition transition1 = transitions.get(0);

            // when
            final IncomingInvoice_approveAsAssetManager _approveAsAssetMgr = wrap(
                    mixin(IncomingInvoice_approveAsAssetManager.class, invoiceCreated));

            _approveAsAssetMgr.act(null);
            transactionService.nextTransaction();

            // then
            assertThat(transition1.getToState()).isNotNull();
            assertThat(transition1.getCreatedOn()).isNotNull();
            assertThat(transition1.getToState()).isNotNull();
            assertThat(transition1.getToState()).isEqualTo(transition1.getTransitionType().getToState());
            assertThat(transition1.getCompletedOn()).isNotNull();
            assertThat(transition1.isCompleted()).isTrue();

            assertThat(transition1.getTask().isCompleted()).isTrue();
            assertThat(transition1.getTask().getCompletedBy()).isNotNull();

            transitions =
                    incomingInvoiceStateTransitionRepository.findByDomainObject(invoiceCreated);
            assertThat(transitions).hasSize(4);

            IncomingInvoiceApprovalStateTransition completedTransition =
                    incomingInvoiceStateTransitionRepository.findByDomainObjectAndCompleted(invoiceCreated, true);
            assertThat(transition1).isSameAs(completedTransition);

            IncomingInvoiceApprovalStateTransition nextTransition =
                    incomingInvoiceStateTransitionRepository.findByDomainObjectAndCompleted(invoiceCreated, false);
            assertThat(nextTransition.getFromState()).isEqualTo(completedTransition.getToState());
            assertThat(nextTransition.getCreatedOn()).isNotNull();
            assertThat(nextTransition.getToState()).isNull();
            assertThat(nextTransition.getCompletedOn()).isNull();
            assertThat(nextTransition.isCompleted()).isFalse();

            assertThat(nextTransition.getTask()).isNotNull();
            assertThat(nextTransition.getTask().getCompletedBy()).isNull();
            assertThat(nextTransition.getTask().isCompleted()).isFalse();

            // and when (LAST transition)
            final IncomingInvoice_approveAsCountryDirector _approveAsCountryDirector = wrap(
                    mixin(IncomingInvoice_approveAsCountryDirector.class, invoiceCreated));

            _approveAsCountryDirector.act(null);
            transactionService.nextTransaction();

            // then
            assertThat(nextTransition.getToState()).isNotNull();

            transitions =
                    incomingInvoiceStateTransitionRepository.findByDomainObject(invoiceCreated);
            assertThat(transitions).hasSize(5);
            assertThat(stateTransitionService.currentStateOf(invoiceCreated, IncomingInvoiceApprovalStateTransition.class))
                    .isEqualTo(IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR);

            completedTransition =
                    incomingInvoiceStateTransitionRepository.findByDomainObjectAndCompleted(invoiceCreated, true);
            assertThat(nextTransition).isSameAs(completedTransition);

        }

    }

    static void assertTransition(
            final IncomingInvoiceApprovalStateTransition transition,
            final IncomingInvoiceApprovalState from,
            final IncomingInvoiceApprovalStateTransitionType type,
            final IncomingInvoiceApprovalState to) {

        assertThat(transition.getTransitionType()).isEqualTo(type);
        if(from != null) {
            assertThat(transition.getFromState()).isEqualTo(from);
        } else {
            assertThat(transition.getFromState()).isNull();
        }
        if(to != null) {
            assertThat(transition.getToState()).isEqualTo(to);
        } else {
            assertThat(transition.getToState()).isNull();
        }
    }

    @Inject
    IncomingInvoiceApprovalStateTransition.Repository incomingInvoiceStateTransitionRepository;

    @Inject
    TaskRepository taskRepository;

    @Inject
    IncomingDocumentRepository incomingDocumentRepository;

    @Inject
    HasDocumentAbstract.Factory factory;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    CurrencyRepository currencyRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    FactoryService factoryService;

    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    BankAccountRepository bankAccountRepository;

    @Inject
    TaskIncomingDocumentService taskIncomingDocumentService;

    @Inject
    PaymentRepository paymentRepository;
}

