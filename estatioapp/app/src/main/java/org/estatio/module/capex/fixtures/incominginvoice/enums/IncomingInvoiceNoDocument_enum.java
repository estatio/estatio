package org.estatio.module.capex.fixtures.incominginvoice.enums;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.fixtures.incominginvoice.builders.IncomingInvoiceNoDocumentBuilder;
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
import static org.estatio.module.capex.dom.invoice.IncomingInvoiceType.ITA_MANAGEMENT_COSTS;
import static org.incode.module.base.integtests.VT.bd;
import static org.incode.module.base.integtests.VT.ld;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum IncomingInvoiceNoDocument_enum
        implements PersonaWithFinder<IncomingInvoice>, PersonaWithBuilderScript<IncomingInvoice, IncomingInvoiceNoDocumentBuilder> {

    invoiceForItaNoOrder(
            ApplicationTenancy_enum.It,
            Organisation_enum.TopModelIt, BankAccount_enum.TopModelIt, Organisation_enum.HelloWorldIt,
            PropertyAndUnitsAndOwnerAndManager_enum.RonIt,
            ITA_MANAGEMENT_COSTS, "12345", PaymentMethod.BANK_TRANSFER,
            ld(2018,01,01), ld(2018,02,10), ld (2017, 12, 20),
            bd("100000.00"), bd("122000.00"), null,
            ITA_MANAGEMENT_COSTS, null, "Some costs", bd("100000.00"), bd("22000.00"), bd("122000.00"), "F2018", null,
            null, null, null, null, null, null, null,null,
            null,
            null,
            null,
            null,
            null,
            null
    );

    private final ApplicationTenancy_enum applicationTenancy;

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
    public IncomingInvoiceNoDocumentBuilder builder() {
        return new IncomingInvoiceNoDocumentBuilder()
                .setPrereq((f,ec) -> f.setSeller(f.objectFor(seller_d, ec)))
                .setPrereq((f,ec) -> f.setSellerBankAccount(f.objectFor(sellerBankAccount_d, ec)))
                .setPrereq((f,ec) -> f.setBuyer(f.objectFor(buyer_d, ec)))
                .setPrereq((f,ec) -> f.setProperty(f.objectFor(property_d, ec)))
                .setAtPath(applicationTenancy.getPath())
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
