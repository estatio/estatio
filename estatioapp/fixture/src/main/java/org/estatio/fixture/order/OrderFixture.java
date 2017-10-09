package org.estatio.fixture.order;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.sudo.SudoService;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.DocumentMenu;
import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.documents.categorisation.triggers.Document_categoriseAsOrder;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.project.ProjectRepository;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.party.PartyRepository;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.documents.incoming.IncomingPdfFixtureForOrder;
import org.estatio.fixture.party.OrganisationForHelloWorldGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.fixture.party.PersonForDylanClaytonGb;
import org.estatio.fixture.project.ProjectForOxf;
import org.estatio.tax.dom.Tax;
import org.estatio.tax.dom.TaxRepository;
import org.estatio.tax.fixture.data.Tax_data;

public class OrderFixture extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new ProjectForOxf());
        executionContext.executeChild(this, new IncomingPdfFixtureForOrder().setRunAs("estatio-user-gb"));
        executionContext.executeChild(this, new PersonForDylanClaytonGb());

        Document fakeOrder2Doc = incomingDocumentRepository.matchAllIncomingDocumentsByName(IncomingPdfFixtureForOrder.resourceName).get(0);
        fakeOrder2Doc.setCreatedAt(new DateTime(2014,3,5,10,0));
        fakeOrder2Doc.setAtPath("/GBR");
        Property propertyForOxf = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
        sudoService.sudo(PersonForDylanClaytonGb.SECURITY_USERNAME, (Runnable) () ->
        wrap(mixin(Document_categoriseAsOrder.class,fakeOrder2Doc)).act(propertyForOxf, ""));

        Project projectForOxf = projectRepository.findByReference("OXF-02");
        Tax taxForGbr = taxRepository.findByReference(Tax_data.GB_VATSTD.getReference());

        Order fakeOrder = orderRepository.findOrderByDocumentName("fakeOrder2.pdf").get(0);
        fakeOrder.setSeller(partyRepository.findPartyByReference(OrganisationForTopModelGb.REF));
        fakeOrder.setBuyer(partyRepository.findPartyByReference(OrganisationForHelloWorldGb.REF));
        fakeOrder.addItem(chargeRepository.findByReference("WORKS"), "order item", new BigDecimal("1000.00"), new BigDecimal("210.00"), new BigDecimal("1210.00"), taxForGbr, "F2016", propertyForOxf,projectForOxf, null);
        fakeOrder.setEntryDate(new LocalDate(2014,3,6));
        fakeOrder.setSeller(partyRepository.findPartyByReference(OrganisationForTopModelGb.REF));
        fakeOrder.setBuyer(partyRepository.findPartyByReference(OrganisationForHelloWorldGb.REF));
        fakeOrder.addItem(chargeRepository.findByReference("WORKS"), "order item", new BigDecimal("1000.00"), new BigDecimal("210.00"), new BigDecimal("1210.00"), taxForGbr, "F2017", propertyForOxf,projectForOxf, null);

    }

    @Inject
    IncomingDocumentRepository incomingDocumentRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    OrderRepository orderRepository;

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

}
