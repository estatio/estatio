package org.estatio.module.capex.app;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.value.Blob;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

public class DocumentMenu_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    DocumentBarcodeService mockDocumentBarcodeService;

    @Test
    public void overrideUserAtPathUsingDocumentName_works_when_overriding() throws Exception {

        // given
        DocumentMenu menu = new DocumentMenu();
        menu.documentBarcodeService = mockDocumentBarcodeService;
        String barcode = "6010012345.pdf";
        String userAtPath = "/FRA";

        // expect
        context.checking(new Expectations(){{
            allowing(mockDocumentBarcodeService).countryPrefixFromBarcode(barcode);
            will(returnValue("BE"));
        }});

        // when
        String overriddenAtPath = menu.overrideUserAtPathUsingDocumentName(userAtPath, barcode);

        // then
        Assertions.assertThat(overriddenAtPath).isEqualTo("/BEL");

    }

    @Test
    public void overrideUserAtPathUsingDocumentName_works_when_not_overriding() throws Exception {

        // given
        DocumentMenu menu = new DocumentMenu();
        menu.documentBarcodeService = mockDocumentBarcodeService;
        String barcode = "6010012345.pdf";
        String userAtPath = "/FRA";

        // expect
        context.checking(new Expectations(){{
            oneOf(mockDocumentBarcodeService).countryPrefixFromBarcode(barcode);
            will(returnValue(null));
        }});

        // when
        String overriddenAtPath = menu.overrideUserAtPathUsingDocumentName(userAtPath, barcode);

        // then
        Assertions.assertThat(overriddenAtPath).isEqualTo("/FRA");

    }

    @Test
    public void overrideUserAtPathUsingDocumentName_works_when_not_barcode() throws Exception {

        // given
        DocumentMenu menu = new DocumentMenu();
        String filename = "3625 GIG 679 226 - ASF srl.pdf";
        String userAtPath = "/ITA";

        // when
        String overriddenAtPath = menu.overrideUserAtPathUsingDocumentName(userAtPath, filename);

        // then
        Assertions.assertThat(overriddenAtPath).isEqualTo("/ITA");

    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void upload_generic_when_using_non_existing_doc_type_data() throws Exception {

        // given
        DocumentMenu menu = new DocumentMenu();
        // expect
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("No enum constant org.estatio.module.invoice.dom.DocumentTypeData.NON_EXISTING_TYPE");
        // when
        menu.uploadGeneric(null, "NON_EXISTING_TYPE", true, null);

    }


    @Mock DocumentTypeRepository mockDocumentTypeRepository;

    @Test
    public void upload_generic_when_not_supported() throws Exception {

        // given
        DocumentMenu menu = new DocumentMenu();
        menu.documentTypeRepository = mockDocumentTypeRepository;
        Blob blob = new Blob("some_name", "application/pdf", new byte[0]);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockDocumentTypeRepository).findByReference("INCOMING_ORDER");
        }});
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Combination documentType =  INCOMING_ORDER, barcodeInDocName = true and atPath = /ITA is not supported");
        // when
        menu.uploadGeneric(blob, "INCOMING_ORDER", true, "/ITA");

    }
}