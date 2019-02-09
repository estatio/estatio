package org.estatio.module.capex.dom.documents;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.module.capex.contributions.BankAccount_attachInvoiceAsIbanProof;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.invoice.dom.DocumentTypeData;

@DomainService(nature = NatureOfService.DOMAIN)
public class LookupAttachedPdfService {

    private static final String ROLE_NAME_FOR_IBAN_PROOF =
            BankAccount_attachInvoiceAsIbanProof.ROLE_NAME_FOR_IBAN_PROOF;

    @Programmatic
    public Optional<Document> lookupIncomingInvoicePdfFrom(final IncomingInvoice incomingInvoice) {
        return lookupPdfFrom(incomingInvoice, Lists.newArrayList(DocumentTypeData.INCOMING_INVOICE), null);
    }

    @Programmatic
    public List<Document> lookupIncomingInvoicePdfsFrom(final IncomingInvoice incomingInvoice) {
        return lookupPdfsFrom(incomingInvoice, Lists.newArrayList(DocumentTypeData.INCOMING_INVOICE), null);
    }

    @Programmatic
    public Optional<Document> lookupIbanProofPdfFrom(final BankAccount bankAccount) {
        return lookupPdfFrom(bankAccount, null, ROLE_NAME_FOR_IBAN_PROOF);
    }

    @Programmatic
    public List<Document> lookupIbanProofPdfsFrom(final BankAccount bankAccount) {
        return lookupPdfsFrom(bankAccount, null, ROLE_NAME_FOR_IBAN_PROOF);
    }

    @Programmatic
    public Optional<Document> lookupOrderPdfFrom(final Order order) {
        return lookupMostRecentPdfFrom(order, Lists.newArrayList(DocumentTypeData.INCOMING_ORDER, DocumentTypeData.ORDER_CONFIRM), null);
    }

    @Programmatic
    public List<Document> lookupOrderPdfsFrom(final Order order) {
        return lookupPdfsFrom(order, Lists.newArrayList(DocumentTypeData.INCOMING_ORDER, DocumentTypeData.ORDER_CONFIRM), null);
    }

    @Programmatic
    public Optional<Document> lookupMostRecentPdfFrom(
            final Object domainObject,
            final List<DocumentTypeData> documentTypeDatas,
            final String roleNameIfAny) {
        final List<Document> documents = lookupPdfsFrom(domainObject, documentTypeDatas, roleNameIfAny);
        return documents.isEmpty()
                ? Optional.empty()
                : Optional.of(
                documents.stream()
                        .sorted(Comparator.comparing(Document::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                        .collect(Collectors.toList())
                        .get(0));
    }

    @Programmatic
    public Optional<Document> lookupPdfFrom(
            final Object domainObject,
            final List<DocumentTypeData> documentTypeDatas,
            final String roleNameIfAny) {
        final List<Document> documents = lookupPdfsFrom(domainObject, documentTypeDatas, roleNameIfAny);
        return documents.isEmpty()
                ? Optional.empty()
                : Optional.of(documents.get(0));
    }

    @Programmatic
    public List<Document> lookupPdfsFrom(
            final Object domainObject,
            final List<DocumentTypeData> documentTypeDatas,
            final String roleNameIfAny) {
        return queryResultsCache.execute(
                () -> doLookupPdfsFrom(domainObject, documentTypeDatas, roleNameIfAny),
                LookupAttachedPdfService.class,
                "lookupPdfsFrom", domainObject, documentTypeDatas);
    }

    private List<Document> doLookupPdfsFrom(
            final Object domainObject,
            final List<DocumentTypeData> documentTypeDatas,
            final String roleNameIfAny) {
        final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(domainObject);
        return paperclips.stream()
                .filter(paperclip -> roleNameIfAny == null || roleNameIfAny.equals(paperclip.getRoleName()))
                .map(Paperclip::getDocument)
                .filter(Document.class::isInstance)
                .map(Document.class::cast)
                .filter(document -> {
                    if(documentTypeDatas == null) {
                        return true;
                    }
                    for (final DocumentTypeData documentTypeData : documentTypeDatas) {
                        if(documentTypeData.isDocTypeFor(document)) {
                            return true;
                        }
                    }
                    return false;
                })
                .filter(document -> Objects.equals(document.getMimeType(), "application/pdf"))
                .collect(Collectors.toList());
    }

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    PaperclipRepository paperclipRepository;

}
