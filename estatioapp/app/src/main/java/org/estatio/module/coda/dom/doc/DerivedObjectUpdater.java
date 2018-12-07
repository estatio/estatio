package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
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
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
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
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Party;
import org.estatio.module.tax.dom.Tax;

@DomainService(nature = NatureOfService.DOMAIN)
public class DerivedObjectUpdater {

    static final String AT_PATH = "/ITA";

    IncomingInvoice upsertIncomingInvoice(
            final CodaDocHead docHead,
            final boolean syncIfNew) {
        IncomingInvoice incomingInvoiceIfAny = derivedObjectLookup.invoiceIfAnyFrom(docHead);
        return upsertIncomingInvoice(docHead, incomingInvoiceIfAny, syncIfNew);
    }

    public static class UpsertIncomingInvoiceEvent extends ActionDomainEvent<Object> {
        public String getUserRef1() {
            return this.getArguments() != null ? ((CodaDocLine) this.getArguments().get(0)).getUserRef1() : null;
        }
    }

    public IncomingInvoice upsertIncomingInvoice(
            final CodaDocHead docHead,
            final IncomingInvoice existingInvoiceIfAny,
            final boolean syncIfNew) {

        final IncomingInvoiceItem existingInvoiceItemIfAny = firstItemOf(existingInvoiceIfAny);

        //
        // Now update any existing objects based on this new CodaDocHead.  Note that it's possible that this
        // CodaDocHead is invalid even if a previous one was valid.  We simply align the Estatio objects
        // (if there are any) with whatever CodaDocHead says).
        //
        final Party buyer = docHead.getCmpCodeBuyer();

        final Party seller = docHead.getSummaryLineAccountCodeEl6Supplier(LineCache.DEFAULT);
        final IncomingInvoiceType type = docHead.getAnalysisLineIncomingInvoiceType(LineCache.DEFAULT);
        final String invoiceNumber = docHead.getSummaryLineExtRef2(LineCache.DEFAULT);
        final Property property = docHead.getSummaryLineAccountEl3Property(LineCache.DEFAULT);

        final BankAccount bankAccount= docHead.getSummaryLineSupplierBankAccount(LineCache.DEFAULT);

        final PaymentMethod paymentMethod = docHead.getSummaryLinePaymentMethod(LineCache.DEFAULT);

        final LocalDate vatRegistrationDate = docHead.getSummaryLineValueDate(LineCache.DEFAULT);

        final LocalDate dateReceived = docHead.getInputDate();

        final LocalDate invoiceDate= docHead.getDocDate();
        final LocalDate dueDate = docHead.getSummaryLineDueDate(LineCache.DEFAULT);

        final LocalDate paidDate = docHead.getStatPayPaidDate();

        final InvoiceStatus invoiceStatus = InvoiceStatus.NEW; // we don't care, this is for outgoing invoices.

        // there will be just a single InvoiceItem, combining info from
        // both the CodaDocLine summary item and the analysis one
        final String description = docHead.getSummaryLineDescription(LineCache.DEFAULT);

        final Charge charge = docHead.getSummaryLineExtRefWorkTypeCharge(LineCache.DEFAULT);
        final Project project = docHead.getSummaryLineExtRefProject(LineCache.DEFAULT);
        final BudgetItem budgetItem = null;

        final BigDecimal grossAmount = docHead.getSummaryLineDocValue(LineCache.DEFAULT);
        final BigDecimal vatAmount = docHead.getSummaryLineDocSumTax(LineCache.DEFAULT);
        final BigDecimal netAmount = Util.subtract(grossAmount, vatAmount);
        final String period = Util.asFinancialYear(docHead.getCodaPeriod());
        final Tax tax = null;
        final boolean postedToCodaBooks = Objects.equals(docHead.getLocation(), "books");

        //
        // update the incoming invoice (we simply blindly follow Coda, since Coda always leads.
        //
        final IncomingInvoice incomingInvoice;
        if (existingInvoiceIfAny != null) {

            incomingInvoice = existingInvoiceIfAny;
            incomingInvoiceRepository.updateInvoice(
                    incomingInvoice,
                    type, invoiceNumber, property, AT_PATH, buyer, seller,
                    invoiceDate, dueDate, vatRegistrationDate, paymentMethod, invoiceStatus, dateReceived, bankAccount,
                    postedToCodaBooks, paidDate
            );

            //
            // also update the existing item
            //
            if (existingInvoiceItemIfAny != null) {

                final LocalDateInterval ldi = PeriodUtil.yearFromPeriod(period);

                existingInvoiceItemIfAny.setIncomingInvoiceType(type);
                existingInvoiceItemIfAny.setCharge(charge);
                existingInvoiceItemIfAny.setDescription(description);
                existingInvoiceItemIfAny.setNetAmount(netAmount);
                existingInvoiceItemIfAny.setVatAmount(vatAmount);
                existingInvoiceItemIfAny.setGrossAmount(grossAmount);
                existingInvoiceItemIfAny.setTax(tax);
                existingInvoiceItemIfAny.setDueDate(dueDate);
                existingInvoiceItemIfAny.setStartDate(ldi.startDate());
                existingInvoiceItemIfAny.setEndDate(ldi.endDate());
                existingInvoiceItemIfAny.setFixedAsset(property);
                existingInvoiceItemIfAny.setProject(project);
                existingInvoiceItemIfAny.setBudgetItem(budgetItem);
            }

        } else {
            // if the DocHead is valid, and sync has been requested, then we create new Estatio objects
            if(syncIfNew) {

                // as a side-effect, the approvalState will be set to NEW
                // (subscriber on ObjectPersist)
                incomingInvoice =
                        incomingInvoiceRepository.create(
                                type, invoiceNumber, property, AT_PATH, buyer, seller,
                                invoiceDate, dueDate, vatRegistrationDate, paymentMethod,
                                invoiceStatus, dateReceived, bankAccount, null, paidDate);

                incomingInvoice.setGrossAmount(grossAmount);
                incomingInvoice.setNetAmount(netAmount);
                incomingInvoice.setPostedToCodaBooks(postedToCodaBooks);

                incomingInvoice.addItem(
                        type, charge, description,
                        netAmount, vatAmount, grossAmount, tax,
                        dueDate, period, property, project, budgetItem);

            } else {
                // nothing to do; this CodaDocHead isn't valid and there isn't an IncomingInvoice to update.
                incomingInvoice = null;
            }
        }
        return incomingInvoice;
    }

