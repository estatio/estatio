package org.estatio.module.capex.fixtures;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.sudo.SudoService;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForDylanOfficeAdministratorGb;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.capex.app.DocumentMenu;
import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.capex.dom.documents.categorisation.triggers.Document_categoriseAsPropertyInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.capex.dom.orderinvoice.IncomingInvoiceItem_createOrderItemLink;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.fixtures.document.personas.IncomingPdfForFakeInvoice2;
import org.estatio.module.capex.fixtures.project.personas.ProjectForOxf;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRepository;
import org.estatio.module.tax.fixtures.data.Tax_enum;

public class IncomingInvoiceFixture extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new ProjectForOxf());
        executionContext.executeChild(this, new OrderFixture());
        executionContext.executeChild(this, new IncomingPdfForFakeInvoice2().setRunAs("estatio-user-gb"));
        executionContext.executeChild(this, new PersonAndRolesForDylanOfficeAdministratorGb());

        Document fakeInvoice2Doc = incomingDocumentRepository.matchAllIncomingDocumentsByName(
                IncomingPdfForFakeInvoice2.resourceName).get(0);
        fakeInvoice2Doc.setCreatedAt(new DateTime(2014,5,22,11,10));
        fakeInvoice2Doc.setAtPath("/GBR");
        Property propertyForOxf = Property_enum.OxfGb.findUsing(serviceRegistry);
        sudoService.sudo(Person_enum.DylanOfficeAdministratorGb.getSecurityUserName(), (Runnable) () ->
        wrap(mixin(Document_categoriseAsPropertyInvoice.class,fakeInvoice2Doc)).act(propertyForOxf, ""));

        Project projectForOxf = projectRepository.findByReference("OXF-02");
        Tax taxForGbr = taxRepository.findByReference(Tax_enum.GB_VATSTD.getReference());

        IncomingInvoice fakeInvoice = incomingInvoiceRepository.findIncomingInvoiceByDocumentName("fakeInvoice2.pdf").get(0);
        fakeInvoice.setDateReceived(new LocalDate(2014,5,15));
        fakeInvoice.setSeller(Organisation_enum.TopModelGb.findUsing(serviceRegistry));
        fakeInvoice.setBuyer(Organisation_enum.HelloWorldGb.findUsing(serviceRegistry));
        fakeInvoice.setType(IncomingInvoiceType.CAPEX);
        fakeInvoice.setDueDate(new LocalDate(2014,6,15));
        fakeInvoice.setInvoiceNumber("65432");
        fakeInvoice.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        fakeInvoice.setInvoiceDate(new LocalDate(2014,5,13));
        fakeInvoice.setNetAmount(new BigDecimal("300.00"));
        fakeInvoice.setGrossAmount(new BigDecimal("363.00"));
        fakeInvoice.addItem(
                IncomingInvoiceType.CAPEX,
                chargeRepository.findByReference("WORKS"),
                "works done",
                new BigDecimal("200.00"),
                new BigDecimal("42.00"),
                new BigDecimal("242.00"),
                taxForGbr,
                new LocalDate(2014,6,15),
                "F2014",
                propertyForOxf,
                projectForOxf,
                null);

        IncomingInvoiceItem invoiceItemToLink = (IncomingInvoiceItem) fakeInvoice.getItems().first();
        OrderItem orderItemToLink = orderRepository.findOrderByDocumentName("fakeOrder2.pdf").get(0).getItems().first();
        mixin(IncomingInvoiceItem_createOrderItemLink.class, invoiceItemToLink).act(orderItemToLink, new BigDecimal("200.00"));

        fakeInvoice.addItem(
                IncomingInvoiceType.PROPERTY_EXPENSES,
                chargeRepository.findByReference("OTHER"),
                "some expenses",
                new BigDecimal("100.00"),
                new BigDecimal("21.00"),
                new BigDecimal("121.00"),
                taxForGbr,
                new LocalDate(2014,6,15),
                "F2014",
                propertyForOxf,
                null,
                null);
    }

    @Inject
    IncomingDocumentRepository incomingDocumentRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    TaxRepository taxRepository;

    @Inject
    SudoService sudoService;

    @Inject
    DocumentMenu documentMenu;

    @Inject
    OrderRepository orderRepository;

}
