package org.estatio.module.coda.integtests;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.coda.dom.elements.CodaMappingRepository;
import org.estatio.module.coda.dom.elements.CodaTransactionType;
import org.estatio.module.coda.dom.elements.DocumentType;
import org.estatio.module.coda.fixtures.elements.enums.CodaElement_enum;
import org.estatio.module.coda.fixtures.elements.enums.CodaMapping_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaMappingRepository_IntegTest extends CodaModuleIntegTestAbstract {

    public static class Repo_tests extends CodaMappingRepository_IntegTest {

        @Before
        public void setupData() {

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext ec) {
                    ec.executeChildren(this, CodaMapping_enum.FRL4_77777_INC1.builder());
                    ec.executeChildren(this, CodaMapping_enum.FRL5_12345_INC1.builder());
                }
            });
        }

        @Test
        public void finders_work() throws Exception {

            // given fixtures
            // when, then
            assertThat(codaMappingRepository.findByAll("/FRA",
                    DocumentType.INVOICE_IN,
                    IncomingInvoiceType.CAPEX,
                    CodaTransactionType.STAT,
                    Charge_enum.FrIncomingCharge1.findUsing(serviceRegistry),
                    true,
                    null,
                    null,
                    null,
                    null,
                    CodaElement_enum.FRL4_77777.findUsing(serviceRegistry)
                    )).isEqualTo(CodaMapping_enum.FRL4_77777_INC1.findUsing(serviceRegistry));
            assertThat(codaMappingRepository.findByAll("/FRA",
                    DocumentType.INVOICE_IN,
                    IncomingInvoiceType.CAPEX,
                    CodaTransactionType.STAT,
                    Charge_enum.FrIncomingCharge1.findUsing(serviceRegistry),
                    true,
                    null,
                    null,
                    null,
                    null,
                    CodaElement_enum.FRL5_12345.findUsing(serviceRegistry)
            )).isEqualTo(CodaMapping_enum.FRL5_12345_INC1.findUsing(serviceRegistry));

            assertThat(codaMappingRepository.findMatching(IncomingInvoiceType.CAPEX, Charge_enum.FrIncomingCharge1.findUsing(serviceRegistry))).hasSize(2);
            assertThat(codaMappingRepository.findMatching(IncomingInvoiceType.SERVICE_CHARGES, Charge_enum.FrIncomingCharge1.findUsing(serviceRegistry))).isEmpty();
            assertThat(codaMappingRepository.findMatching(IncomingInvoiceType.CAPEX, Charge_enum.FrIncomingCharge2.findUsing(serviceRegistry))).isEmpty();

            assertThat(codaMappingRepository.findByCharge(Charge_enum.FrIncomingCharge1.findUsing(serviceRegistry))).hasSize(2);
            assertThat(codaMappingRepository.findByCharge(Charge_enum.FrIncomingCharge2.findUsing(serviceRegistry))).isEmpty();

            assertThat(codaMappingRepository.findByCodaElement(CodaElement_enum.FRL4_77777.findUsing(serviceRegistry))).hasSize(1);
            assertThat(codaMappingRepository.findByCodaElement(CodaElement_enum.FRL5_12345.findUsing(serviceRegistry))).hasSize(1);


        }

    }



    @Inject CodaMappingRepository codaMappingRepository;

}