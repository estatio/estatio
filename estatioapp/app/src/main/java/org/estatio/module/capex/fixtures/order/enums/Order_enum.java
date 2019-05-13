package org.estatio.module.capex.fixtures.order.enums;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.base.Objects;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.capex.fixtures.document.enums.IncomingPdf_enum;
import org.estatio.module.capex.fixtures.order.builders.OrderBuilder;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.charge.fixtures.incoming.enums.IncomingCharge_enum;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;
import org.estatio.module.tax.fixtures.data.Tax_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.bd;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum Order_enum
        implements PersonaWithFinder<Order>, PersonaWithBuilderScript<Order, OrderBuilder> {

    fakeOrder2Pdf(
            IncomingInvoiceType.CAPEX, Person_enum.DanielOfficeAdministratorFr,
            IncomingPdf_enum.FakeOrder2, null,
            ApplicationTenancy_enum.Fr, new DateTime(2014, 3, 5, 10, 0), "estatio-user-fr",
            Organisation_enum.TopModelFr, Organisation_enum.HelloWorldFr,
            Project_enum.VivProjectFr, PropertyAndUnitsAndOwnerAndManager_enum.VivFr,
            ld(2014, 3, 6),
            Tax_enum.FR_VATSTD,
            IncomingCharge_enum.FrWorks, "order item", bd("1000.00"), bd("210.00"), bd("1210.00"), "F2016",
            IncomingCharge_enum.FrMarketing, "marketing stuff", bd("500.00"), bd("105.00"), bd("605.00"), "F2017"
    ),
    fakeOrder3Pdf(
            IncomingInvoiceType.LOCAL_EXPENSES, Person_enum.DanielOfficeAdministratorFr,
            IncomingPdf_enum.FakeOrder2, null,
            ApplicationTenancy_enum.Fr, new DateTime(2018, 1, 5, 10, 0), "estatio-user-fr",
            Organisation_enum.TopModelFr, Organisation_enum.HelloWorldFr,
            null, null,
            ld(2018, 1, 5),
            Tax_enum.FR_VATSTD,
            IncomingCharge_enum.FrFurnitures, "order item", bd("1000.00"), bd("210.00"), bd("1210.00"), "F2018",
            null, null, null, null, null, null
    ),
    italianOrder(
            IncomingInvoiceType.CAPEX, Person_enum.CarmenIncomingInvoiceManagerIt,
            null, "4111/RON/006/001",
            ApplicationTenancy_enum.It, null, null,
            Organisation_enum.TopModelIt, Organisation_enum.HelloWorldIt,
            Project_enum.RonProjectIt, PropertyAndUnitsAndOwnerAndManager_enum.RonIt,
            ld(2018, 1, 5),
            null,
            IncomingCharge_enum.ItExternalConsultantCosts, "italian order item", bd("2000.00"), bd("420.00"), bd("2420.00"), "F2018",
            null, null, null, null, null, null
    ),
    italianOrder4112(
            IncomingInvoiceType.CAPEX, Person_enum.CarmenIncomingInvoiceManagerIt,
            null, "4112/RON/006/001",
            ApplicationTenancy_enum.It, null, null,
            Organisation_enum.TopModelIt, Organisation_enum.HelloWorldIt,
            Project_enum.RonProjectIt, PropertyAndUnitsAndOwnerAndManager_enum.RonIt,
            ld(2018, 4, 5),
            null,
            IncomingCharge_enum.ItExternalConsultantCosts, "italian order item #4112", bd("5000.00"), bd("500.00"), bd("5500.00"), "F2018",
            null, null, null, null, null, null
    ),
    italianOrder4113(
            IncomingInvoiceType.CAPEX, Person_enum.CarmenIncomingInvoiceManagerIt,
            null, "4113/RON/006/001",
            ApplicationTenancy_enum.It, null, null,
            Organisation_enum.TopModelIt, Organisation_enum.HelloWorldIt,
            Project_enum.RonProject2It, PropertyAndUnitsAndOwnerAndManager_enum.RonIt,
            ld(2018, 4, 5),
            null,
            IncomingCharge_enum.ItConstruction, "italian order item #4113, construction costs", bd("15000.00"), bd("1500.00"), bd("16500.00"), "F2018",
            IncomingCharge_enum.ItInternalConsultantCosts, "italian order item #4113, internal consultant costs", bd("5000.00"), bd("500.00"), bd("5500.00"), "F2018"
    );

    private final IncomingInvoiceType orderType;

    private final Person_enum officerAdministrator_d;

    private final IncomingPdf_enum document_d;
    private final String orderNumber;
    private final ApplicationTenancy_enum documentApplicationTenancy_d;
    private final DateTime documentCreatedAt;
    private final String documentScannedBy;

    private final Organisation_enum seller_d;
    private final Organisation_enum buyer_d;
    private final Project_enum project_d;
    private final PropertyAndUnitsAndOwnerAndManager_enum property_d; // to derive the owner

    private final LocalDate entryDate;

    private final Tax_enum itemTax_d;

    private final IncomingCharge_enum item1Charge_d;
    private final String item1Description;
    private final BigDecimal item1NetAmount;
    private final BigDecimal item1VatAmount;
    private final BigDecimal item1GrossAmount;
    private final String item1Period;

    private final IncomingCharge_enum item2Charge_d;
    private final String item2Description;
    private final BigDecimal item2NetAmount;
    private final BigDecimal item2VatAmount;
    private final BigDecimal item2GrossAmount;
    private final String item2Period;

    @Override
    public OrderBuilder builder() {
        return new OrderBuilder()
                .setPrereq((f,ec) -> f.setOfficeAdministrator(f.objectFor(officerAdministrator_d, ec)))
                .setPrereq((f,ec) -> {
                    if(document_d != null) {
                        final Document document =
                                document_d.builder().setRunAs(documentScannedBy).build(f, ec).getObject();
                        document.setCreatedAt(documentCreatedAt);
                        document.setAtPath(documentApplicationTenancy_d.getPath());
                        f.setDocument(document);
                    }
                })
                .setOrderNumber(orderNumber)
                .setPrereq((f,ec) -> f.setSeller(f.objectFor(seller_d, ec)))
                .setPrereq((f,ec) -> f.setBuyer(f.objectFor(buyer_d, ec)))
                .setPrereq((f,ec) -> f.setProject(f.objectFor(project_d, ec)))
                .setPrereq((f,ec) -> f.setProperty(f.objectFor(property_d, ec)))
                .setEntryDate(entryDate)
                .setOrderType(orderType)

                .setPrereq((f,ec) -> f.setItemTax(f.objectFor(itemTax_d, ec)))

                .setPrereq((f,ec) -> f.setItem1Charge(f.findUsing(item1Charge_d)))
                .setItem1Description(item1Description)
                .setItem1NetAmount(item1NetAmount)
                .setItem1VatAmount(item1VatAmount)
                .setItem1GrossAmount(item1GrossAmount)
                .setItem1Period(item1Period)

                .setPrereq((f,ec) -> f.setItem2Charge(f.findUsing(item2Charge_d)))
                .setItem2Description(item2Description)
                .setItem2NetAmount(item2NetAmount)
                .setItem2VatAmount(item2VatAmount)
                .setItem2GrossAmount(item2GrossAmount)
                .setItem2Period(item2Period)
                ;
    }

    @Override
    public Order findUsing(final ServiceRegistry2 serviceRegistry) {
        final OrderRepository orderRepository = serviceRegistry.lookupService(OrderRepository.class);
        if(document_d != null) {
            final String documentName = document_d.findUsing(serviceRegistry).getName();
            final List<Order> orders = orderRepository.findOrderByDocumentName(
                    documentName);
            return orders.stream()
                    .filter(x -> Objects.equal(x.getSeller(), seller_d.findUsing(serviceRegistry)))
                    .filter(x -> Objects.equal(x.getBuyer(), buyer_d.findUsing(serviceRegistry)))
                    .filter(x -> Objects.equal(x.getProperty(), property_d!=null ? property_d.findUsing(serviceRegistry) : null))
                    .filter(x -> Objects.equal(x.getEntryDate(), entryDate))
                    .findFirst()
                    .get(); // fail fast
        } else {
            return orderRepository.findByOrderNumber(orderNumber);
        }
    }

}
