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
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"document", "seller", "buyer", "property", "entryDate"}, callSuper = false)
@ToString(of={"document", "seller", "buyer", "property", "entryDate"})
@Accessors(chain = true)
public class OrderBuilder extends BuilderScriptAbstract<Order, OrderBuilder> {

    @Getter @Setter
    Person officeAdministrator;
    @Getter @Setter
    Document document;

    @Getter @Setter
    Organisation seller;
    @Getter @Setter
    Organisation buyer;
    @Getter @Setter
    Project project;
    @Getter @Setter
    Property property;

    @Getter @Setter
    LocalDate entryDate;

    @Getter @Setter
    Tax itemTax;

    @Getter @Setter
    String item1Description;
    @Getter @Setter
    BigDecimal item1NetAmount;
    @Getter @Setter
    BigDecimal item1VatAmount;
    @Getter @Setter
    BigDecimal item1GrossAmount;
    @Getter @Setter
    Charge item1Charge;
    @Getter @Setter
    String item1Period;

    @Getter @Setter
    String item2Description;
    @Getter @Setter
    BigDecimal item2NetAmount;
    @Getter @Setter
    BigDecimal item2VatAmount;
    @Getter @Setter
    BigDecimal item2GrossAmount;
    @Getter @Setter
    Charge item2Charge;
    @Getter @Setter
    String item2Period;

    @Getter
    Order object;
    @Getter
    OrderItem firstItem;
    @Getter
    OrderItem secondItem;

    @Override
    protected void execute(final ExecutionContext ec) {

        checkParam("officeAdministrator", ec, Person.class);
        checkParam("document", ec, Document.class);

        checkParam("buyer", ec, Organisation.class);
        checkParam("seller", ec, Organisation.class);
        if (property!=null) checkParam("property", ec, Property.class);
        if (project!=null )checkParam("project", ec, Project.class);

        checkParam("entryDate", ec, LocalDate.class);

        checkParam("itemTax", ec, Tax.class);

        checkParam("item1Description", ec, String.class);
        checkParam("item1NetAmount", ec, BigDecimal.class);
        checkParam("item1VatAmount", ec, BigDecimal.class);
        checkParam("item1GrossAmount", ec, BigDecimal.class);
        checkParam("item1Charge", ec, Charge.class);
        checkParam("item1Period", ec, String.class);

        if (item2Description!=null) {
            checkParam("item2Description", ec, String.class);
            checkParam("item2NetAmount", ec, BigDecimal.class);
            checkParam("item2VatAmount", ec, BigDecimal.class);
            checkParam("item2GrossAmount", ec, BigDecimal.class);
            checkParam("item2Charge", ec, Charge.class);
            checkParam("item2Period", ec, String.class);
        }

        // given we categorise for a property
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(officeAdministrator.getUsername(), () -> {

            final String comment = "";
            wrap(mixin(Document_categoriseAsOrder.class,document)).act(property, comment);

            // given most/all of the info has been completed  (not using our view model here).
            final String documentName = document.getName();
            Order order = orderRepository.findOrderByDocumentName(documentName).get(0);

            // only way to create a first order item "legally" is through the view model
            final IncomingDocAsOrderViewModel viewModel = mixin(Order_switchView.class, order).act();
            final IncomingDocAsOrderViewModel.changeOrderDetails changeOrderDetails =
                    mixin(IncomingDocAsOrderViewModel.changeOrderDetails.class, viewModel);
            wrap(changeOrderDetails).act(changeOrderDetails.default0Act(), buyer, seller, changeOrderDetails.default3Act(), changeOrderDetails.default4Act());

            wrap(viewModel).setTax(itemTax);
            wrap(viewModel).setProperty(property);
            wrap(viewModel).setProject(project);

            wrap(viewModel).editCharge(item1Charge);
            wrap(viewModel).setDescription(item1Description);
            wrap(viewModel).setNetAmount(item1NetAmount);
            wrap(viewModel).setVatAmount(item1VatAmount);
            wrap(viewModel).setGrossAmount(item1GrossAmount);
            wrap(viewModel).setPeriod(item1Period);
            wrap(viewModel).setBudgetItem(null);

            wrap(viewModel).save();

            wrap(order).changeDates(order.default0ChangeDates(), entryDate);

            // this does an upsert base on the charge, so we still end up with only one item
            wrap(order).addItem(item1Charge, item1Description, item1NetAmount, item1VatAmount, item1GrossAmount, itemTax,
                    item1Period,
                    property,
                    project,
                    null);

            if (item2Description!=null) {
                // add a different charge; this creates a second item
                wrap(order).addItem(item2Charge, item2Description, item2NetAmount, item2VatAmount, item2GrossAmount,
                        itemTax, item2Period, property, project, null);
            }

            this.object = order;
        });


        final List<OrderItem> items = Lists.newArrayList(this.object.getItems());
        firstItem = items.get(0);
        secondItem = items.size()>1 ? items.get(1) : null;


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
