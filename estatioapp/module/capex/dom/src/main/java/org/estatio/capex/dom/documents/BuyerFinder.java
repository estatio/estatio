package org.estatio.capex.dom.documents;

import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class BuyerFinder {

    @Programmatic
    public Party buyerDerivedFromDocumentName(final IncomingInvoice incomingInvoice){
        if (incomingInvoice==null) return null;

        final StringBuffer buffer = new StringBuffer();
        final Optional<Document> document = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(incomingInvoice);
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

        String partyReference = null;
        switch (barcodeSerie){
        case "301":
            partyReference = "FR01";
            break;
        case "302":
            partyReference = "FR02";
            break;
        case "303":
            partyReference = "FR03";
            break;
        case "304":
            partyReference = "FRCL10317";
            break;
        case "305":
            partyReference = "FR05";
            break;

        // TODO: 306 serie no party reference found for SCI Winter in Estatio .. ()

        case "307":
            partyReference = "FR07";
            break;
        default:
            // nothing
        }
        return partyReference!=null ? partyRepository.findPartyByReference(partyReference) : null;
    }

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;

    @Inject
    PartyRepository partyRepository;

}
