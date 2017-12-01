package org.estatio.module.capex.fixtures;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.DateTime;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.sudo.SudoService;

import org.incode.module.base.integtests.VT;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.capex.app.order.IncomingDocAsOrderViewModel;
import org.estatio.module.capex.app.order.Order_switchView;
import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.capex.dom.documents.categorisation.triggers.Document_categoriseAsOrder;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.fixtures.document.personas.IncomingPdfForFakeOrder2;
import org.estatio.module.capex.fixtures.project.personas.ProjectForOxf;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRepository;
import org.estatio.module.tax.fixtures.data.Tax_enum;

import lombok.Getter;

public class OrderFixture extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new ProjectForOxf());
        executionContext.executeChild(this, new IncomingPdfForFakeOrder2().setRunAs("estatio-user-gb"));
        executionContext.executeChild(this, Person_enum.DylanOfficeAdministratorGb.toBuilderScript());

        // given a document has been scanned and uploaded
        Document fakeOrder2Doc = incomingDocumentRepository.matchAllIncomingDocumentsByName(IncomingPdfForFakeOrder2.resourceName).get(0);
        fakeOrder2Doc.setCreatedAt(new DateTime(2014,3,5,10,0));
        fakeOrder2Doc.setAtPath("/GBR");

        // given we categorise for a property
        final Property propertyForOxf = Property_enum.OxfGb.findUsing(serviceRegistry);

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.DylanOfficeAdministratorGb.getSecurityUserName(), () -> {

            wrap(mixin(Document_categoriseAsOrder.class,fakeOrder2Doc)).act(propertyForOxf, "");

            // given most/all of the info has been completed  (not using our view model here).
            final Project projectForOxf = projectRepository.findByReference("OXF-02");
            final Tax taxForGbr = taxRepository.findByReference(Tax_enum.GB_VATSTD.getReference());

            final Party orgTopModelGb = OrganisationAndComms_enum.TopModelGb.findUsing(serviceRegistry);
            final Party orgHelloWorldGb = partyRepository.findPartyByReference(
                    OrganisationAndComms_enum.HelloWorldGb.getRef());
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
