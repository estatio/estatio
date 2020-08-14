package org.estatio.module.coda.integtests;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoice_enum;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
import org.estatio.module.coda.dom.elements.InvoiceItemCodaElementsLinkRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceChargeSetEvent_IntegTest extends CodaModuleIntegTestAbstract {

    IncomingInvoice incomingInvoice;

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext ec) {
                ec.executeChildren(this,
                        new DocumentTypesAndTemplatesForCapexFixture(),
                        new IncomingChargesFraXlsxFixture());

                ec.executeChildren(this,
                        IncomingInvoice_enum.fakeInvoice2Pdf);
            }
        });
    }

    @Test
    public void charge_set_subscriber_test() throws Exception {

        // given
        incomingInvoice = IncomingInvoice_enum.fakeInvoice2Pdf.findUsing(serviceRegistry);

        // then
        assertThat(incomingInvoice.getItems()).hasSize(2);
        assertThat(InvoiceItemCodaElementsLinkRepository.findUnique((IncomingInvoiceItem) incomingInvoice.getItems().first())).isNotNull();
        assertThat(InvoiceItemCodaElementsLinkRepository.findUnique((IncomingInvoiceItem) incomingInvoice.getItems().last())).isNotNull();

    }

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    InvoiceItemCodaElementsLinkRepository InvoiceItemCodaElementsLinkRepository;


}