    void updateLinkToOrderItem(
            final CodaDocHead docHead,
            final boolean syncIfNew,
            final ErrorSet softErrors) {

        final OrderItemInvoiceItemLink linkIfAny = derivedObjectLookup.linkIfAnyFrom(docHead);

        updateLinkToOrderItem(docHead, linkIfAny, syncIfNew, softErrors);
    }

    public void updateLinkToOrderItem(
            final CodaDocHead docHead,
            final OrderItemInvoiceItemLink existingLinkIfAny,
            final boolean syncIfNew,
            final ErrorSet softErrors) {

        final IncomingInvoice incomingInvoice = derivedObjectLookup.invoiceIfAnyFrom(docHead);
        final IncomingInvoiceItem invoiceItem = firstItemOf(incomingInvoice);

        final BigDecimal grossAmount = docHead.getSummaryLineDocValue(LineCache.DEFAULT);
        final BigDecimal vatAmount = docHead.getSummaryLineDocSumTax(LineCache.DEFAULT);
        final BigDecimal netAmount = Util.subtract(grossAmount, vatAmount);

        final OrderItem orderItem = docHead.getSummaryLineExtRefOrderItem(LineCache.DEFAULT);
        if(existingLinkIfAny != null) {

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
            if(syncIfNew) {

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
                wrapperFactory.wrap(
                        factoryService.mixin(IncomingInvoiceItem_createOrderItemLink.class, invoiceItem))
                        .act(orderItem, invoiceItem.getNetAmount());
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
            final boolean syncIfNew,
            final ErrorSet softErrors) {

        final Paperclip paperclipIfAny = derivedObjectLookup.paperclipIfAnyFrom(docHead);
        final String documentNameIfAny = derivedObjectLookup.documentNameIfAnyFrom(docHead);

        updatePaperclip(docHead, paperclipIfAny, documentNameIfAny, syncIfNew, softErrors);
    }

    /**
     * attach paperclip to Document named after 'userref1', if exists.
     */
    public void updatePaperclip(
            final CodaDocHead docHead,
            final Paperclip existingPaperclipIfAny,
            final String existingDocumentNameIfAny,
            final boolean syncIfNew,
            final ErrorSet softErrors) {

        final DocumentType incomingDocumentType =
                DocumentTypeData.INCOMING.findUsing(documentTypeRepository);
        final DocumentType incomingInvoiceDocumentType =
                DocumentTypeData.INCOMING_INVOICE.findUsing(documentTypeRepository);

        final IncomingInvoice incomingInvoice = derivedObjectLookup.invoiceIfAnyFrom(docHead);

        if (existingPaperclipIfAny != null) {

            final String documentName = docHead.getSummaryLineDocumentName(LineCache.DEFAULT);
            if(documentName == null) {

                // userRef1 removed, so delete the existing paperclip.
                paperclipRepository.delete(existingPaperclipIfAny);

                // reset the existing document back to vanilla 'INCOMING'
                existingPaperclipIfAny.getDocument().setType(incomingDocumentType);

            } else {

                if (!Objects.equals(existingDocumentNameIfAny, documentName)) {

                    // reset the existing document back to vanilla 'INCOMING'
                    existingPaperclipIfAny.getDocument().setType(incomingDocumentType);

                    // userRef1 has been changed, so attempt to point to new Document
                    final List<Document> documents =
                            documentRepository.findByTypeAndNameAndAtPath(incomingDocumentType, AT_PATH, documentName);
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
                            softErrors.add("More than one document found named '%s'", documentName);
                    }
                } else {
                    // no change in the document name, so leave paperclip as it is
                }
            }

        } else {

            // if the DocHead is valid, and its handling is set to sync, then we create new Estatio objects
            if(syncIfNew) {

                final String documentName = docHead.getSummaryLineDocumentName(LineCache.DEFAULT);

                // userRef1 has been entered, there was no paperclip previously
                final List<Document> documents =
                        documentRepository.findByTypeAndNameAndAtPath(incomingDocumentType, "/ITA", documentName);
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
                        softErrors.add("More than one document found named '%s'", documentName);
                }

            } else {
                // no paperclips to create.
            }
        }
    }

    /**
     * update task description (if pending is to complete)
     * <p>
     * nb: note that the task description won't be updated if awaiting approval and there are only soft errors.
     */
    public void updatePendingTask(
            final CodaDocHead docHead,
            final boolean syncIfNew,
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

        final IncomingInvoiceApprovalStateTransitionType transitionType =
                pendingTransition.getTransitionType();
        switch (transitionType) {
            case COMPLETE:
                // this should always be the case; there are no other pending transitions from a state of NEW.
                // update the task will the issues identified.
                final Task task = pendingTransition.getTask();
                if (task != null) {
                    task.setDescription(errors.getText());
                }
                break;
        }
    }

    private static IncomingInvoiceItem firstItemOf(final IncomingInvoice existingInvoiceIfAny) {
        return existingInvoiceIfAny != null
                ? existingInvoiceIfAny.getItems().size() == 1
                ? (IncomingInvoiceItem) existingInvoiceIfAny.getItems().first()
                : null
                : null;
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
