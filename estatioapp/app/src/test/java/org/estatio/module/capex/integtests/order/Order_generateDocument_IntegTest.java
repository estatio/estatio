package org.estatio.module.capex.integtests.order;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.togglz.junit.TogglzRule;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.HiddenException;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.base.spiimpl.togglz.EstatioTogglzFeature;
import org.estatio.module.capex.app.DocumentMenu;
import org.estatio.module.capex.contributions.Order_generateDocument;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.fixtures.order.enums.Order_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesItaXlsxFixture;
import org.estatio.module.invoice.dom.DocumentTypeData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.incode.module.base.integtests.VT.ld;

public class Order_generateDocument_IntegTest extends CapexModuleIntegTestAbstract {

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Inject
    DocumentMenu documentMenu;

    DocumentType orderTemplateDocumentType;
    Order order;


    @Before
    public void setupData() {

        // check bootstrapping...
        Assertions.assertThat(documentMenu).isNotNull();

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
                        Order_enum.fakeOrder2Pdf,
                        Order_enum.italianOrder,
                        Person_enum.JonathanIncomingInvoiceManagerGb);
            }
        });

        orderTemplateDocumentType = DocumentTypeData.ORDER_CONFIRM.findUsing(documentTypeRepository);
    }


    @Test
    public void when_french_order() throws Exception {

        // given
        order = Order_enum.fakeOrder2Pdf.findUsing(serviceRegistry);
        assertThat(order.getAtPath()).isEqualTo("/FRA");

        final Order_generateDocument mixin = mixin(Order_generateDocument.class, order);

        // when
        final List<DocumentTemplate> documentTemplates = mixin.choices0Act();

        // then
        assertThat(documentTemplates).isEmpty();

        // expect
        expectedExceptions.expect(HiddenException.class);

        // when
        final DocumentTemplate anyTemplate =
                repositoryService.allInstances(DocumentTemplate.class).stream().findFirst().orElse(null);
        mixin.act(anyTemplate);

    }


    @Test
    public void when_italian_order() throws Exception {

        // given
        order = Order_enum.italianOrder.findUsing(serviceRegistry);
        assertThat(order.getAtPath()).isEqualTo("/ITA");

        final Order_generateDocument mixin = mixin(Order_generateDocument.class, order);

        final List<DocumentTemplate> documentTemplates = mixin.choices0Act();
        assertThat(documentTemplates).hasSize(1);

        final DocumentTemplate documentTemplate = documentTemplates.get(0);
        assertThat(documentTemplate.getType()).isEqualTo(orderTemplateDocumentType);

        final List<Paperclip> paperclipsBefore = paperclipRepository.listAll();
        assertThat(paperclipsBefore).hasSize(1); // PDF linked to Order

        // when
        final Order returnedOrder = (Order) mixin.act(documentTemplate);

        // then
        assertThat(returnedOrder).isSameAs(order);

        final List<Paperclip> paperclipsAfter = paperclipRepository.listAll();
        assertThat(paperclipsAfter).hasSize(paperclipsBefore.size() + 1);
        final Paperclip paperclip =
                paperclipsAfter.stream().filter(x -> !paperclipsBefore.contains(x)).findFirst().orElse(null);
        assertThat(paperclip).isNotNull();
        assertThat(paperclip.getAttachedTo()).isSameAs(order);
        final DocumentAbstract document = paperclip.getDocument();
        final Blob blob = document.getBlob();
        assertThat(blob).isNotNull();

        final String roleName = paperclip.getRoleName();
        assertThat(roleName).isNull();
    }


    @Inject
    DocumentTypeRepository documentTypeRepository;
    @Inject
    PaperclipRepository paperclipRepository;

}
