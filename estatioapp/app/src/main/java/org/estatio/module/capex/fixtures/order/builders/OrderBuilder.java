package org.estatio.module.capex.fixtures.order.builders;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.sudo.SudoService;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.capex.app.order.IncomingDocAsOrderViewModel;
import org.estatio.module.capex.app.order.Order_switchView;
import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.capex.dom.documents.categorisation.triggers.Document_categoriseAsOrder;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"person"}, callSuper = false)
@ToString(of={"person"})
@Accessors(chain = true)
public class OrderBuilder extends BuilderScriptAbstract<Order, OrderBuilder> {

    @Getter @Setter
    Person officerAdministrator;
    @Getter @Setter
    Document document;

    @Getter @Setter
    Party seller;
    @Getter @Setter
    Party buyer;
    @Getter @Setter
    Project project;
    @Getter @Setter
    Property property;

    @Getter @Setter
    LocalDate entryDate;

    @Getter @Setter
    Tax orderItemTax;

    @Getter @Setter
    String orderItem1Description;
    @Getter @Setter
    BigDecimal orderItem1NetAmount;
    @Getter @Setter
    BigDecimal orderItem1VatAmount;
    @Getter @Setter
    BigDecimal orderItem1GrossAmount;
    @Getter @Setter
    Charge orderItem1Charge;
    @Getter @Setter
    String orderItem1Period;

    @Getter @Setter
    String orderItem2Description;
    @Getter @Setter
    BigDecimal orderItem2NetAmount;
    @Getter @Setter
    BigDecimal orderItem2VatAmount;
    @Getter @Setter
    BigDecimal orderItem2GrossAmount;
    @Getter @Setter
    Charge orderItem2Charge;
    @Getter @Setter
    String orderItem2Period;

    @Getter
    Order object;
    @Getter
    OrderItem firstItem;
    @Getter
    OrderItem secondItem;

    @Override
    protected void execute(final ExecutionContext ec) {

        checkParam("officerAdministrator", ec, Person.class);
        checkParam("document", ec, Document.class);

        checkParam("buyer", ec, Party.class);
        checkParam("seller", ec, Party.class);
        checkParam("property", ec, Property.class);
        checkParam("project", ec, Project.class);

        checkParam("entryDate", ec, LocalDate.class);

        checkParam("orderItemTax", ec, Tax.class);

        checkParam("orderItem1Description", ec, String.class);
        checkParam("orderItem1NetAmount", ec, BigDecimal.class);
        checkParam("orderItem1VatAmount", ec, BigDecimal.class);
        checkParam("orderItem1GrossAmount", ec, BigDecimal.class);
        checkParam("orderItem1Charge", ec, Charge.class);
        checkParam("orderItem1Period", ec, String.class);

        checkParam("orderItem2Description", ec, String.class);
        checkParam("orderItem2NetAmount", ec, BigDecimal.class);
        checkParam("orderItem2VatAmount", ec, BigDecimal.class);
        checkParam("orderItem2GrossAmount", ec, BigDecimal.class);
        checkParam("orderItem2Charge", ec, Charge.class);
        checkParam("orderItem2Period", ec, String.class);

        // given we categorise for a property
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(officerAdministrator.getUsername(), () -> {

            final String comment = "";
            wrap(mixin(Document_categoriseAsOrder.class,document)).act(property, comment);

            // given most/all of the info has been completed  (not using our view model here).
            final String documentName = document.getName();
            Order fakeOrder = orderRepository.findOrderByDocumentName(documentName).get(0);

            // only way to create a first order item "legally" is through the view model
            final IncomingDocAsOrderViewModel viewModel = mixin(Order_switchView.class, fakeOrder).act();
            final IncomingDocAsOrderViewModel.changeOrderDetails changeOrderDetails =
                    mixin(IncomingDocAsOrderViewModel.changeOrderDetails.class, viewModel);
            wrap(changeOrderDetails).act(changeOrderDetails.default0Act(), buyer, seller, changeOrderDetails.default3Act(), changeOrderDetails.default4Act());

            wrap(viewModel).setTax(orderItemTax);
            wrap(viewModel).setProperty(property);
            wrap(viewModel).setProject(project);

            wrap(viewModel).editCharge(orderItem1Charge);
            wrap(viewModel).setDescription(orderItem1Description);
            wrap(viewModel).setNetAmount(orderItem1NetAmount);
            wrap(viewModel).setVatAmount(orderItem1VatAmount);
            wrap(viewModel).setGrossAmount(orderItem1GrossAmount);
            wrap(viewModel).setPeriod(orderItem1Period);
            wrap(viewModel).setBudgetItem(null);

            wrap(viewModel).save();

            wrap(fakeOrder).changeDates(fakeOrder.default0ChangeDates(), entryDate);

            // this does an upsert base on the charge, so we still end up with only one item
            wrap(fakeOrder).addItem(orderItem1Charge, orderItem1Description, orderItem1NetAmount, orderItem1VatAmount, orderItem1GrossAmount, orderItemTax,
                    orderItem2Period, // not a typo, but presumably the original fixture was wrong... guessing the upsert doesn't actually update this field
                    property,
                    project,
                    null);

            // add a different charge; this creates a second item
            wrap(fakeOrder).addItem(orderItem2Charge, orderItem2Description, orderItem2NetAmount, orderItem2VatAmount, orderItem2GrossAmount,
                    orderItemTax, orderItem2Period, property, project, null);

            this.object = fakeOrder;
        });


        final List<OrderItem> items = Lists.newArrayList(this.object.getItems());
        firstItem = items.get(0);
        secondItem = items.get(1);


    }


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
