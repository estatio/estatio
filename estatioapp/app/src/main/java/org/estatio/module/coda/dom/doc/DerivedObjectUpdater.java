package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.services.wrapper.DisabledException;
import org.apache.isis.applib.services.wrapper.HiddenException;
import org.apache.isis.applib.services.wrapper.InvalidException;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.orderinvoice.IncomingInvoiceItem_createOrderItemLink;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.capex.dom.orderinvoice.OrderItem_removeInvoiceItemLink;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.dom.util.PeriodUtil;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.docflow.dom.DocFlowZipRepository;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Party;
import org.estatio.module.tax.dom.Tax;

import static org.estatio.module.docflow.dom.DocFlowZipService.PAPERCLIP_ROLE_NAME_GENERATED;

@DomainService(nature = NatureOfService.DOMAIN)
public class DerivedObjectUpdater {

    static final String AT_PATH = "/ITA";
    static final Tax NULL_TAX = null;
    static final BudgetItem NULL_BUDGET_ITEM = null;

    public IncomingInvoice upsertIncomingInvoice(
            final CodaDocHead docHead,
            final Existing existing,
            final boolean createIfDoesNotExist) {


        //
        // Update any existing objects based on this new CodaDocHead.  Note that it's possible that this
        // CodaDocHead is invalid even if a previous one was valid.  We simply align the Estatio objects
        // (if there are any) with whatever CodaDocHead says).
        //
        final Party buyer = docHead.getCmpCodeBuyer();

        final Party seller = docHead.getSummaryLineAccountCodeEl6Supplier(LineCache.DEFAULT);
        final IncomingInvoiceType firstAnalysisLineType = docHead.getFirstAnalysisLineIncomingInvoiceType(LineCache.DEFAULT);
        final String invoiceNumber = docHead.getSummaryLineExtRef2(LineCache.DEFAULT);
        final Property property = docHead.getSummaryLineAccountEl3Property(LineCache.DEFAULT);

        final BankAccount bankAccount = docHead.getSummaryLineSupplierBankAccount(LineCache.DEFAULT);
        final PaymentMethod paymentMethod = docHead.getSummaryLinePaymentMethod(LineCache.DEFAULT);
        final LocalDate vatRegistrationDate = docHead.getSummaryLineValueDate(LineCache.DEFAULT);
        final LocalDate dateReceived = docHead.getInputDate();
        final LocalDate invoiceDate = docHead.getDocDate();
        final LocalDate summaryLineDueDate = docHead.getSummaryLineDueDate(LineCache.DEFAULT);

        final LocalDate paidDate = docHead.getStatPayPaidDate();

        final InvoiceStatus invoiceStatus = InvoiceStatus.NEW; // we don't care, this is for outgoing invoices.


        final String period = Util.asFinancialYear(docHead.getCodaPeriod());
        final boolean postedToCodaBooks = Objects.equals(docHead.getLocation(), "books");

        final IncomingInvoice existingInvoiceIfAny = existing.getIncomingInvoiceIfAny();


        final BigDecimal summaryGrossAmount = docHead.getSummaryLineDocValue(LineCache.DEFAULT);
        final BigDecimal summaryVatAmount = elseZero(docHead.getSummaryLineDocSumTax(LineCache.DEFAULT));
        final BigDecimal summaryNetAmount = Util.subtract(summaryGrossAmount, summaryVatAmount);


        //
        // update the incoming invoice (we simply blindly follow Coda, since Coda always leads.
        //
        final IncomingInvoice incomingInvoice;
        if (existingInvoiceIfAny != null) {

            incomingInvoice = existingInvoiceIfAny;

            final IncomingInvoiceType incomingInvoiceType = firstAnalysisLineType;
            final LocalDate dueDate = summaryLineDueDate;

            incomingInvoiceRepository.updateInvoice(
                    incomingInvoice,
                    incomingInvoiceType, invoiceNumber, property, AT_PATH, buyer, seller,
                    invoiceDate, dueDate, vatRegistrationDate, paymentMethod, invoiceStatus, dateReceived, bankAccount,
                    postedToCodaBooks, paidDate
            );

            incomingInvoice.setGrossAmount(summaryGrossAmount);
            incomingInvoice.setNetAmount(summaryNetAmount);

            //
            // also update the existing items
            //
            final Map<Integer, IncomingInvoiceItem> existingItems = existing.getItemByLineNumberIfAny();
            final Map<Integer, Charge> existingCharges = existing.getChargeByLineNumberIfAny();
            final Map<Integer, Project> existingProjects = existing.getProjectByLineNumberIfAny();

            for (final CodaDocLine docLine : docHead.getAnalysisLines()) {
                final int lineNum = docLine.getLineNum();

                final IncomingInvoiceItem invoiceItemIfAny = existingItems.get(lineNum);

                if(invoiceItemIfAny != null) {

                    final IncomingInvoiceItem invoiceItem = invoiceItemIfAny;

                    // only overwrite charge if value hasn't been modified in Estatio since originally sync'd
                    final Charge existingChargeIfAny = existingCharges.get(lineNum);
                    final Charge replacementCharge = docHead.getAnalysisChargeByLineNumber().get(lineNum);
                    if(existingChargeIfAny == invoiceItem.getCharge()) {
                        invoiceItem.setCharge(replacementCharge);
                    }

                    // only overwrite project if value hasn't been modified in Estatio since originally sync'd
                    final Project existingProjectIfAny = existingProjects.get(lineNum);
                    final Project replacementProject = docHead.getAnalysisProjectByLineNumber().get(lineNum);
                    if(existingProjectIfAny == invoiceItem.getProject()) {
                        invoiceItem.setProject(replacementProject);
                    }

                    final LocalDateInterval ldi = PeriodUtil.yearFromPeriod(period);

                    invoiceItem.setIncomingInvoiceType(docLine.getIncomingInvoiceType());

                    final BigDecimal grossAmount = elseZero(docLine.getDocValue());
                    final BigDecimal vatAmount = elseZero(docLine.getDocSumTax());
                    final BigDecimal netAmount = Util.subtract(grossAmount, vatAmount);

                    invoiceItem.setDescription(docLine.getDescription());
                    invoiceItem.setNetAmount(netAmount);
                    invoiceItem.setVatAmount(vatAmount);
                    invoiceItem.setGrossAmount(grossAmount);
                    invoiceItem.setTax(NULL_TAX);
                    invoiceItem.setDueDate(docLine.getDueDate());
                    invoiceItem.setStartDate(ldi.startDate());
                    invoiceItem.setEndDate(ldi.endDate());
                    invoiceItem.setFixedAsset(property);

                    // we never call invoiceItem.setBudgetItem(...) - maintained exclusively in Estatio
                }
            }


            // edge condition: if Coda somehow now has fewer lines than it did previously, then zero out those surplus items.
            final int numLines = docHead.getAnalysisLines().size();
            final int numItems = existingItems.size();
            if(numItems > numLines) {
                for (int n = numLines; n < numItems; n++) {
                    final IncomingInvoiceItem invoiceItem = existingItems.get(n);

                    invoiceItem.setDescription("<no longer exists in Coda>");
                    invoiceItem.setNetAmount(BigDecimal.ZERO);
                    invoiceItem.setVatAmount(BigDecimal.ZERO);
                    invoiceItem.setGrossAmount(BigDecimal.ZERO);
                }
            }


        } else {

            // if the DocHead is valid, and sync has been requested, then we create new Estatio objects
            if (createIfDoesNotExist) {

                // as a side-effect, the approvalState will be set to NEW
                // (subscriber on ObjectPersist)

                final IncomingInvoiceType incomingInvoiceType = firstAnalysisLineType;
                final IncomingInvoiceApprovalState approvalState = null;

                incomingInvoice =
                        incomingInvoiceRepository.create(
                                incomingInvoiceType, invoiceNumber, property, AT_PATH, buyer, seller,
                                invoiceDate, summaryLineDueDate, vatRegistrationDate, paymentMethod,
                                invoiceStatus, dateReceived, bankAccount, approvalState, postedToCodaBooks, paidDate);

                incomingInvoice.setGrossAmount(summaryGrossAmount);
                incomingInvoice.setNetAmount(summaryNetAmount);

                for (final CodaDocLine docLine : docHead.getAnalysisLines()) {

                    final BigDecimal grossAmount = elseZero(docLine.getDocValue());
                    final BigDecimal vatAmount = elseZero(docLine.getDocSumTax());
                    final BigDecimal netAmount = Util.subtract(grossAmount, vatAmount);
                    final LocalDate dueDate = docLine.getDueDate();

                    incomingInvoice.addItem(
                            docLine.getIncomingInvoiceType(),
                            docLine.getExtRefWorkTypeCharge(),
                            docLine.getDescription(),
                            netAmount, vatAmount, grossAmount, NULL_TAX,
                            dueDate, period, property, docLine.getExtRefProject(), NULL_BUDGET_ITEM);
                }

            } else {
                // nothing to do; this CodaDocHead isn't valid and there isn't an IncomingInvoice to update.
                incomingInvoice = null;
            }
        }


        return incomingInvoice;
    }


