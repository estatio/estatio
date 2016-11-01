package org.estatio.dom.documents.binders;

import java.lang.reflect.Field;
import java.util.Optional;

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
import org.incode.module.document.dom.impl.applicability.Binder;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.party.Organisation;
import org.estatio.fixture.documents.DocumentTypeAndTemplatesFSForInvoicesUsingSsrs;

public class DocumentTypeAndTemplatesFSForInvoicesUsingSsrsTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    ConfigurationService mockConfigurationService;

    @Mock
    DocumentTemplate mockDocumentTemplate;

    @Mock
    Document mockDocument;

    @Mock
    Lease mockLease;

    DocumentType stubDocumentType;
    Invoice stubInvoice;
    Organisation stubBuyer;
    Property stubProperty;
    Unit stubUnit;
    Brand stubBrand;

    @Mock
    PaperclipRepository mockPaperclipRepository;

    BinderForDocumentAttachedToPrelimLetterOrInvoice binder;

    @Before
    public void setUp() throws Exception {
        stubDocumentType = new DocumentType("DT", "Some document type");

        stubInvoice = new Invoice();
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
            allowing(mockPaperclipRepository).paperclipAttaches(mockDocument, Invoice.class);
            will(returnValue(stubInvoice));

            allowing(mockLease).getProperty();
            will(returnValue(stubProperty));

            allowing(mockLease).getReference();
            will(returnValue("XXX-ABC-789"));
        }});

        // expecting
        context.checking(new Expectations() {{
            allowing(mockConfigurationService).getProperty("isis.deploymentType");
            will(returnValue("prototyping"));
        }});

        binder = new BinderForDocumentAttachedToPrelimLetterOrInvoice();
        binder.paperclipRepository = mockPaperclipRepository;
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
        final Binder.Binding binding = binder.newBinding(mockDocumentTemplate, mockDocument, "some additional text");

        // then
        final Object dataModel = binding.getDataModel();

        // given
        final RendererForFreemarker renderer = rendererForFreemarker();

        // when
        final String nameText = DocumentTypeAndTemplatesFSForInvoicesUsingSsrs.NAME_TEXT_INVOICE_GLOBAL;
        final String rendered = renderer.renderCharsToChars(stubDocumentType, "", "/", 0L, nameText, dataModel);

        // then
        Assertions.assertThat(rendered).isEqualTo("XXX Buyer-1 XXX-123 Brandino Invoice 2016-11-01");

    }

    @Test
    public void when_no_primary_occupancy() throws Exception {

        // given
        context.checking(new Expectations() {{
            allowing(mockLease).primaryOccupancy();
            will(returnValue(Optional.empty()));
        }});

        // when
        final Binder.Binding binding = binder.newBinding(mockDocumentTemplate, mockDocument, "some additional text");

        // then
        final Object dataModel = binding.getDataModel();

        // given
        final RendererForFreemarker renderer = rendererForFreemarker();

        // when
        final String nameText = DocumentTypeAndTemplatesFSForInvoicesUsingSsrs.NAME_TEXT_INVOICE_GLOBAL;
        final String rendered = renderer.renderCharsToChars(stubDocumentType, "", "/", 0L, nameText, dataModel);

        // then
        Assertions.assertThat(rendered).isEqualTo("XXX Buyer-1   Invoice 2016-11-01");
    }

    private RendererForFreemarker rendererForFreemarker() throws NoSuchFieldException, IllegalAccessException {
        final FreeMarkerService freeMarkerService = new FreeMarkerService();
        final Field configurationServiceField = FreeMarkerService.class.getDeclaredField("configurationService");
        configurationServiceField.setAccessible(true);
        configurationServiceField.set(freeMarkerService, mockConfigurationService);
        freeMarkerService.init();

        final RendererForFreemarker renderer = new RendererForFreemarker();
        final Field freeMarkerServiceField = RendererForFreemarker.class.getDeclaredField("freeMarkerService");
        freeMarkerServiceField.setAccessible(true);
        freeMarkerServiceField.set(renderer, freeMarkerService);
        return renderer;
    }

}