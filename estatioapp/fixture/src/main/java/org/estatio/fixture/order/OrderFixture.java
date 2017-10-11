package org.estatio.fixture.order;

import java.math.BigDecimal;
import java.util.SortedSet;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.sudo.SudoService;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.documents.categorisation.triggers.Document_categoriseAsOrder;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItem;
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
import org.estatio.fixture.party.PersonForDylanOfficeAdministratorGb;
import org.estatio.fixture.project.ProjectForOxf;
import org.estatio.tax.dom.Tax;
import org.estatio.tax.dom.TaxRepository;
import org.estatio.tax.fixture.data.Tax_data;

import lombok.Getter;

public class OrderFixture extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new ProjectForOxf());
        executionContext.executeChild(this, new IncomingPdfFixtureForOrder().setRunAs("estatio-user-gb"));
        executionContext.executeChild(this, new PersonForDylanOfficeAdministratorGb());

        // given a document has been scanned and uploaded
        Document fakeOrder2Doc = incomingDocumentRepository.matchAllIncomingDocumentsByName(IncomingPdfFixtureForOrder.resourceName).get(0);
        fakeOrder2Doc.setCreatedAt(new DateTime(2014,3,5,10,0));
        fakeOrder2Doc.setAtPath("/GBR");

        // given we categorise for a property
        Property propertyForOxf = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(PersonForDylanOfficeAdministratorGb.SECURITY_USERNAME, (Runnable) () ->
        wrap(mixin(Document_categoriseAsOrder.class,fakeOrder2Doc)).act(propertyForOxf, ""));

        // given most/all of the info has been completed  (not using our view model here).
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

        this.order = fakeOrder;
        final SortedSet<OrderItem> items = order.getItems();
        firstItem = items.first();

    }

    @Getter
    Order order;
    @Getter
    OrderItem firstItem;

    @Inject
    IncomingDocumentRepository incomingDocumentRepository;

    @Inject
    QueryResultsCache queryResultsCache;

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


}