    void updateLinkToOrderItem(
            final CodaDocHead docHead,
            final Existing existing,
            final boolean createIfDoesNotExist,
            final ErrorSet softErrors) {

        final OrderItemInvoiceItemLink existingLinkIfAny = existing.getOrderItemLinkIfAny();

        final IncomingInvoice incomingInvoice = derivedObjectLookup.invoiceIfAnyFrom(docHead);
        final IncomingInvoiceItem invoiceItem = IncomingInvoice.firstItemOf(incomingInvoice);

        final BigDecimal grossAmount = docHead.getSummaryLineDocValue(LineCache.DEFAULT);
        final BigDecimal vatAmount = docHead.getSummaryLineDocSumTax(LineCache.DEFAULT);
        final BigDecimal netAmount = Util.subtract(grossAmount, vatAmount);

        final OrderItem orderItem = docHead.getSummaryLineExtRefOrderItem(LineCache.DEFAULT);
        if (existingLinkIfAny != null) {

            if (orderItem == null || invoiceItem == null) {

                // the CodaDocHead no longer identifies either an order item or invoice item,
                // so we just discard the link
                linkRepository.removeLink(existingLinkIfAny);

            } else if (
                    existingLinkIfAny.getOrderItem() != orderItem ||
                            existingLinkIfAny.getInvoiceItem() != invoiceItem) {

                // we remove the existing link...
                final boolean removedLink =
                        removeLinkIfPossible(existingLinkIfAny.getOrderItem(), existingLinkIfAny.getInvoiceItem(), softErrors);

                // ... and recreate, because
                if (removedLink) {
                    linkIfPossible(orderItem, invoiceItem, softErrors);
                }

            } else {

                // nothing to do; make sure the netAmount is updated.
                existingLinkIfAny.setNetAmount(netAmount);
            }

        } else {

            // if the DocHead is valid, and sync has been requested, then we create new Estatio objects
            if (createIfDoesNotExist) {

                //
                // create the link, if we can
                //
                linkIfPossible(orderItem, invoiceItem, softErrors);
            } else {
                // nothing to do.
                // There was no link previously, and the current doc head is invalid so don't create one yet.
            }
        }
    }

