package org.estatio.module.capex.integtests.order;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.togglz.junit.TogglzRule;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.base.spiimpl.togglz.EstatioTogglzFeature;
import org.estatio.module.capex.contributions.Order_generateDocument;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.fixtures.order.enums.Order_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
import org.estatio.module.invoice.dom.DocumentTypeData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.incode.module.base.integtests.VT.ld;

public class Order_generateDocument_IntegTest extends CapexModuleIntegTestAbstract {

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {

                // taken from the DocumentTypesAndTemplatesSeedService (not run in integ tests by default)
                final LocalDate templateDate = ld(2012,1,1);

                executionContext.executeChildren(this,
                        new DocumentTypesAndTemplatesForCapexFixture(templateDate),
                        new IncomingChargesFraXlsxFixture());

                executionContext.executeChildren(this,
                        Order_enum.fakeOrder2Pdf,
                        Person_enum.JonathanIncomingInvoiceManagerGb);
            }
        });

        order = Order_enum.fakeOrder2Pdf.findUsing(serviceRegistry);
        orderTemplateDocumentType = DocumentTypeData.ORDER_CONFIRM.findUsing(documentTypeRepository);
    }

    DocumentType orderTemplateDocumentType;
    Order order;


    @Test
    public void happy_case() throws Exception {

        // given
        final Order_generateDocument mixin = mixin(Order_generateDocument.class, order);

        final List<DocumentTemplate> documentTemplates = mixin.choices0Act();
        assertThat(documentTemplates).hasSize(1);

        final DocumentTemplate documentTemplate = documentTemplates.get(0);
        assertThat(documentTemplate.getType()).isEqualTo(orderTemplateDocumentType);

        // when
        final Document document = (Document) mixin.act(documentTemplate);

        // then
        final Blob blob = document.getBlob();

        assertThat(blob).isNotNull();
    }


    @Inject
    DocumentTypeRepository documentTypeRepository;

}
