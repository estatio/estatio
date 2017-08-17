package org.estatio.capex.dom.invoice.manager;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;

import org.estatio.capex.dom.coda.CodaElement;
import org.estatio.capex.dom.coda.CodaMapping;
import org.estatio.capex.dom.coda.CodaMappingRepository;
import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.state.StateTransitionRepositoryGeneric;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.capex.dom.invoice.manager.IncomingInvoiceDownloadManager")
@NoArgsConstructor
public class IncomingInvoiceDownloadManager {

    public String title() {
        return "Invoice Download";
    }

    public IncomingInvoiceDownloadManager(final LocalDate fromInputDate, final LocalDate toInputDate, final org.estatio.dom.asset.Property property, final IncomingInvoiceType incomingInvoiceType){
        this.fromInputDate = fromInputDate;
        this.toInputDate = toInputDate;
        this.propertyReference = property == null ? null : property.getReference();
        this.incomingInvoiceTypeName = incomingInvoiceType == null ? null : incomingInvoiceType.name();
    }

    @Getter @Setter
    private LocalDate fromInputDate;

    @Getter @Setter
    private LocalDate toInputDate;

    @Getter @Setter
    private String propertyReference;

    @Getter @Setter
    @PropertyLayout(named = "Incoming Invoice Type")
    private String incomingInvoiceTypeName;

    public Property getProperty(){
        return getPropertyReference() == null ? null : propertyRepository.findPropertyByReference(getPropertyReference());
    }

    IncomingInvoiceType getIncomingInvoiceType() {
        return getIncomingInvoiceTypeName() == null ? null : IncomingInvoiceType.valueOf(getIncomingInvoiceTypeName());
    }


    @CollectionLayout(defaultView = "table")
    public List<IncomingInvoice> getInvoices() {
        final Predicate<IncomingInvoice> excludeNew = x -> !x.getApprovalState().equals(IncomingInvoiceApprovalState.NEW);
        final Predicate<IncomingInvoice> excludeDiscarded = x -> !x.getApprovalState().equals(IncomingInvoiceApprovalState.DISCARDED);
        final Predicate<IncomingInvoice> filterType = getIncomingInvoiceType()!= null ? x -> x.getType().equals(getIncomingInvoiceType()) : x->true;
        return incomingInvoiceRepository.findByPropertyAndDateReceivedBetween(getProperty(), getFromInputDate(), getToInputDate())
                .stream()
                .filter(excludeNew)
                .filter(excludeDiscarded)
                .filter(filterType)
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<IncomingInvoiceItem> getInvoiceItems() {
        return getInvoices().stream()
                .flatMap(inv -> inv.getItems().stream())
                .map(invoiceItem -> (IncomingInvoiceItem) invoiceItem)
                .collect(Collectors.toList());
    }

    @Programmatic
    public IncomingInvoiceDownloadManager init() {
        return this;
    }


    CodaElement codaElementFor(final IncomingInvoiceItem x) {
        final List<CodaMapping> codaMappings = codaMappingRepository.findMatching(x.getIncomingInvoiceType(), x.getCharge());
        return codaMappings.size() == 0 ? null : codaMappings.get(0).getCodaElement();
    }

    String documentNumberFor(final IncomingInvoiceItem invoiceItem) {
        final IncomingInvoice invoice = (IncomingInvoice) invoiceItem.getInvoice();
        final Optional<Document> documentIfAny = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(invoice);
        return documentIfAny.map(DocumentAbstract::getName).orElse(null);
    }

    String commentsFor(final IncomingInvoiceItem invoiceItem){
        StringBuffer result = new StringBuffer();
        final IncomingInvoice invoice = (IncomingInvoice) invoiceItem.getInvoice();
        List<IncomingInvoiceApprovalStateTransition> transitions = stateTransitionRepositoryGeneric.findByDomainObject(invoice, IncomingInvoiceApprovalStateTransition.class);
        for (IncomingInvoiceApprovalStateTransition transition : transitions){
            if (transition.getTask()!=null && transition.getTask().getComment() !=null){
                result.append(transition.getTask().getComment());
                result.append(" | ");
            }
        }
        return result.toString();
    }


    final static Class exportClass = IncomingInvoiceExport.class;

    String defaultFileNameWithSuffix(final String suffix) {
        final String fileName = String.format("%s_%s_%s-%s",
                exportClass.getSimpleName(),
                getPropertyReference() == null ? "" : getPropertyReference(),
                getFromInputDate().toString("yyyyMMdd"),
                getToInputDate().toString("yyyyMMdd")
        );

        return fileName.concat(suffix);
    }



    @javax.inject.Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @javax.inject.Inject
    LookupAttachedPdfService lookupAttachedPdfService;

    @Inject
    CodaMappingRepository codaMappingRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    protected StateTransitionRepositoryGeneric stateTransitionRepositoryGeneric;



}
