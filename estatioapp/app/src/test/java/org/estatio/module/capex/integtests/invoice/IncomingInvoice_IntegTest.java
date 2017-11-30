package org.estatio.module.capex.integtests.invoice;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.enums.BankAccount_enum;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_discard;
import org.estatio.module.capex.fixtures.IncomingInvoiceFixture;
import org.estatio.module.capex.fixtures.charge.IncomingChargeFixture;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.country.fixtures.enums.Country_enum;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoice_IntegTest extends CapexModuleIntegTestAbstract {

    Property propertyForOxf;
    Party buyer;
    Party seller;

    Country greatBritain;
    Charge charge_for_works;

    IncomingInvoice incomingInvoice;

    BankAccount bankAccount;

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());
                executionContext.executeChild(this, new IncomingChargeFixture());
                executionContext.executeChild(this, new IncomingInvoiceFixture());
                executionContext.executeChild(this, BankAccount_enum.TopModelGb.toFixtureScript());
            }
        });
    }

    @Test
    public void when_discarded_reported_items_are_reversed_while_no_correction_item_is_added() {

        // given
        incomingInvoiceSetup();
        incomingInvoiceIsCompleted();
        IncomingInvoiceItem reportedItem = oneItemIsReported();

        // when
        invoiceIsDiscarded();

        // then
        assertThat(incomingInvoice.getItems().size()).isEqualTo(3);
        IncomingInvoiceItem reversal = (IncomingInvoiceItem) incomingInvoice.getItems().first();
        assertThat(reversal.getReversalOf()).isEqualTo(reportedItem);
        assertThat(reversal.getNetAmount()).isEqualTo(reportedItem.getNetAmount().negate());
        assertThat(reversal.getReportedDate()).isNull();

    }

    private void incomingInvoiceSetup() {
        propertyForOxf = Property_enum.OxfGb.findUsing(serviceRegistry);

        buyer = Organisation_enum.HelloWorldGb.findUsing(serviceRegistry);
        seller = Organisation_enum.TopModelGb.findUsing(serviceRegistry);

        greatBritain = Country_enum.GBR.findUsing(serviceRegistry);
        charge_for_works = chargeRepository.findByReference("WORKS");

        bankAccount = bankAccountRepository.findBankAccountByReference(seller, BankAccount_enum.TopModelGb.getIban());

        incomingInvoice = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate("65432", seller, new LocalDate(2014,5,13));
        incomingInvoice.setBankAccount(bankAccount);

        assertThat(incomingInvoice).isNotNull();
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.NEW);
        assertThat(incomingInvoice.getItems().size()).isEqualTo(2);

    }

    private void incomingInvoiceIsCompleted() {
        mixin(IncomingInvoice_complete.class, incomingInvoice).act("PROPERTY_MANAGER", null, null);
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);
    }

    private IncomingInvoiceItem oneItemIsReported(){
        IncomingInvoiceItem reportedItem = (IncomingInvoiceItem) incomingInvoice.getItems().first();
        reportedItem.setReportedDate(LocalDate.now());
        return reportedItem;
    }

    private void invoiceIsDiscarded(){
        mixin(IncomingInvoice_discard.class, incomingInvoice).act("some reason");
    }


    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    BankAccountRepository bankAccountRepository;

}

