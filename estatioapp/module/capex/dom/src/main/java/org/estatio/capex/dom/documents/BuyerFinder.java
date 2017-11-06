package org.estatio.capex.dom.documents;

import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.order.Order;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class BuyerFinder {

    @Programmatic
    public Party buyerDerivedFromDocumentName(final Order order){
        if (order==null) return null;

        final Optional<Document> document = lookupAttachedPdfService.lookupOrderPdfFrom(order);
        return buyerDerivedFrom(document);
    }

    @Programmatic
    public Party buyerDerivedFromDocumentName(final IncomingInvoice incomingInvoice){
        if (incomingInvoice==null) return null;

        final Optional<Document> document = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(incomingInvoice);
        return buyerDerivedFrom(document);
    }

    private Party buyerDerivedFrom(final Optional<Document> document) {
        final StringBuffer buffer = new StringBuffer();
        document.ifPresent(d->buffer.append(d.getName()));
        String barcodeSerie = buffer.length()>3 ? buffer.substring(0,3).toString() : null;
        return partyDerivedFromBarcodeSerie(barcodeSerie);
    }

    @Programmatic
    public Party buyerDerivedFromDocumentName(final Document document){
        if (document == null || document.getName()==null || document.getName().length()<4){
            return null;
        }

        String barcodeSerie = document.getName().substring(0,3);
        return partyDerivedFromBarcodeSerie(barcodeSerie);
    }

    private Party partyDerivedFromBarcodeSerie(final String barcodeSerie){
        if (barcodeSerie==null) return null;

        String countryPrefix;
        String firstChar = barcodeSerie.substring(0,1);
        switch (firstChar){
        case "1":
            countryPrefix = "NL";
            break;
        case "2":
            countryPrefix = "IT";
            break;
        case "3":
            countryPrefix = "FR";
            break;
        case "4":
            countryPrefix = "SE";
            break;
        case "5":
            countryPrefix = "GB";
            break;
        default:
            return null;
        }
        String partyReference = countryPrefix.concat(barcodeSerie.substring(1,3));
        return partyRepository.findPartyByReference(partyReference);
    }

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;

    @Inject
    PartyRepository partyRepository;

}
