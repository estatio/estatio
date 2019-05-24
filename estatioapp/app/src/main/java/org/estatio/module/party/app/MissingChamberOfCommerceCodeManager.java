package org.estatio.module.party.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.party.app.services.ChamberOfCommerceCodeLookUpService;
import org.estatio.module.party.app.services.OrganisationNameNumberViewModel;
import org.estatio.module.party.dom.Organisation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(objectType = "party.MissingChamberOfCommerceCodeManager")
@XmlRootElement(name = "missingChamberOfCommerceCodeViewModel")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public class MissingChamberOfCommerceCodeManager {

    public MissingChamberOfCommerceCodeManager(final List<Organisation> remainingOrganisations) {
        this.remainingOrganisations = remainingOrganisations;
        this.supplier = this.remainingOrganisations.remove(0);
    }

    @Getter @Setter
    private Organisation supplier;

    @Getter @Setter
    private String chamberOfCommerceCode;

    @Getter @Setter
    public List<Organisation> remainingOrganisations = new ArrayList<>();

    @Getter @Setter
    public List<Organisation> skippedOrganisations = new ArrayList<>();

    @Action(associateWith = "candidateCodes", semantics = SemanticsOf.IDEMPOTENT)
    public MissingChamberOfCommerceCodeManager chooseChamberOfCommerceCode(final List<OrganisationNameNumberViewModel> choice) {
        setChamberOfCommerceCode(choice.get(0).getChamberOfCommerceCode());
        return this;
    }

    public String validateChooseChamberOfCommerceCode(final List<OrganisationNameNumberViewModel> choice) {
        return choice.size() != 1 ?
                "Must select exactly one option from the list of suggestions" :
                null;
    }

    public List<OrganisationNameNumberViewModel> getCandidateCodes() {
        return lookUpService.getChamberOfCommerceCodeCandidatesByOrganisation(supplier);
    }

    public Document getNewestInvoice() {
        Optional<IncomingInvoice> invoiceIfAny = incomingInvoiceRepository.findBySellerAndApprovalStates(supplier, Arrays.asList(IncomingInvoiceApprovalState.values()))
                .stream()
                .max(Comparator.comparing(IncomingInvoice::getInvoiceDate));

        Optional<Document> documentIfAny = invoiceIfAny.isPresent() ?
                pdfService.lookupIncomingInvoicePdfFrom(invoiceIfAny.get()) :
                Optional.empty();

        return documentIfAny.orElse(null);
    }

    @XmlTransient
    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @XmlTransient
    @Inject
    LookupAttachedPdfService pdfService;

    @XmlTransient
    @Inject
    ChamberOfCommerceCodeLookUpService lookUpService;

}
