package org.estatio.module.application.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.wicketstuff.pdfjs.Scale;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.wicket.pdfjs.cpt.applib.PdfJsViewer;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;

import org.estatio.module.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.party.app.services.ChamberOfCommerceCodeLookUpService;
import org.estatio.module.party.app.services.OrganisationNameNumberViewModel;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.dom.role.PartyRoleType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(objectType = "party.MissingChamberOfCommerceCodeManager")
@DomainObjectLayout(cssClassFa = "fa-wrench")
@XmlRootElement(name = "missingChamberOfCommerceCodeViewModel")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public class MissingChamberOfCommerceCodeManager {

    public String title() {
        return "Fix missing Chamber of Commerce codes";
    }

    public MissingChamberOfCommerceCodeManager(final List<Organisation> remainingOrganisations) {
        this.remainingOrganisations = remainingOrganisations;
        this.organisation = this.remainingOrganisations.remove(0);
    }

    @Getter @Setter
    private Organisation organisation;

    public String getRoles() {
        return organisation.getRoles().stream()
                .map(PartyRole::getRoleType)
                .filter(type -> type.getKey().equals(LeaseAgreementRoleTypeEnum.TENANT.getKey()) || type.getKey().equals(IncomingInvoiceRoleTypeEnum.SUPPLIER.getKey()))
                .map(PartyRoleType::getTitle)
                .collect(Collectors.joining(", "));
    }

    @Getter @Setter
    @Property(editing = Editing.ENABLED)
    private String chamberOfCommerceCode;

    @Getter @Setter
    public List<Organisation> remainingOrganisations = new ArrayList<>();

    @Getter @Setter
    public List<Organisation> skippedOrganisations = new ArrayList<>();

    @Getter @Setter
    @CollectionLayout(named = "No suggestions and no invoice")
    public List<Organisation> noSuggestions = new ArrayList<>();

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
        return lookUpService.getChamberOfCommerceCodeCandidatesByOrganisation(organisation);
    }

    @PdfJsViewer(initialPageNum = 1, initialScale = Scale.PAGE_WIDTH, initialHeight = 1500)
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Blob getNewestInvoice() {
        Optional<IncomingInvoice> invoiceIfAny = incomingInvoiceRepository.findBySellerAndApprovalStates(organisation, Arrays.asList(IncomingInvoiceApprovalState.values()))
                .stream()
                .max(Comparator.comparing(IncomingInvoice::getInvoiceDate, Comparator.nullsFirst(Comparator.naturalOrder())));

        Optional<Document> documentIfAny = invoiceIfAny.isPresent() ?
                pdfService.lookupIncomingInvoicePdfFrom(invoiceIfAny.get()) :
                Optional.empty();

        return documentIfAny.map(DocumentAbstract::getBlob).orElse(null);
    }

    public MissingChamberOfCommerceCodeManager save() {
        this.organisation.setChamberOfCommerceCode(getChamberOfCommerceCode());

        prepareForNextOrganisation();

        return this;
    }

    public String disableSave() {
        return chamberOfCommerceCode == null ? "Chamber of Commerce code is required to save" : null;
    }

    public MissingChamberOfCommerceCodeManager skip() {
        this.skippedOrganisations.add(this.organisation);

        prepareForNextOrganisation();

        return this;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public MissingChamberOfCommerceCodeManager skipToSpecificOrganisation(final Organisation organisation) {
        final List<Organisation> toBeSkipped = new ArrayList<>();

        for (final Organisation current : remainingOrganisations) {
            if (!current.equals(organisation)) {
                toBeSkipped.add(current);
            } else {
                this.organisation = current;
                this.chamberOfCommerceCode = null;
                break;
            }
        }

        skippedOrganisations.addAll(toBeSkipped);
        remainingOrganisations.removeAll(toBeSkipped);

        return this;
    }

    public List<Organisation> choices0SkipToSpecificOrganisation() {
        return remainingOrganisations;
    }

    private void prepareForNextOrganisation() {
        if (organisation == null)
            return; // we hit the end of the recursive cycle

        this.organisation = this.remainingOrganisations.isEmpty() ? null : this.remainingOrganisations.remove(0);
        this.chamberOfCommerceCode = null;

        // not very helpful to the user, skip to next in line
        if (getNewestInvoice() == null && getCandidateCodes().isEmpty()) {
            this.noSuggestions.add(this.organisation);
            prepareForNextOrganisation();
        }
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
