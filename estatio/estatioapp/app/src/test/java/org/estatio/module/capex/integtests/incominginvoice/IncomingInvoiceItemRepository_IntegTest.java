package org.estatio.module.capex.integtests.incominginvoice;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceItemRepository_IntegTest extends CapexModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {

                executionContext.executeChild(this, new IncomingChargesFraXlsxFixture());
                executionContext.executeChild(this, OrganisationAndComms_enum.TopModelGb.builder());
                executionContext.executeChild(this, OrganisationAndComms_enum.HelloWorldGb.builder());
                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.builder());
            }
        });
    }

    @Inject
    IncomingInvoiceItemRepository incomingInvoiceItemRepository;
    @Inject
    PropertyRepository propertyRepository;

    Party seller;
    Party buyer;
    Property property;
    String invoiceNumber;
    String atPath;
    LocalDate invoiceDate;
    LocalDate dueDate;
    PaymentMethod paymentMethod;
    InvoiceStatus invoiceStatus;
    String description;
    Charge charge;
    Tax tax;

    @Test
    public void findByInvoiceAndChargeAndSequence_works() throws Exception {

        // given
        IncomingInvoice invoice = createIncomingInvoiceAndTwoItemsWithSameCharge();

        // when
        Charge chargeToFindOnItem = charge;
        IncomingInvoiceItem item1 = incomingInvoiceItemRepository.findByInvoiceAndChargeAndSequence(invoice, chargeToFindOnItem, BigInteger.valueOf(1L));
        IncomingInvoiceItem item2 = incomingInvoiceItemRepository.findByInvoiceAndChargeAndSequence(invoice, chargeToFindOnItem, BigInteger.valueOf(2L));

        // then
        assertThat(item1.getInvoice()).isEqualTo(invoice);
        assertThat(item1.getCharge()).isEqualTo(charge);
        assertThat(item1.getSequence()).isEqualTo(BigInteger.valueOf(1L));

        assertThat(item2.getInvoice()).isEqualTo(invoice);
        assertThat(item2.getCharge()).isEqualTo(charge);
        assertThat(item2.getSequence()).isEqualTo(BigInteger.valueOf(2L));

        // and when
        Charge chargeNotToBeFoundOnItem = chargeRepository.findByReference("OTHER");
        IncomingInvoiceItem itemNotToBeFound = incomingInvoiceItemRepository.findByInvoiceAndChargeAndSequence(invoice, chargeNotToBeFoundOnItem, BigInteger.valueOf(1L));

        // then
        assertThat(itemNotToBeFound).isNull();

    }

    private IncomingInvoice createIncomingInvoiceAndTwoItemsWithSameCharge(){
        seller = OrganisationAndComms_enum.TopModelGb.findUsing(serviceRegistry);
        buyer = OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry);
        property = propertyRepository.findPropertyByReference(Property_enum.OxfGb.getRef());
        invoiceNumber = "123";
        invoiceDate = new LocalDate(2017,1,1);
        dueDate = invoiceDate.minusMonths(1);
        final LocalDate vatRegistrationDate = null;
        paymentMethod = PaymentMethod.BANK_TRANSFER;
        invoiceStatus = InvoiceStatus.NEW;
        atPath = "/GBR";
        final LocalDate dateReceived = null;
        final boolean postedToCodaBooks = false;
        final LocalDate paidDate = null;
        final BankAccount bankAccount = null;

        IncomingInvoice invoice = incomingInvoiceRepository.create(IncomingInvoiceType.CAPEX, invoiceNumber, property,
                atPath,
                buyer, seller, invoiceDate, dueDate, vatRegistrationDate, paymentMethod, invoiceStatus, dateReceived,
                bankAccount, IncomingInvoiceApprovalState.PAID,
                postedToCodaBooks, paidDate);

        charge = chargeRepository.findByReference("WORKS");
        description = "some description";
        tax = taxRepository.findByReference("FRF");

        invoice.addItem(
                invoice.getType(), charge, description,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                tax, dueDate, null,  null, null, null);

        invoice.addItem(
                invoice.getType(), charge, description,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                tax, dueDate, null,  null, null, null);

        return invoice;
    }

    @Inject
    PartyRepository partyRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    TaxRepository taxRepository;

}
