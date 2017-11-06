package org.estatio.fixture.order;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.DateTime;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.sudo.SudoService;

import org.incode.module.base.integtests.VT;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.documents.categorisation.triggers.Document_categoriseAsOrder;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.order.viewmodel.IncomingDocAsOrderViewModel;
import org.estatio.capex.dom.order.viewmodel.Order_switchView;
import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.project.ProjectRepository;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.documents.incoming.IncomingPdfFixtureForOrder;
import org.estatio.fixture.party.OrganisationForHelloWorldGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.fixture.party.PersonForDylanOfficeAdministratorGb;
import org.estatio.fixture.project.ProjectForOxf;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRepository;
import org.estatio.module.tax.fixture.data.Tax_data;

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
        final Property propertyForOxf = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(PersonForDylanOfficeAdministratorGb.SECURITY_USERNAME, () -> {

            wrap(mixin(Document_categoriseAsOrder.class,fakeOrder2Doc)).act(propertyForOxf, "");

            // given most/all of the info has been completed  (not using our view model here).
            final Project projectForOxf = projectRepository.findByReference("OXF-02");
            final Tax taxForGbr = taxRepository.findByReference(Tax_data.GB_VATSTD.getReference());

            final Party orgTopModelGb = partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);
            final Party orgHelloWorldGb = partyRepository.findPartyByReference(OrganisationForHelloWorldGb.REF);
            final Charge chargeWorks = chargeRepository.findByReference("WORKS");

            Order fakeOrder = orderRepository.findOrderByDocumentName("fakeOrder2.pdf").get(0);

            // only way to create a first order item "legally" is through the view model
            final IncomingDocAsOrderViewModel viewModel = mixin(Order_switchView.class, fakeOrder).act();
            final IncomingDocAsOrderViewModel.changeOrderDetails changeOrderDetails =
                    mixin(IncomingDocAsOrderViewModel.changeOrderDetails.class, viewModel);
            wrap(changeOrderDetails).act(changeOrderDetails.default0Act(), orgHelloWorldGb, orgTopModelGb, changeOrderDetails.default3Act(), changeOrderDetails.default4Act());

            wrap(viewModel).editCharge(chargeWorks);
            wrap(viewModel).setDescription("order item");
            wrap(viewModel).setNetAmount(VT.bd("1000.00"));
            wrap(viewModel).setVatAmount(VT.bd("210.00"));
            wrap(viewModel).setGrossAmount(VT.bd("1210.00"));
            wrap(viewModel).setTax(taxForGbr);
            wrap(viewModel).setPeriod("F2016");
            wrap(viewModel).setProperty(propertyForOxf);
            wrap(viewModel).setProject(projectForOxf);
            wrap(viewModel).setBudgetItem(null);

            wrap(viewModel).save();

            wrap(fakeOrder).changeDates(fakeOrder.default0ChangeDates(), VT.ld(2014,3,6));

            // this does an upsert base on the charge, so we still end up with only one item
            wrap(fakeOrder).addItem(chargeWorks, "order item", VT.bd("1000.00"), VT.bd("210.00"), VT.bd("1210.00"), taxForGbr, "F2017", propertyForOxf, projectForOxf, null);

            // add a different charge; this creates a second item
            final Charge chargeMarketing = chargeRepository.findByReference("MARKETING");
            wrap(fakeOrder).addItem(chargeMarketing, "marketing stuff", VT.bd("500.00"), VT.bd("105.00"), VT.bd("605.00"), taxForGbr, "F2017", propertyForOxf, projectForOxf, null);

            this.order = fakeOrder;
        });


        final List<OrderItem> items = Lists.newArrayList(order.getItems());
        firstItem = items.get(0);
        secondItem = items.get(1);

    }

    @Getter
    Order order;
    @Getter
    OrderItem firstItem;
    @Getter
    OrderItem secondItem;

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
