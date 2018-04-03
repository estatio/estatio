package org.estatio.module.capex.fixtures.incominginvoice.builders;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;
import org.apache.isis.applib.services.sudo.SudoService;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.bankaccount.verification.triggers.BankAccount_verify;
import org.estatio.module.capex.dom.documents.categorisation.triggers.Document_categoriseAsPropertyInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approve;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_approveAsCountryDirector;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.orderinvoice.IncomingInvoiceItem_createOrderItemLink;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Person;
import org.estatio.module.tax.dom.Tax;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"invoiceNumber", "seller", "invoiceDate"}, callSuper = false)
@ToString(of={"invoiceNumber", "seller", "invoiceDate"})
@Accessors(chain = true)
public class IncomingInvoiceBuilder extends BuilderScriptAbstract<IncomingInvoice, IncomingInvoiceBuilder> {

    @Getter @Setter
    Person officeAdministrator;
    @Getter @Setter
    Document document;

    @Getter @Setter
    Organisation seller;
    @Getter @Setter
    BankAccount sellerBankAccount;
    @Getter @Setter
    Organisation buyer;
    @Getter @Setter
    Property property;

    @Getter @Setter
    IncomingInvoiceType invoiceType;
    @Getter @Setter
    LocalDate dateReceived;
    @Getter @Setter
    LocalDate dueDate;
    @Getter @Setter
    String invoiceNumber;
    @Getter @Setter
    PaymentMethod paymentMethod;
    @Getter @Setter
    LocalDate invoiceDate;

    @Getter @Setter
    BigDecimal netAmount;
    @Getter @Setter
    BigDecimal grossAmount;

    @Getter @Setter
    Tax itemTax;

    @Getter @Setter
    IncomingInvoiceType item1InvoiceType;
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
    Project item1Project;

    @Getter @Setter
    IncomingInvoiceType item2InvoiceType;
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
    @Getter @Setter
    Project item2Project;

    @Getter @Setter
    Order order;
    @Getter @Setter
    BigDecimal orderItemLinkAmount;

    @Getter @Setter
    Person propertyManager;

    @Getter @Setter
    Person assetManager;

    @Getter @Setter
    Person countryDirector;

    @Getter @Setter
    Person treasurer;

    @Getter
    IncomingInvoice object;

    @Override
    protected void execute(final ExecutionContext ec) {

        checkParam("officeAdministrator", ec, Person.class);
        checkParam("document", ec, Document.class);

        checkParam("buyer", ec, Organisation.class);
        checkParam("seller", ec, Organisation.class);
        checkParam("sellerBankAccount", ec, BankAccount.class);
        checkParam("property", ec, Property.class);

        checkParam("dueDate", ec, LocalDate.class);
        checkParam("invoiceDate", ec, LocalDate.class);
        checkParam("dateReceived", ec, LocalDate.class);

        checkParam("itemTax", ec, Tax.class);

        checkParam("item1InvoiceType", ec, IncomingInvoiceType.class);
        //checkParam("item1Project", ec, Project.class); // optional
        checkParam("item1Charge", ec, Charge.class);
        checkParam("item1Description", ec, String.class);
        checkParam("item1NetAmount", ec, BigDecimal.class);
        checkParam("item1VatAmount", ec, BigDecimal.class);
        checkParam("item1GrossAmount", ec, BigDecimal.class);
        checkParam("item1Period", ec, String.class);

        if (getItem2InvoiceType()!=null) {
            checkParam("item2InvoiceType", ec, IncomingInvoiceType.class);
            //checkParam("item2Project", ec, Project.class); // optional
            checkParam("item2Charge", ec, Charge.class);
            checkParam("item2Description", ec, String.class);
            checkParam("item2NetAmount", ec, BigDecimal.class);
            checkParam("item2VatAmount", ec, BigDecimal.class);
            checkParam("item2GrossAmount", ec, BigDecimal.class);
            checkParam("item2Period", ec, String.class);
        }

        // checkParam("orderDocument", ec, Document.class); // optional
        if(order != null) {
            checkParam("orderItemLinkAmount", ec, BigDecimal.class);
        }

        if(propertyManager!=null) {
            checkParam("propertyManager", ec, Person.class);
            checkParam("assetManager", ec, Person.class);
            checkParam("countryDirector", ec, Person.class);
            checkParam("treasurer", ec, Person.class);
        }

        sudoService.sudo(officeAdministrator.getUsername(), (Runnable) () ->
            wrap(mixin(Document_categoriseAsPropertyInvoice.class, document)).act(property, "")
        );

        final String documentName = document.getName();

        IncomingInvoice invoice = incomingInvoiceRepository.findIncomingInvoiceByDocumentName(documentName).get(0);
        invoice.setDateReceived(dateReceived);
        invoice.setSeller(seller);
        invoice.setBankAccount(sellerBankAccount);
        invoice.setBuyer(buyer);
        invoice.setType(invoiceType);
        invoice.setDueDate(dueDate);
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setPaymentMethod(paymentMethod);
        invoice.setInvoiceDate(invoiceDate);
        invoice.setNetAmount(netAmount);
        invoice.setGrossAmount(grossAmount);

        invoice.addItem(
                item1InvoiceType,
                item1Charge,
                item1Description,
                item1NetAmount,
                item1VatAmount,
                item1GrossAmount,
                itemTax,
                dueDate,
                item1Period,
                property,
                item1Project,
                null);

        if(order != null) {
            IncomingInvoiceItem invoiceItemToLink = (IncomingInvoiceItem) invoice.getItems().first();
            OrderItem orderItemToLink = order.getItems().first();
            mixin(IncomingInvoiceItem_createOrderItemLink.class, invoiceItemToLink).act(orderItemToLink, orderItemLinkAmount);
        }

        if (item2InvoiceType!=null) {
            invoice.addItem(
                    item2InvoiceType,
                    item2Charge,
                    item2Description,
                    item2NetAmount,
                    item2VatAmount,
                    item2GrossAmount,
                    itemTax,
                    dueDate,
                    item2Period,
                    property,
                    item2Project,
                    null);
        }

        if (propertyManager!=null) {
            // complete and approve invoice and verify bankaccount
            sudoService.sudo(propertyManager.getUsername(), (Runnable) () ->
                    wrap(mixin(IncomingInvoice_complete.class, invoice)).act("PROPERTY_MANAGER", null, null)
            );
            sudoService.sudo(assetManager.getUsername(), (Runnable) () ->
                    wrap(mixin(IncomingInvoice_approve.class, invoice)).act("ASSET_MANAGER", null, null, false)
            );
            sudoService.sudo(countryDirector.getUsername(), (Runnable) () ->
                    wrap(mixin(IncomingInvoice_approveAsCountryDirector.class, invoice)).act(null, false)
            );
            sudoService.sudo(treasurer.getUsername(), (Runnable) () ->
                    wrap(mixin(BankAccount_verify.class, sellerBankAccount)).act(null)
            );
        }

    }

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    SudoService sudoService;

}