    boolean removeLinkIfPossible(
            final OrderItem orderItem,
            final IncomingInvoiceItem invoiceItem,
            final ErrorSet softErrors) {

        if (orderItem != null && invoiceItem != null) {
            try {
                wrapperFactory.wrap(
                        factoryService.mixin(OrderItem_removeInvoiceItemLink.class, orderItem))
                        .act(invoiceItem);
            } catch (HiddenException ex) {
                softErrors.add(
                        "Failed to remove existing link between '%s' and '%s'",
                        titleService.titleOf(orderItem), titleService.titleOf(invoiceItem));
                return false;
            } catch (DisabledException | InvalidException ex) {
                softErrors.add(ex.getMessage());
                return false;
            }
        }
        return true;
    }

    void linkIfPossible(
            final OrderItem orderItem,
            final IncomingInvoiceItem invoiceItem,
            final ErrorSet softErrors) {

        if (orderItem != null && invoiceItem != null) {
            try {
                factoryService.mixin(IncomingInvoiceItem_createOrderItemLink.class, invoiceItem).act(orderItem, invoiceItem.getNetAmount());
            } catch (HiddenException ex) {
                softErrors.add(
                        "Failed to create a link between '%s' and '%s'",
                        titleService.titleOf(orderItem), titleService.titleOf(invoiceItem));
            } catch (DisabledException | InvalidException ex) {
                softErrors.add(ex.getMessage());
            }
        }
    }

