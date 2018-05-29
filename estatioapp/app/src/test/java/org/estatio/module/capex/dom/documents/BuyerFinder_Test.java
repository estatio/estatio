package org.estatio.module.capex.dom.documents;

import java.util.Optional;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.party.dom.PartyRepository;

public class BuyerFinder_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    LookupAttachedPdfService mockLookupAttachedPdfService;
    @Mock
    PartyRepository mockPartyRepository;

    @Test
    public void buyerDerivedFromDocumentName_works() throws Exception {

        testWhenParamIsInvoice("301xxxxxxx.pdf", "FR01");
        testWhenParamIsInvoice("302xxxxxxx.pdf", "FR02");
        testWhenParamIsInvoice("303xxxxxxx.pdf", "FR03");
        testWhenParamIsInvoice("304xxxxxxx.pdf", "FR04");
        testWhenParamIsInvoice("305xxxxxxx.pdf", "FR05");
        testWhenParamIsInvoice("307xxxxxxx.pdf", "FR07");
        testWhenParamIsInvoice("6010xxxxxx.pdf", "BE00");

        testWhenParamIsDocument("301xxxxxxx.pdf", "FR01");
        testWhenParamIsDocument("302xxxxxxx.pdf", "FR02");
        testWhenParamIsDocument("303xxxxxxx.pdf", "FR03");
        testWhenParamIsDocument("304xxxxxxx.pdf", "FR04");
        testWhenParamIsDocument("305xxxxxxx.pdf", "FR05");
        testWhenParamIsDocument("307xxxxxxx.pdf", "FR07");
        testWhenParamIsDocument("6010xxxxxx.pdf", "BE00");

        testerWhenNullOrTooShort(null);
        testerWhenNullOrTooShort("12");

    }

    private void testWhenParamIsInvoice(final String documentName, final String derivedPartyReference){
        // given
        IncomingInvoice invoice = new IncomingInvoice();
        BuyerFinder finder = new BuyerFinder();
        finder.partyRepository = mockPartyRepository;
        finder.lookupAttachedPdfService = mockLookupAttachedPdfService;
        Optional<Document> optional = Optional.of(new Document(null,null, documentName, null, null));

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLookupAttachedPdfService).lookupIncomingInvoicePdfFrom(invoice);
            will(returnValue(optional));
            oneOf(mockPartyRepository).findPartyByReference(derivedPartyReference);
        }});

        // when
        finder.buyerDerivedFromDocumentName(invoice);
    }

    private void testWhenParamIsDocument(final String documentName, final String derivedPartyReference){
        // given
        Document document = new Document(null, null, documentName, null, null);
        BuyerFinder finder = new BuyerFinder();
        finder.partyRepository = mockPartyRepository;

        // expect
        context.checking(new Expectations(){{
            oneOf(mockPartyRepository).findPartyByReference(derivedPartyReference);
        }});

        // when
        finder.buyerDerivedFromDocumentName(document);
    }

    private void testerWhenNullOrTooShort(final String documentName){
        // given
        IncomingInvoice invoice = new IncomingInvoice();
        BuyerFinder finder = new BuyerFinder();
        finder.partyRepository = mockPartyRepository;
        finder.lookupAttachedPdfService = mockLookupAttachedPdfService;
        Optional<Document> optional = Optional.of(new Document(null,null, documentName, null, null));

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLookupAttachedPdfService).lookupIncomingInvoicePdfFrom(invoice);
            will(returnValue(optional));
        }});

        // when
        finder.buyerDerivedFromDocumentName(invoice);
    }

}