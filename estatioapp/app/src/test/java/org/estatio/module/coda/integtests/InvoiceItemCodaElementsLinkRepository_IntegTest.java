package org.estatio.module.coda.integtests;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.coda.dom.elements.InvoiceItemCodaElementsLink;
import org.estatio.module.coda.dom.elements.InvoiceItemCodaElementsLinkRepository;
import org.estatio.module.coda.fixtures.elements.enums.CodaElement_enum;
import org.estatio.module.invoice.dom.InvoiceStatus;

public class InvoiceItemCodaElementsLinkRepository_IntegTest extends CodaModuleIntegTestAbstract {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext ec) {
                    ec.executeChildren(this, CodaElement_enum.FRL5_12345.builder());
                    ec.executeChildren(this, CodaElement_enum.FRL4_77777.builder());
                }
            });
        }

        @Test
        public void finders_work() throws Exception {

            // given
            IncomingInvoice incomingInvoice = incomingInvoiceRepository.create(
                    IncomingInvoiceType.CAPEX,
                    null,
                    null,
                    "/FRA",
                    null, null, null, null, null, null, InvoiceStatus.NEW, null, null,
                    IncomingInvoiceApprovalState.COMPLETED, false, null);
            incomingInvoice.addItem(IncomingInvoiceType.CAPEX, Charge_enum.FrIncomingCharge1.findUsing(serviceRegistry), null, null, null, null, null,
                    null, null, null, null, null);
            IncomingInvoiceItem item = (IncomingInvoiceItem) incomingInvoice.getItems().first();

            // when
            final InvoiceItemCodaElementsLink link = repository
                    .upsert(item, CodaElement_enum.FRL4_77777.findUsing(serviceRegistry),
                            CodaElement_enum.FRL5_12345.findUsing(serviceRegistry));

            // then
            Assertions.assertThat(repository.findUnique(item)).isEqualTo(link);

        }

        @Inject InvoiceItemCodaElementsLinkRepository repository;

        @Inject IncomingInvoiceRepository incomingInvoiceRepository;

}