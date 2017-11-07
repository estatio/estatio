package org.estatio.fixture.documents;

import java.lang.reflect.Field;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.config.ConfigurationService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.freemarker.dom.service.FreeMarkerService;

import org.incode.module.docrendering.freemarker.dom.impl.RendererForFreemarker;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.document.documents.binders.FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover;
import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.Occupancy;
import org.estatio.dom.lease.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.tags.Brand;
import org.estatio.module.party.dom.Organisation;

public class DocumentTypeAndTemplatesFSForInvoicesUsingSsrs_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    ConfigurationService mockConfigurationService;

    @Mock
    DocumentTemplate mockDocumentTemplate;

    @Mock
    DocumentType mockDocumentType;

    @Mock
    Document mockDocument;

    @Mock
    Lease mockLease;

    DocumentType stubDocumentType;
    InvoiceForLease stubInvoice;
    Organisation stubBuyer;
    Property stubProperty;
    Unit stubUnit;
    Brand stubBrand;

    @Mock
    PaperclipRepository mockPaperclipRepository;

    FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover rendererModelFactory;

    @Before
    public void setUp() throws Exception {
        stubDocumentType = DocumentTypeData.PRELIM_LETTER.create();

        stubInvoice = new InvoiceForLease();
        stubInvoice.setDueDate(new LocalDate(2016,11,1));
        stubBuyer = new Organisation();
        stubBuyer.setName("Buyer-1");

        stubInvoice.setBuyer(stubBuyer);
        stubInvoice.setLease(mockLease);

        stubProperty = new Property();
        stubProperty.setReference("XXX");

        stubUnit = new Unit();
        stubUnit.setName("XXX-123");

        stubBrand = new Brand();
        stubBrand.setName("Brandino");

        // expect
        context.checking(new Expectations() {{
            allowing(mockPaperclipRepository).paperclipAttaches(mockDocument, InvoiceForLease.class);
            will(returnValue(stubInvoice));

            allowing(mockLease).getProperty();
            will(returnValue(stubProperty));

            allowing(mockLease).getReference();
            will(returnValue("XXX-ABC-789"));

            allowing(mockDocument).getType();
            will(returnValue(stubDocumentType));
        }});

        // expecting
        context.checking(new Expectations() {{
            allowing(mockConfigurationService).getProperty("isis.deploymentType");
            will(returnValue("prototyping"));
        }});

        rendererModelFactory = new FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover();
        inject(rendererModelFactory, "paperclipRepository", mockPaperclipRepository);
    }

    private static void inject(
            final Object target,
            final String fieldName,
            final Object reference) throws NoSuchFieldException, IllegalAccessException {
        final Field delegateField = target.getClass().getDeclaredField(fieldName);
        delegateField.setAccessible(true);
        delegateField.set(target, reference);
    }

    @Test
    public void when_primary_occupancy() throws Exception {

        // given

        final Occupancy stubOccupancy = new Occupancy();
        stubOccupancy.setUnit(stubUnit);
        stubOccupancy.setBrand(stubBrand);

        context.checking(new Expectations() {{
            allowing(mockLease).primaryOccupancy();
            will(returnValue(Optional.of(stubOccupancy)));
        }});

        // when
        final Object rendererModel = rendererModelFactory.newRendererModel(mockDocumentTemplate, mockDocument);

        // given
        final RendererForFreemarker renderer = rendererForFreemarker();

        // when
        final String nameText = DocumentTypeAndTemplatesFSForInvoicesUsingSsrs.loadResource("InvoiceEmailCoverNoteSubjectLine.ftl");
        final String rendered = renderer.renderCharsToChars(stubDocumentType, "", "/", 0L, nameText, rendererModel);

        // then
        Assertions.assertThat(rendered).isEqualTo("Invoice 2016-11-01, XXX XXX-123 Buyer-1 Brandino");

    }

    @Test
    public void when_no_primary_occupancy() throws Exception {

        // given
        context.checking(new Expectations() {{
            allowing(mockLease).primaryOccupancy();
            will(returnValue(Optional.empty()));
        }});

        // when
        final Object rendererModel = rendererModelFactory.newRendererModel(mockDocumentTemplate, mockDocument);

        // given
        final RendererForFreemarker renderer = rendererForFreemarker();

        // when
        final String nameText = DocumentTypeAndTemplatesFSForInvoicesUsingSsrs.loadResource("InvoiceEmailCoverNoteSubjectLine.ftl");
        final String rendered = renderer.renderCharsToChars(stubDocumentType, "", "/", 0L, nameText, rendererModel);

        // then
        Assertions.assertThat(rendered).isEqualTo("Invoice 2016-11-01, XXX  Buyer-1 ");
    }

    private RendererForFreemarker rendererForFreemarker() throws NoSuchFieldException, IllegalAccessException {
        final FreeMarkerService freeMarkerService = new FreeMarkerService();
        final Field configurationServiceField = FreeMarkerService.class.getDeclaredField("configurationService");
        configurationServiceField.setAccessible(true);
        configurationServiceField.set(freeMarkerService, mockConfigurationService);
        freeMarkerService.init(ImmutableMap.of());

        final RendererForFreemarker renderer = new RendererForFreemarker();
        final Field freeMarkerServiceField = RendererForFreemarker.class.getDeclaredField("freeMarkerService");
        freeMarkerServiceField.setAccessible(true);
        freeMarkerServiceField.set(renderer, freeMarkerService);
        return renderer;
    }

}