    /**
     * attach paperclip to Document named after 'userref1', if exists.
     */
    void updatePaperclip(
            final CodaDocHead docHead,
            final boolean createIfDoesNotExist,
            final ErrorSet softErrors) {

        final Paperclip paperclipIfAny = derivedObjectLookup.paperclipIfAnyFrom(docHead);
        final String documentNameIfAny = derivedObjectLookup.documentNameIfAnyFrom(docHead);

        updatePaperclip(docHead, paperclipIfAny, documentNameIfAny, createIfDoesNotExist, softErrors);
    }

    /**
     * Attach paperclip to Document based on the value of 'userref1', if exists.
     *
     * If prefixed with 'S', then we strip and treat the remainder as a (numeric) SDI ID, and attach to the
     * (generated PDF of the) corresponding {@link org.estatio.module.docflow.dom.DocFlowZip} (if it exists).
     *
     * If no prefix, then we append '.pdf' and search for an uploaded {@link Document} with that name.
     *
     */
    public void updatePaperclip(
            final CodaDocHead docHead,
            final Paperclip existingPaperclipIfAny,
            final String existingDocumentNameIfAny,
            final boolean createIfDoesNotExist,
            final ErrorSet softErrors) {

        final DocumentType incomingDocumentType =
                DocumentTypeData.INCOMING.findUsing(documentTypeRepository);
        final DocumentType incomingInvoiceDocumentType =
                DocumentTypeData.INCOMING_INVOICE.findUsing(documentTypeRepository);

        final IncomingInvoice incomingInvoice = derivedObjectLookup.invoiceIfAnyFrom(docHead);

        if (existingPaperclipIfAny != null) {

            final String userRef1 = docHead.getSummaryLineDocumentName(LineCache.DEFAULT);
            if (userRef1 == null) {

                // userRef1 removed, so delete the existing paperclip.
                paperclipRepository.delete(existingPaperclipIfAny);

                // reset the existing document back to vanilla 'INCOMING'
                existingPaperclipIfAny.getDocument().setType(incomingDocumentType);

            } else {

                if (!Objects.equals(existingDocumentNameIfAny, userRef1)) {

                    // userRef1 has been changed, so attempt to point to new Document

                    if(userRef1.startsWith(CodaDocLine.USER_REF_SDI_ID_PREFIX)) {

                        // domestic Ita, search for a DocFlowZip
                        final String sdiIdStr = userRef1.substring(1);
                        final long sdiId;
                        try {
                            sdiId = Long.parseLong(userRef1);
                        } catch(NumberFormatException ex) {
                            softErrors.add("Could not find a 'DocFlowZip', inferred SDI Id '%s' is not numeric", sdiIdStr);
                            return;
                        }

                        final Optional<Document> documentIfAny = docFlowZipRepository.optFindBySdiId(sdiId)
                                .map(docFlowZip -> docFlowZip.locateAttachedDocument(PAPERCLIP_ROLE_NAME_GENERATED));
                        if (!documentIfAny.isPresent()) {
                            softErrors.add("Could not find a 'DocFlowZip' for SDI Id '%s'", sdiIdStr);
                            return;
                        }

                        // we simply point the existing paperclip of the CodaDocHead to its PDF
                        existingPaperclipIfAny.setDocument(documentIfAny.get());

                    } else {

                        // foreign Ita, so we search on barcode

                        // reset the existing document back to vanilla 'INCOMING'
                        existingPaperclipIfAny.getDocument().setType(incomingDocumentType);

                        final List<Document> documents =
                                documentRepository.findByTypeAndNameAndAtPath(incomingDocumentType, AT_PATH, userRef1);
                        switch (documents.size()) {
                        case 0:
                            // could not locate new Document, so delete old paperclip and reset document
                            paperclipRepository.delete(existingPaperclipIfAny);
                            break;
                        case 1:
                            // update the paperclip to point to the new document.
                            final Document document = documents.get(0);
                            existingPaperclipIfAny.setDocument(document);
                            document.setType(incomingInvoiceDocumentType);
                            break;
                        default:
                            // could not locate a unique Document, so delete
                            paperclipRepository.delete(existingPaperclipIfAny);
                            softErrors.add("More than one document found named '%s'", userRef1);
                        }
                    }


                } else {
                    // no change in the document name, so leave paperclip as it is
                }
            }

        } else {

            // if the DocHead is valid, and its handling is set to sync, then we create new Estatio objects
            if (createIfDoesNotExist) {

                final String userRef1 = docHead.getSummaryLineDocumentName(LineCache.DEFAULT);

                // This should not happen for new invoices any longer, as CodaDocHeads can't be valid any longer if the userRef1 is missing.
                // However, for some existing invoices before this validation was in place, it might not be present on the docHead. This null guard allows those to sync anyway.
                if (userRef1 == null) {
                    return;
                }

                // userRef1 has been entered, there was no paperclip previously
                if(userRef1.startsWith(CodaDocLine.USER_REF_SDI_ID_PREFIX)) {

                    // domestic Ita, search for a DocFlowZip
                    final String sdiIdStr = userRef1.substring(1);
                    final long sdiId;
                    try {
                        sdiId = Long.parseLong(userRef1);
                    } catch(NumberFormatException ex) {
                        softErrors.add("Could not find a 'DocFlowZip', inferred SDI Id '%s' is not numeric", sdiIdStr);
                        return;
                    }

                    final Optional<Document> documentIfAny = docFlowZipRepository.optFindBySdiId(sdiId)
                            .map(docFlowZip -> docFlowZip.locateAttachedDocument(PAPERCLIP_ROLE_NAME_GENERATED));
                    if (!documentIfAny.isPresent()) {
                        softErrors.add("Could not find a 'DocFlowZip' for SDI Id '%s'", sdiIdStr);
                        return;
                    }

                    // domestic Ita, use docflow
                    final Document document = documentIfAny.get();
                    paperclipRepository.attach(document, null, incomingInvoice);
                    document.setType(incomingInvoiceDocumentType);

                } else {

                    // foreign Ita, use barcode

                    final List<Document> documents =
                        documentRepository.findByTypeAndNameAndAtPath(incomingDocumentType, "/ITA", userRef1);
                    switch (documents.size()) {
                        case 0:
                            // nothing to do
                            break;
                        case 1:
                            // attach
                            final Document document = documents.get(0);
                            paperclipRepository.attach(document, null, incomingInvoice);
                            document.setType(incomingInvoiceDocumentType);
                            break;
                        default:
                            // could not locate a unique Document, so do nothing
                            softErrors.add("More than one document found named '%s'", userRef1);
                    }
                }

            } else {
                // no paperclips to create.
            }
        }
    }

