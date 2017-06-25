package org.estatio.integtests.capex.incominginvoice;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.party.OrganisationForHelloWorldGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceItemRepository_IntegTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new OrganisationForTopModelGb());
                executionContext.executeChild(this, new OrganisationForHelloWorldGb());
                executionContext.executeChild(this, new PropertyForOxfGb());
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
    public void findByInvoiceAndCharge_works() throws Exception {

        // given
        IncomingInvoice invoice = createIncomingInvoiceAndItem();

        // when
        Charge chargeToFindOnItem = charge;
        IncomingInvoiceItem item = incomingInvoiceItemRepository.findByInvoiceAndCharge(invoice, chargeToFindOnItem);

        // then
        assertThat(item).isNotNull();
        assertThat(item.getInvoice()).isEqualTo(invoice);
        assertThat(item.getCharge()).isEqualTo(charge);

        // and when
        Charge chargeNotToBeFoundOnItem = chargeRepository.findByReference("OTHER");
        item = incomingInvoiceItemRepository.findByInvoiceAndCharge(invoice, chargeNotToBeFoundOnItem);

        // then
        assertThat(item).isNull();

    }

    private IncomingInvoice createIncomingInvoiceAndItem(){
        seller = partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);
        buyer = partyRepository.findPartyByReference(OrganisationForHelloWorldGb.REF);
        property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
        invoiceNumber = "123";
        invoiceDate = new LocalDate(2017,1,1);
        dueDate = invoiceDate.minusMonths(1);
        paymentMethod = PaymentMethod.BANK_TRANSFER;
        invoiceStatus = InvoiceStatus.NEW;
        atPath = "/GBR";

        IncomingInvoice invoice = incomingInvoiceRepository.create(IncomingInvoiceType.CAPEX, invoiceNumber, property,
                atPath,
                buyer, seller, invoiceDate, dueDate, paymentMethod, invoiceStatus, null,null);

        charge = chargeRepository.findByReference("WORKS");
        description = "some description";
        tax = taxRepository.findByReference("FRF");

        mixin(IncomingInvoice.addItem.class, invoice).act(
                charge, description,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                tax, dueDate, null, null, null, null, null);

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
