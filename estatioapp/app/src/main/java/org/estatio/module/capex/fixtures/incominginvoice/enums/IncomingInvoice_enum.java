package org.estatio.module.capex.fixtures.incominginvoice.enums;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.fixtures.document.enums.IncomingPdf_enum;
import org.estatio.module.capex.fixtures.incominginvoice.builders.IncomingInvoiceBuilder;
import org.estatio.module.capex.fixtures.order.enums.Order_enum;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.charge.fixtures.incoming.enums.IncomingCharge_enum;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;
import org.estatio.module.tax.fixtures.data.Tax_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.asset.fixtures.person.enums.Person_enum.DylanOfficeAdministratorGb;
import static org.estatio.module.asset.fixtures.person.enums.Person_enum.EmmaTreasurerGb;
import static org.estatio.module.asset.fixtures.person.enums.Person_enum.FloellaAssetManagerGb;
import static org.estatio.module.asset.fixtures.person.enums.Person_enum.JonathanIncomingInvoiceManagerGb;
import static org.estatio.module.asset.fixtures.person.enums.Person_enum.OscarCountryDirectorGb;
import static org.estatio.module.capex.dom.invoice.IncomingInvoiceType.CAPEX;
import static org.estatio.module.capex.dom.invoice.IncomingInvoiceType.PROPERTY_EXPENSES;
import static org.estatio.module.capex.fixtures.project.enums.Project_enum.OxfProject;
import static org.estatio.module.charge.fixtures.incoming.enums.IncomingCharge_enum.FrOther;
import static org.estatio.module.charge.fixtures.incoming.enums.IncomingCharge_enum.FrWorks;
import static org.incode.module.base.integtests.VT.bd;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum IncomingInvoice_enum
        implements PersonaWithFinder<IncomingInvoice>, PersonaWithBuilderScript<IncomingInvoice, IncomingInvoiceBuilder> {

    fakeInvoice2Pdf(
            DylanOfficeAdministratorGb,
            IncomingPdf_enum.FakeInvoice2, ApplicationTenancy_enum.Gb, new DateTime(2014,5,22,11,10), "estatio-user-gb",
            Organisation_enum.TopModelGb, BankAccount_enum.TopModelGb, Organisation_enum.HelloWorldGb,
            PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            CAPEX, "65432", PaymentMethod.BANK_TRANSFER,
            ld(2014, 5, 15), ld(2014,6,15), ld(2014,5,13),
            bd("300.00"), bd("363.00"),
            Tax_enum.GB_VATSTD,
            CAPEX, FrWorks, "works done", bd("200.00"), bd("42.00"), bd("242.00"), "F2014", OxfProject,
            PROPERTY_EXPENSES, FrOther, "some expenses", bd("100.00"), bd("21.00"), bd("121.00"), "F2014",null,
            Order_enum.fakeOrder2Pdf,
            bd("200.00"),
            null, null, null, null
    ),
    fakeInvoice3Pdf(
            DylanOfficeAdministratorGb,
            IncomingPdf_enum.FakeInvoice3, ApplicationTenancy_enum.Gb, new DateTime(2014,5,22,11,10), "estatio-user-gb",
            Organisation_enum.TopSellerGb, BankAccount_enum.TopSellerGb, Organisation_enum.HelloWorldGb,
            PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
            PROPERTY_EXPENSES, "13579", PaymentMethod.BANK_TRANSFER,
            ld(2014, 5, 15), ld(2014,6,15), ld(2014,5,13),
            bd("500.00"), bd("600.00"),
            Tax_enum.GB_VATSTD,
            PROPERTY_EXPENSES, FrOther, "some property expenses", bd("500.00"), bd("100.00"), bd("600.00"), "F2014", null,
            null, null, null, null, null, null, null,null,
            null,
            null,
            JonathanIncomingInvoiceManagerGb,
            FloellaAssetManagerGb,
            OscarCountryDirectorGb,
            EmmaTreasurerGb
    );

    private final Person_enum officerAdministrator_d;

    private final IncomingPdf_enum document_d;
    private final ApplicationTenancy_enum documentApplicationTenancy_d;
    private final DateTime documentCreatedAt;
    private final String documentScannedBy;

    private final Organisation_enum seller_d;
    private final BankAccount_enum sellerBankAccount_d;
    private final Organisation_enum buyer_d;
    private final PropertyAndUnitsAndOwnerAndManager_enum property_d; // to derive the owner

    private final IncomingInvoiceType invoiceType;
    private final String invoiceNumber;
    private final PaymentMethod paymentMethod;
    private final LocalDate dateReceived;
    private final LocalDate dueDate;
    private final LocalDate invoiceDate;

    private final BigDecimal netAmount;
    private final BigDecimal grossAmount;

    private final Tax_enum itemTax_d;

    private final IncomingInvoiceType item1InvoiceType;
    private final IncomingCharge_enum item1Charge_d;
    private final String item1Description;
    private final BigDecimal item1NetAmount;
    private final BigDecimal item1VatAmount;
    private final BigDecimal item1GrossAmount;
    private final String item1Period;
    private final Project_enum item1Project_d;

    private final IncomingInvoiceType item2InvoiceType;
    private final IncomingCharge_enum item2Charge_d;
    private final String item2Description;
    private final BigDecimal item2NetAmount;
    private final BigDecimal item2VatAmount;
    private final BigDecimal item2GrossAmount;
    private final String item2Period;
    private final Project_enum item2Project_d;

    private final Order_enum order_d;
    private final BigDecimal orderItemLinkAmount;

    private final Person_enum propertyManager_d;
    private final Person_enum assetManager_d;
    private final Person_enum countryDirector_d;
    private final Person_enum treasurer_d;

    @Override
    public IncomingInvoiceBuilder builder() {
        return new IncomingInvoiceBuilder()
                .setPrereq((f,ec) -> f.setOfficeAdministrator(f.objectFor(officerAdministrator_d, ec)))
                .setPrereq((f,ec) -> {
                    final Document document =
                            document_d.builder().setRunAs(documentScannedBy).build(f, ec).getObject();
                    document.setCreatedAt(documentCreatedAt);
                    document.setAtPath(documentApplicationTenancy_d.getPath());
                    f.setDocument(document);
                })
                .setPrereq((f,ec) -> f.setSeller(f.objectFor(seller_d, ec)))
                .setPrereq((f,ec) -> f.setSellerBankAccount(f.objectFor(sellerBankAccount_d, ec)))
                .setPrereq((f,ec) -> f.setBuyer(f.objectFor(buyer_d, ec)))
                .setPrereq((f,ec) -> f.setProperty(f.objectFor(property_d, ec)))
                .setInvoiceType(invoiceType)
                .setInvoiceNumber(invoiceNumber)
                .setPaymentMethod(paymentMethod)
                .setDateReceived(dateReceived)
                .setDueDate(dueDate)
                .setInvoiceDate(invoiceDate)
                .setNetAmount(netAmount)
                .setGrossAmount(grossAmount)

                .setPrereq((f,ec) -> f.setItemTax(f.objectFor(itemTax_d, ec)))

                .setItem1InvoiceType(item1InvoiceType)
                .setPrereq((f,ec) -> f.setItem1Charge(f.findUsing(item1Charge_d)))
                .setItem1Description(item1Description)
                .setItem1NetAmount(item1NetAmount)
                .setItem1VatAmount(item1VatAmount)
                .setItem1GrossAmount(item1GrossAmount)
                .setItem1Period(item1Period)
                .setPrereq((f,ec) -> f.setItem1Project(f.objectFor(item1Project_d, ec)))

                .setItem2InvoiceType(item2InvoiceType)
                .setPrereq((f,ec) -> f.setItem2Charge(f.findUsing(item2Charge_d)))
                .setItem2Description(item2Description)
                .setItem2NetAmount(item2NetAmount)
                .setItem2VatAmount(item2VatAmount)
                .setItem2GrossAmount(item2GrossAmount)
                .setItem2Period(item2Period)
                .setPrereq((f,ec) -> f.setItem2Project(f.objectFor(item2Project_d, ec)))

                .setPrereq((f,ec) -> f.setOrder(f.objectFor(order_d, ec)))
                .setOrderItemLinkAmount(orderItemLinkAmount)

                .setPrereq((f,ec) -> f.setPropertyManager(f.objectFor(propertyManager_d, ec)))
                .setPrereq((f,ec) -> f.setAssetManager(f.objectFor(assetManager_d, ec)))
                .setPrereq((f,ec) -> f.setCountryDirector(f.objectFor(countryDirector_d, ec)))
                .setPrereq((f,ec) -> f.setTreasurer(f.objectFor(treasurer_d, ec)))
                ;
    }

    @Override
    public IncomingInvoice findUsing(final ServiceRegistry2 serviceRegistry) {
        final IncomingInvoiceRepository repository = serviceRegistry.lookupService(IncomingInvoiceRepository.class);
        return repository.findByInvoiceNumberAndSellerAndInvoiceDate(invoiceNumber, seller_d.findUsing(serviceRegistry), invoiceDate);
    }

}
