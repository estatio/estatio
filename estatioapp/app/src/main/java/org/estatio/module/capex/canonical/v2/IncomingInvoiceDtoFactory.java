package org.estatio.module.capex.canonical.v2;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.util.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.canonical.financial.v2.PaymentMethod;
import org.estatio.canonical.incominginvoice.v2.IncomingInvoiceDto;
import org.estatio.canonical.incominginvoice.v2.IncomingInvoiceItemType;
import org.estatio.canonical.incominginvoice.v2.IncomingInvoicePaymentStatus;
import org.estatio.canonical.order.v2.IncomingInvoiceTypeType;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.doc.CodaDocHeadRepository;
import org.estatio.module.invoice.dom.InvoiceItem;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "capex.canonical.v2.IncomingInvoiceDtoFactory"
)
public class IncomingInvoiceDtoFactory extends DtoFactoryAbstract<IncomingInvoice, IncomingInvoiceDto> {

    public IncomingInvoiceDtoFactory() {
        super(IncomingInvoice.class, IncomingInvoiceDto.class);
    }

    protected IncomingInvoiceDto newDto(final IncomingInvoice incomingInvoice) {

        final IncomingInvoiceDto dto = new IncomingInvoiceDto();

        dto.setSelf(mappingHelper.oidDtoFor(incomingInvoice));

        final CodaDocHead codaDocHeadIfAny = codaDocHeadRepository.findByIncomingInvoice(incomingInvoice);

        dto.setCodaDocHead(mappingHelper.oidDtoFor(codaDocHeadIfAny));
        dto.setAtPath(incomingInvoice.getAtPath());
        dto.setSellerBankAccount(mappingHelper.oidDtoFor(incomingInvoice.getBankAccount()));
        dto.setBuyerParty(mappingHelper.oidDtoFor(incomingInvoice.getBuyer()));
        dto.setSellerParty(mappingHelper.oidDtoFor(incomingInvoice.getSeller()));
        dto.setFixedAsset(mappingHelper.oidDtoFor(incomingInvoice.getProperty()));
        dto.setDueDate(asXMLGregorianCalendar(incomingInvoice.getDueDate()));
        dto.setInvoiceDate(asXMLGregorianCalendar(incomingInvoice.getInvoiceDate()));
        dto.setReceivedDate(asXMLGregorianCalendar(incomingInvoice.getDateReceived()));
        dto.setInvoiceNumber(incomingInvoice.getInvoiceNumber());
        dto.setPaymentMethod(paymentMethodOf(incomingInvoice.getPaymentMethod()));
        dto.setPaymentStatus(paymentStatusOf(incomingInvoice));

        final List<IncomingInvoiceItemType> itemDtos = Lists.newArrayList();
        for (final InvoiceItem invoiceItem : incomingInvoice.getItems()) {
            final IncomingInvoiceItem incomingInvoiceItem = (IncomingInvoiceItem) invoiceItem;
            final IncomingInvoiceItemType itemDto = new IncomingInvoiceItemType();

            itemDto.setSelf(mappingHelper.oidDtoFor(incomingInvoiceItem));
            itemDto.setDescription(incomingInvoiceItem.getDescription());
            itemDto.setType(typeFor(incomingInvoiceItem));

            itemDto.setDueDate(asXMLGregorianCalendar(incomingInvoiceItem.getDueDate()));
            itemDto.setGrossAmount(incomingInvoiceItem.getGrossAmount());
            itemDto.setNetAmount(incomingInvoiceItem.getNetAmount());
            itemDto.setTax(mappingHelper.oidDtoFor(incomingInvoiceItem.getTax()));
            itemDto.setVatAmount(null);

            // can only set if there is precisely one link to an OrderItem.
            final List<OrderItemInvoiceItemLink> links = linkRepository.findLinksByInvoiceItem(incomingInvoiceItem);
            if(links.size() == 1) {
                final OrderItemInvoiceItemLink link = links.get(0);
                itemDto.setOrder(mappingHelper.oidDtoFor(link.getOrder()));
                itemDto.setProject(mappingHelper.oidDtoFor(link.getProject()));
                itemDto.setCharge(mappingHelper.oidDtoFor(incomingInvoiceItem.getCharge()));
            }

            itemDtos.add(itemDto);
        }
        dto.setItems(itemDtos);

        return dto;
    }

