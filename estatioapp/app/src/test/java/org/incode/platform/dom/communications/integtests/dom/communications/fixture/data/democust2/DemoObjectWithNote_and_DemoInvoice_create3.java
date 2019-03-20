package org.incode.platform.dom.communications.integtests.dom.communications.fixture.data.democust2;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.value.Blob;

import org.incode.module.base.dom.MimeTypes;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwner;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.dom.impl.State;
import org.incode.module.country.fixture.CountriesRefData;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.Document_attachSupportingPdf;
import org.incode.platform.dom.communications.integtests.demo.dom.demowithnotes.DemoObjectWithNotes;
import org.incode.platform.dom.communications.integtests.demo.dom.demowithnotes.DemoObjectWithNotesMenu;
import org.incode.platform.dom.communications.integtests.demo.dom.invoice.DemoInvoice;
import org.incode.platform.dom.communications.integtests.demo.dom.invoice.DemoInvoiceRepository;
import org.incode.platform.dom.communications.integtests.dom.communications.dom.apiimpl.DemoAppCommunicationChannelOwner_newChannelContributions;
import org.incode.platform.dom.communications.integtests.dom.communications.dom.invoice.DemoInvoice_simulateRenderAsDoc;

public class DemoObjectWithNote_and_DemoInvoice_create3 extends FixtureScript {

    public static final String FRED_HAS_EMAIL_AND_PHONE = "Fred HasEmailAndPhone";
    public static final String MARY_HAS_PHONE_AND_POST = "Mary HasPhoneAndPost";
    public static final String JOE_HAS_EMAIL_AND_POST = "Joe HasPostAndEmail";


    @Override
    protected void execute(final ExecutionContext executionContext) {

        final Country gbrCountry = countryRepository.findCountry(CountriesRefData.GBR);

        final DemoObjectWithNotes custA = wrap(demoCustomerMenu).createDemoObjectWithNotes(FRED_HAS_EMAIL_AND_PHONE);
        addEmailAddress(custA, "fred@gmail.com");
        addEmailAddress(custA, "freddy@msn.com");
        addPhoneOrFaxNumber(custA, CommunicationChannelType.PHONE_NUMBER, "555 1234");
        addPhoneOrFaxNumber(custA, CommunicationChannelType.FAX_NUMBER, "555 4321");

        final DemoInvoice custA_1 = demoInvoiceRepository.create("1", custA);
        attachReceipt(custA_1, "Sample4.PDF");

        final DemoInvoice custA_2 = demoInvoiceRepository.create("2", custA);
        attachReceipt(custA_2, "Sample5.PDF");


        final DemoObjectWithNotes custB = wrap(demoCustomerMenu).createDemoObjectWithNotes(MARY_HAS_PHONE_AND_POST);
        addPhoneOrFaxNumber(custB, CommunicationChannelType.PHONE_NUMBER, "777 0987");
        addPhoneOrFaxNumber(custB, CommunicationChannelType.FAX_NUMBER, "777 7890");
        addPostalAddress(custB, gbrCountry, null, "45", "High Street", null, "OX1 4BJ", "Oxford");
        addPostalAddress(custB, gbrCountry, null, "23", "Railway Road", null, "WN7 4AA", "Leigh");

        final DemoInvoice custB_1 = demoInvoiceRepository.create("1", custB);
        attachReceipt(custB_1, "xlsdemo1.pdf");

        final DemoInvoice custB_2 = demoInvoiceRepository.create("2", custB);
        attachReceipt(custB_2, "xlsdemo2.pdf");

        final DemoObjectWithNotes custC = wrap(demoCustomerMenu).createDemoObjectWithNotes(JOE_HAS_EMAIL_AND_POST);
        addEmailAddress(custC, "joe@yahoo.com");
        addEmailAddress(custC, "joey@friends.com");
        addPostalAddress(custC, gbrCountry, null, "5", "Witney Gardens", null, "WA4 5HT", "Warrington");
        addPostalAddress(custC, gbrCountry, null, "3", "St. Nicholas Street Road", null, "YO11 2HF", "Scarborough");

        final DemoInvoice custC_1 = demoInvoiceRepository.create("1", custC);
        attachReceipt(custC_1, "pptdemo1.pdf");

        final DemoInvoice custC_2 = demoInvoiceRepository.create("2", custC);
        attachReceipt(custC_2, "pptdemo2.pdf");
    }

    private Document attachReceipt(final DemoInvoice invoice, final String resourceName) {
        final Blob blob = loadPdf(resourceName);
        try {
            return wrap(mixin(DemoInvoice_simulateRenderAsDoc.class, invoice)).$$(blob, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Document attachPdf(final Document document, final String resourceName) {
        final Blob blob = loadPdf(resourceName);
        try {
            final Document_attachSupportingPdf attachPdf = mixin(Document_attachSupportingPdf.class, document);
            return wrap(attachPdf).exec(attachPdf.default0Exec(), blob, null, attachPdf.default3Exec());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Blob loadPdf(final String resourceName) {
        final byte[] bytes = loadResourceBytes(resourceName);
        return new Blob(resourceName, MimeTypes.APPLICATION_PDF, bytes);
    }

    private static byte[] loadResourceBytes(final String resourceName) {
        final URL templateUrl = Resources
                .getResource(DemoObjectWithNote_and_DemoInvoice_create3.class, resourceName);
        try {
            return Resources.toByteArray(templateUrl);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Unable to read resource URL '%s'", templateUrl));
        }
    }

    void addEmailAddress(final CommunicationChannelOwner cco, final String address) {
        wrap(demoAppCommunicationChannelOwner_newChannelContributions).newEmail(cco, CommunicationChannelType.EMAIL_ADDRESS, address);
    }

    void addPhoneOrFaxNumber(
            final CommunicationChannelOwner cco,
            final CommunicationChannelType type,
            final String number) {
        wrap(demoAppCommunicationChannelOwner_newChannelContributions).newPhoneOrFax(cco, type, number);
    }

    void addPostalAddress(
            final CommunicationChannelOwner cco,
            final Country country,
            final State state,
            final String addressLine1,
            final String addressLine2,
            final String addressLine3,
            final String postalCode,
            final String city) {
        wrap(demoAppCommunicationChannelOwner_newChannelContributions).newPostal(cco, CommunicationChannelType.POSTAL_ADDRESS, country, state, addressLine1, addressLine2, addressLine3, postalCode, city);
    }


    @Inject
    DemoObjectWithNotesMenu demoCustomerMenu;

    @Inject
    CountryRepository countryRepository;

    @Inject
    DemoInvoiceRepository demoInvoiceRepository;

    @Inject
    DemoAppCommunicationChannelOwner_newChannelContributions demoAppCommunicationChannelOwner_newChannelContributions;

}
