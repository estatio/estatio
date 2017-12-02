package org.estatio.module.capex.fixtures.incominginvoice;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.sudo.SudoService;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.capex.dom.documents.categorisation.triggers.Document_categoriseAsPropertyInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.capex.dom.orderinvoice.IncomingInvoiceItem_createOrderItemLink;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.fixtures.document.enums.IncomingPdf_enum;
import org.estatio.module.capex.fixtures.order.OrderFixture;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.fixtures.incoming.enums.IncomingCharge_enum;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.fixtures.data.Tax_enum;

import static org.incode.module.base.integtests.VT.bd;
import static org.incode.module.base.integtests.VT.ld;

public class IncomingInvoiceFixture extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext ec) {

        // prereqs
        final Tax taxForGbr = Tax_enum.GB_VATSTD.builder().build(this, ec).getObject();
        final Property propertyForOxf = Property_enum.OxfGb.builder().build(this, ec).getObject();
        final Person dylan = Person_enum.DylanOfficeAdministratorGb.builder().build(this, ec).getObject();
        final Organisation topModelGb = OrganisationAndComms_enum.TopModelGb.builder().build(this, ec).getObject();
        final Organisation helloWorldGb = OrganisationAndComms_enum.HelloWorldGb.builder().build(this, ec).getObject();
        final Project projectForOxf = Project_enum.OxfProject.builder().build(this, ec).getObject();

        ec.executeChild(this, new OrderFixture());

        Document fakeInvoice2Doc = IncomingPdf_enum.FakeInvoice2.builder()
                                                   .setRunAs("estatio-user-gb").build(this, ec).getObject();

        fakeInvoice2Doc.setCreatedAt(new DateTime(2014,5,22,11,10));
        fakeInvoice2Doc.setAtPath(ApplicationTenancy_enum.Gb.getPath());
        sudoService.sudo(dylan.getUsername(), (Runnable) () ->
            wrap(mixin(Document_categoriseAsPropertyInvoice.class,fakeInvoice2Doc)).act(propertyForOxf, "")
        );

        IncomingInvoice fakeInvoice = incomingInvoiceRepository.findIncomingInvoiceByDocumentName("fakeInvoice2.pdf").get(0);
        fakeInvoice.setDateReceived(new LocalDate(2014,5,15));
        fakeInvoice.setSeller(topModelGb);
        fakeInvoice.setBuyer(helloWorldGb);
        fakeInvoice.setType(IncomingInvoiceType.CAPEX);
        fakeInvoice.setDueDate(new LocalDate(2014,6,15));
        fakeInvoice.setInvoiceNumber("65432");
        fakeInvoice.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        fakeInvoice.setInvoiceDate(new LocalDate(2014,5,13));
        fakeInvoice.setNetAmount(new BigDecimal("300.00"));
        fakeInvoice.setGrossAmount(new BigDecimal("363.00"));

        final Charge charge = IncomingCharge_enum.FrWorks.findUsing(serviceRegistry);
        fakeInvoice.addItem(
                IncomingInvoiceType.CAPEX,
                charge,
                "works done",
                bd("200.00"),
                bd("42.00"),
                bd("242.00"),
                taxForGbr,
                ld(2014,6,15),
                "F2014",
                propertyForOxf,
                projectForOxf,
                null);

        IncomingInvoiceItem invoiceItemToLink = (IncomingInvoiceItem) fakeInvoice.getItems().first();
        OrderItem orderItemToLink = orderRepository.findOrderByDocumentName("fakeOrder2.pdf").get(0).getItems().first();
        mixin(IncomingInvoiceItem_createOrderItemLink.class, invoiceItemToLink).act(orderItemToLink, bd("200.00"));

        final Charge frOther = IncomingCharge_enum.FrOther.findUsing(serviceRegistry);
        fakeInvoice.addItem(
                IncomingInvoiceType.PROPERTY_EXPENSES,
                frOther,
                "some expenses",
                bd("100.00"),
                bd("21.00"),
                bd("121.00"),
                taxForGbr,
                ld(2014,6,15),
                "F2014",
                propertyForOxf,
                null,
                null);
    }

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    SudoService sudoService;

    @Inject
    OrderRepository orderRepository;

}