    @Inject
    DocFlowZipRepository docFlowZipRepository;

    /**
     * update task description (if pending is to complete)
     * <p>
     * nb: note that the task description won't be updated if awaiting approval and there are only soft errors.
     */
    void tryUpdatePendingTaskIfRequired(
            final CodaDocHead docHead,
            final ErrorSet errors) {

        final IncomingInvoice incomingInvoice = derivedObjectLookup.invoiceIfAnyFrom(docHead);

        if (incomingInvoice == null || errors.isEmpty()) {
            return;
        }

        // still waiting to be completed;
        // update the current task, if possible
        final IncomingInvoiceApprovalStateTransition pendingTransition =
                stateTransitionService.pendingTransitionOf(
                        incomingInvoice, IncomingInvoiceApprovalStateTransition.class);

        // there may be no pending transition, if the invoice has transition through to its final state.
        // in such a case, there's no task to update, so we just quit and basically discard the detected errors because
        // there's no way to bring them to anyone's attention.
        //
        // an example where this has occurred is for an invoice transitioned from PAYABLE to PAID, even though there
        // are errors due to new validation (we introduced validation on userRef1 after go-live).
        // Normally such invoices would have been pulled back for re-approval, however there is *also* logic to prevent
        // PAYABLE invoices from being pulled back (this was for historical invoices).
        //
        // This should be a transitional issue; for new invoices, the validation on userRef1 will stop the invoice
        // from even being created.
        //
        if (pendingTransition == null) {
            return;
        }

        // bring the errors (perhaps newly discovered, eg as result of stricter validation) to someone's attention
        final Task task = pendingTransition.getTask();
        if (task == null) {
            return;
        }
        task.setDescription(errors.getText());

    }

    private static BigDecimal elseZero(final BigDecimal bd) {
        return bd != null ? bd : BigDecimal.ZERO;
    }


    @Inject
    TitleService titleService;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    OrderItemInvoiceItemLinkRepository linkRepository;

    @Inject
    DocumentRepository documentRepository;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    WrapperFactory wrapperFactory;

    @Inject
    FactoryService factoryService;

    @Inject
    DerivedObjectLookup derivedObjectLookup;
}
