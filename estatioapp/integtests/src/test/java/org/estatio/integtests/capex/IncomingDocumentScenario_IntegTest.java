package org.estatio.integtests.capex;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.fixture.CountriesRefData;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.capex.dom.documents.HasDocumentAbstract;
import org.estatio.capex.dom.documents.HasDocumentAbstract_categorizeAsInvoice;
import org.estatio.capex.dom.documents.HasDocumentAbstract_categorizeAsOrder;
import org.estatio.capex.dom.documents.HasDocumentAbstract_resetCategorization;
import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.documents.incoming.IncomingDocumentViewModel;
import org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewModel;
import org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewmodel_createInvoice;
import org.estatio.capex.dom.documents.invoice.OrderItemWrapper;
import org.estatio.capex.dom.documents.order.IncomingOrderViewModel;
import org.estatio.capex.dom.documents.order.IncomingOrderViewmodel_createOrder;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.PaperclipForOrder;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.paperclips.PaperclipForFixedAsset;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.currency.CurrencyRepository;
import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.invoice.paperclips.PaperclipForInvoice;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.currency.CurrenciesRefData;
import org.estatio.fixture.documents.incoming.IncomingPdfFixture;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingDocumentScenario_IntegTest extends EstatioIntegrationTest {

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

    public static class ClassifyAndCreateFromIncomingDocuments extends IncomingDocumentScenario_IntegTest {

        Property propertyForOxf;
        Party buyer;
        Country greatBritain;
        Charge charge_for_works;
        org.estatio.dom.currency.Currency euro;
        DocumentType INCOMING;
        DocumentType INCOMING_ORDER;
        DocumentType INCOMING_INVOICE;

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
        }

        @Test
        public void complete_scenario_test(){
            findIncomingDocuments_works();
            classificationAsOrder_works();
            classificationAsinvoice_works();
            resetClassification_works();
            createOrder_works();
            createInvoice_works();
        }

        List<HasDocumentAbstract> incomingDocuments;
        IncomingDocumentViewModel incomingDocumentViewModel1;
        IncomingDocumentViewModel incomingDocumentViewModel2;

        private void findIncomingDocuments_works() {

            // given, when
            incomingDocuments = factory.map(repository.findIncomingDocuments());
            incomingDocumentViewModel1 = (IncomingDocumentViewModel) incomingDocuments.get(0);
            incomingDocumentViewModel2 = (IncomingDocumentViewModel) incomingDocuments.get(1);

            // then
            assertThat(incomingDocuments.size()).isEqualTo(2);
            assertThat(incomingDocumentViewModel1.getDocument().getType()).isEqualTo(INCOMING);
            assertThat(incomingDocumentViewModel2.getDocument().getType()).isEqualTo(INCOMING);

        }

        HasDocumentAbstract_categorizeAsOrder _categorizeAsOrder;
        List<HasDocumentAbstract> incomingOrders;
        IncomingOrderViewModel incomingOrderViewModel;

        private void classificationAsOrder_works() {

            // given
            _categorizeAsOrder = new HasDocumentAbstract_categorizeAsOrder(incomingDocumentViewModel1);
            serviceRegistry2.injectServicesInto(_categorizeAsOrder);

            // when gotoNext is set to true
            IncomingDocumentViewModel nextViewModel = (IncomingDocumentViewModel) _categorizeAsOrder.act(propertyForOxf, true);
            // then next is second viewmodel
            assertThat(nextViewModel.getDocument()).isEqualTo(incomingDocumentViewModel2.getDocument());

            // and when
            transactionService.nextTransaction();
            incomingDocuments = factory.map(repository.findIncomingDocuments());
            // then
            assertThat(incomingDocuments.size()).isEqualTo(1);

            incomingOrders = factory.map(repository.findUnclassifiedIncomingOrders());
            assertThat(incomingOrders.size()).isEqualTo(1);

            incomingOrderViewModel = (IncomingOrderViewModel) incomingOrders.get(0);
            assertThat(incomingOrderViewModel.getFixedAsset()).isEqualTo(propertyForOxf);
            assertThat(incomingOrderViewModel.getDocument().getType()).isEqualTo(INCOMING_ORDER);

            // document is linked to property
            assertThat(paperclipRepository.findByAttachedTo(propertyForOxf).size()).isEqualTo(1);
            PaperclipForFixedAsset paperclip = (PaperclipForFixedAsset) paperclipRepository.findByAttachedTo(propertyForOxf).get(0);
            Document doc = incomingDocumentViewModel1.getDocument();
            assertThat(paperclip.getDocument()).isEqualTo(doc);

        }

        HasDocumentAbstract_categorizeAsInvoice _categorizeAsInvoice;
        List<HasDocumentAbstract> incomingInvoices;
        IncomingInvoiceViewModel incomingInvoiceViewModel;

        private void classificationAsinvoice_works() {

            // given
            _categorizeAsInvoice = new HasDocumentAbstract_categorizeAsInvoice(incomingDocumentViewModel2);
            serviceRegistry2.injectServicesInto(_categorizeAsInvoice);

            // when
            _categorizeAsInvoice.act(propertyForOxf, true);
            transactionService.nextTransaction();
            incomingDocuments = factory.map(repository.findIncomingDocuments());
            // then
            assertThat(incomingDocuments.size()).isEqualTo(0);

            incomingInvoices = factory.map(repository.findUnclassifiedIncomingInvoices());
            assertThat(incomingInvoices.size()).isEqualTo(1);

            incomingInvoiceViewModel = (IncomingInvoiceViewModel) incomingInvoices.get(0);
            assertThat(incomingInvoiceViewModel.getFixedAsset()).isEqualTo(propertyForOxf);
            assertThat(incomingInvoiceViewModel.getDocument().getType()).isEqualTo(INCOMING_INVOICE);

            // document is linked to property
            assertThat(paperclipRepository.findByAttachedTo(propertyForOxf).size()).isEqualTo(2);
            PaperclipForFixedAsset paperclip = (PaperclipForFixedAsset) paperclipRepository.findByAttachedTo(propertyForOxf).get(1);
            Document doc = incomingDocumentViewModel2.getDocument();
            assertThat(paperclip.getDocument()).isEqualTo(doc);

        }

        HasDocumentAbstract_resetCategorization _resetCategorization;

        private void resetClassification_works() {

            // given
            _resetCategorization = new HasDocumentAbstract_resetCategorization(incomingInvoiceViewModel);
            serviceRegistry2.injectServicesInto(_resetCategorization);

            assertThat(incomingDocuments.size()).isEqualTo(0);

            // when
            _resetCategorization.act();
            transactionService.nextTransaction();

            // then
            incomingDocuments = factory.map(repository.findIncomingDocuments());
            assertThat(incomingDocuments.size()).isEqualTo(1);
            incomingInvoices = factory.map(repository.findUnclassifiedIncomingInvoices());
            assertThat(incomingInvoices.size()).isEqualTo(0);

        }

        IncomingOrderViewmodel_createOrder _createOrder;
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
            assertThat(incomingOrderViewModel.minimalRequiredDataToComplete()).isEqualTo("order number, buyer, seller, description, net amount, gross amount, charge, period required");
            _createOrder = new IncomingOrderViewmodel_createOrder(incomingOrderViewModel);
            serviceRegistry2.injectServicesInto(_createOrder);

            // when
            incomingOrderViewModel.createSeller("SELLER-REF", false, "Seller name", greatBritain);
            seller = (Organisation) partyRepository.findPartyByReference("SELLER-REF");
            incomingOrderViewModel.changeOrderDetails(orderNumber, (Organisation) buyer, seller, null, null);
            incomingOrderViewModel.changeItemDetails(description, netAmount, null, null, grossAmount);

            incomingOrderViewModel.setCharge(charge_for_works);
            incomingOrderViewModel.setPeriod(period);

            orderCreated = (Order) _createOrder.createOrder(false);
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
            assertThat(item.getStartDate()).isEqualTo(new LocalDate(2016,7,1));
            assertThat(item.getEndDate()).isEqualTo(new LocalDate(2017,6,30));

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
            incomingOrders = factory.map(repository.findUnclassifiedIncomingOrders());
            assertThat(incomingOrders.size()).isEqualTo(0);

        }

        IncomingInvoiceViewmodel_createInvoice _createInvoice;
        IncomingInvoice invoiceCreated;

        private void createInvoice_works(){

            // given
            incomingInvoiceViewModel = (IncomingInvoiceViewModel) _categorizeAsInvoice.act(propertyForOxf, false);
            transactionService.nextTransaction();

            _createInvoice = new IncomingInvoiceViewmodel_createInvoice(incomingInvoiceViewModel);
            serviceRegistry2.injectServicesInto(_createInvoice);

            // when
            // link to order item
            OrderItemWrapper orderItemWrapper = new OrderItemWrapper(orderCreated.getOrderNumber(), orderCreated.getItems().first().getCharge());
            incomingInvoiceViewModel.modifyOrderItem(orderItemWrapper);

            assertThat(incomingInvoiceViewModel.minimalRequiredDataToComplete()).isEqualTo("invoice number, due date, payment method required");
            final String invoiceNumber = "321";
            incomingInvoiceViewModel.setInvoiceNumber(invoiceNumber);
            final LocalDate dueDate = new LocalDate(2016, 2, 14);
            incomingInvoiceViewModel.setDueDate(dueDate);
            final PaymentMethod paymentMethod = PaymentMethod.BANK_TRANSFER;
            incomingInvoiceViewModel.setPaymentMethod(paymentMethod);

            invoiceCreated = (IncomingInvoice) _createInvoice.createInvoice(false);
            transactionService.nextTransaction();

            // then
            assertThat(invoiceCreated).isNotNull();
            assertThat(invoiceCreated.getInvoiceNumber()).isEqualTo(invoiceNumber);
            assertThat(invoiceCreated.getSeller()).isEqualTo(seller);
            assertThat(invoiceCreated.getBuyer()).isEqualTo(buyer);
            assertThat(invoiceCreated.getStatus()).isEqualTo(InvoiceStatus.NEW);
            assertThat(invoiceCreated.getAtPath()).isEqualTo(buyer.getAtPath());
            assertThat(invoiceCreated.getCurrency()).isEqualTo(euro);
            assertThat(invoiceCreated.getDueDate()).isEqualTo(dueDate);
            assertThat(invoiceCreated.getPaymentMethod()).isEqualTo(paymentMethod);
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
            assertThat(invoiceItem.getStartDate()).isEqualTo(new LocalDate(2016, 7, 1));
            assertThat(invoiceItem.getEndDate()).isEqualTo(new LocalDate(2017, 6, 30));

            // already on viewmodel
            assertThat(invoiceItem.getFixedAsset()).isEqualTo(propertyForOxf);

            // document is linked to invoice
            assertThat(paperclipRepository.findByAttachedTo(invoiceCreated).size()).isEqualTo(1);
            PaperclipForInvoice paperclip = (PaperclipForInvoice) paperclipRepository.findByAttachedTo(invoiceCreated).get(0);

            Document doc = incomingInvoiceViewModel.getDocument();
            assertThat(paperclip.getDocument()).isEqualTo(doc);

            // incoming invoices is empty
            incomingInvoices = factory.map(repository.findUnclassifiedIncomingInvoices());
            assertThat(incomingInvoices.size()).isEqualTo(0);

        }

    }

    @Inject
    IncomingDocumentRepository repository;

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
    ServiceRegistry2 serviceRegistry2;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    PaperclipRepository paperclipRepository;
}
