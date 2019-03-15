package org.estatio.module.capex.integtests.order;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.togglz.junit.TogglzRule;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.base.spiimpl.togglz.EstatioTogglzFeature;
import org.estatio.module.capex.app.DocumentMenu;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.fixtures.order.enums.Order_enum;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.capex.spiimpl.docs.rml.RendererModelFactoryForOrder;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesItaXlsxFixture;
import org.estatio.module.invoice.dom.DocumentTypeData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.incode.module.base.integtests.VT.ld;

public class DocumentTemplate_renderContent_IntegTest extends CapexModuleWithFakeGotenbergBootstrapIntegTest {

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Inject
    DocumentMenu documentMenu;
    @Inject
    private DocumentTemplateRepository documentTemplateRepository;

    DocumentType orderTemplateDocumentType;
    Order order;

    @Before
    public void setupData() {

        // check bootstrapping...
        assertThat(documentMenu).isNotNull();

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {

                // taken from the DocumentTypesAndTemplatesSeedService (not run in integ tests by default)
                final LocalDate templateDate = ld(2012,1,1);

                executionContext.executeChildren(this,
                        new DocumentTypesAndTemplatesForCapexFixture(templateDate),
                        new IncomingChargesFraXlsxFixture(),
                        new IncomingChargesItaXlsxFixture()
                        );

                executionContext.executeChildren(this,
                        Order_enum.italianOrder,
                        Person_enum.JonathanIncomingInvoiceManagerGb);
            }
        });

        orderTemplateDocumentType = DocumentTypeData.ORDER_CONFIRM.findUsing(documentTypeRepository);
    }


    @Test
    public void when_italian_order() throws Exception {

        // given
        order = Order_enum.italianOrder.findUsing(serviceRegistry);
        assertThat(order.getAtPath()).isEqualTo("/ITA");

        final DocumentType orderConfirmType = DocumentTypeData.ORDER_CONFIRM.findUsing(documentTypeRepository);
        final DocumentTemplate orderConfirmItaTemplate =
                documentTemplateRepository.findFirstByTypeAndApplicableToAtPath(orderConfirmType, order.getAtPath());
        assertThat(orderConfirmItaTemplate).isNotNull();

        // when
        final Object contentDataModel = orderConfirmItaTemplate.newRendererModel(order);

        // then
        assertThat(contentDataModel).isInstanceOf(RendererModelFactoryForOrder.DataModel.class);
        RendererModelFactoryForOrder.DataModel dataModel = (RendererModelFactoryForOrder.DataModel) contentDataModel;

        // and also...
        final RendererModelFactoryForOrder.SupplierModel supplierModel = dataModel.getSupplierModel();
        assertThat(supplierModel).isNotNull();
        assertThat(supplierModel.getAddress()).isEqualTo("??????????????");

        // and given
        final Document document = serviceRegistry.injectServicesInto(new Document(null, null, null, null, null));

        assertThat(document.getBlobBytes()).isNull();
        assertThat(document.getName()).isNull();

        // when
        orderConfirmItaTemplate.renderContent(document, contentDataModel);

        // then
        assertThat(document.getBlobBytes()).isNotNull();
        assertThat(document.getName()).isNotNull();

    }


    @Inject
    DocumentTypeRepository documentTypeRepository;
    @Inject
    PaperclipRepository paperclipRepository;

}