    /**
     * For Italy (as per <code>IncomingInvoiceTypeDerivationForItalyService</code>), can only be one of
     * CAPEX, ITA_MANAGEMENT_COSTS, or ITA_RECOVERABLE
     */
    private IncomingInvoiceTypeType typeFor(final IncomingInvoiceItem incomingInvoiceItem) {
        final IncomingInvoiceType iiType = incomingInvoiceItem.getIncomingInvoiceType();
        if(iiType == null) {
            return null;
        }
        switch (iiType) {

        case CAPEX:
            return IncomingInvoiceTypeType.CAPEX;

        // Italian
        case ITA_RECOVERABLE:
            return IncomingInvoiceTypeType.ITA_RECOVERABLE;
        case ITA_MANAGEMENT_COSTS:
            return IncomingInvoiceTypeType.ITA_MANAGEMENT_COSTS;

        case ITA_ORDER_INVOICE:
            // not really an invoice type, used for orders
            return null;

        // French
        case SERVICE_CHARGES:
            return IncomingInvoiceTypeType.FRA_SERVICE_CHARGES;
        case LOCAL_EXPENSES:
            return IncomingInvoiceTypeType.FRA_LOCAL_EXPENSES;
        case PROPERTY_EXPENSES:
            return IncomingInvoiceTypeType.FRA_PROPERTY_EXPENSES;
        case CORPORATE_EXPENSES:
            return IncomingInvoiceTypeType.FRA_CORPORATE_EXPENSES;
        case TANGIBLE_FIXED_ASSET:
            return IncomingInvoiceTypeType.FRA_TANGIBLE_FIXED_ASSET;
        case INTERCOMPANY:
            return IncomingInvoiceTypeType.FRA_INTERCOMPANY;
        case RE_INVOICING:
            return IncomingInvoiceTypeType.FRA_RE_INVOICING;

        default:
            return null;
        }
    }

    private PaymentMethod paymentMethodOf(final org.estatio.module.invoice.dom.PaymentMethod paymentMethod) {
        if(paymentMethod == null) {
            return null;
        }

        switch (paymentMethod) {
            case DIRECT_DEBIT:
                return PaymentMethod.DIRECT_DEBIT;
            case BANK_TRANSFER:
                return PaymentMethod.BANK_TRANSFER;
            case CASH:
                return PaymentMethod.CASH;
            case CHEQUE:
                return PaymentMethod.CHEQUE;

            case BILLING_ACCOUNT:
            case CREDIT_CARD:
            case REFUND_BY_SUPPLIER:
            case MANUAL_PROCESS:
            default:
                // for now, not mapped
                return null;
        }
    }

    private IncomingInvoicePaymentStatus paymentStatusOf(final IncomingInvoice incomingInvoice) {
        final IncomingInvoiceApprovalState approvalState = incomingInvoice.getApprovalState();
        switch (approvalState) {
        case PAYABLE:
            return IncomingInvoicePaymentStatus.PAYABLE;
        case PAID:
            return IncomingInvoicePaymentStatus.PAID;
        case NEW:
        case COMPLETED:
        case DISCARDED:
        case APPROVED:
        case APPROVED_BY_COUNTRY_DIRECTOR:
        case APPROVED_BY_CORPORATE_MANAGER:
        case APPROVED_BY_CENTER_MANAGER:
        case PENDING_IN_CODA_BOOKS:
        case PENDING_BANK_ACCOUNT_CHECK:
        default:
            return IncomingInvoicePaymentStatus.NOT_PAYABLE;
        }
    }

    @Inject
    OrderItemInvoiceItemLinkRepository linkRepository;

    @Inject
    CodaDocHeadRepository codaDocHeadRepository;

}
