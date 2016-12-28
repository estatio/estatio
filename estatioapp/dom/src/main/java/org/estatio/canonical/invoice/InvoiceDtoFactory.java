package org.estatio.canonical.invoice;

import java.math.BigDecimal;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.canonical.DtoFactoryAbstract;
import org.estatio.canonical.invoice.v1.InvoiceDto;
import org.estatio.canonical.invoice.v1.InvoiceItemDto;
import org.estatio.canonical.DtoMappingHelper;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.financial.FixedAssetFinancialAccountRepository;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class InvoiceDtoFactory extends DtoFactoryAbstract {

    private final InvoiceItemDtoFactory invoiceItemDtoFactory = new InvoiceItemDtoFactory();

    @Programmatic
    public InvoiceDto newDto(final Invoice invoice) {
        InvoiceDto dto = new InvoiceDto();

        dto.setSelf(mappingHelper.oidDtoFor(invoice));
        dto.setAtPath(invoice.getApplicationTenancyPath());
        dto.setBuyerParty(mappingHelper.oidDtoFor(invoice.getBuyer()));
        dto.setSellerParty(mappingHelper.oidDtoFor(invoice.getSeller()));

        dto.setDueDate(asXMLGregorianCalendar(invoice.getDueDate()));
        dto.setInvoiceDate(asXMLGregorianCalendar(invoice.getInvoiceDate()));
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setPaymentMethod(toDto(invoice.getPaymentMethod()));
        dto.setCollectionNumber(invoice.getCollectionNumber());

        final Lease lease = invoice.getLease();
        if (lease != null){
            dto.setAgreementReference(lease.getReference());
            final BankMandate paidBy = lease.getPaidBy();
            if (paidBy != null) {
                dto.setPaidByMandate(mappingHelper.oidDtoFor(paidBy));
                dto.setBuyerBankAccount(mappingHelper.oidDtoFor(paidBy.getBankAccount()));
            }
        }

        final Optional<FixedAsset> fixedAssetIfAny = Optional.ofNullable(invoice.getFixedAsset());
        if (fixedAssetIfAny.isPresent()) {
            final FixedAsset fixedAsset = fixedAssetIfAny.get();
            dto.setFixedAssetReference(fixedAsset.getReference());
            dto.setFixedAssetExternalReference(fixedAsset.getExternalReference());

            // there should be only one
            dto.setSellerBankAccount(mappingHelper.oidDtoFor(invoice.getSellerBankAccount()));
        }

        invoice.getItems().stream().forEach(item -> dto.getItems().add(invoiceItemDtoFactory.newDto(item)));

        dto.setNetAmount(dto.getItems().stream()
                            .map(InvoiceItemDto::getNetAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setGrossAmount(dto.getItems().stream()
                            .map(InvoiceItemDto::getGrossAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setVatAmount(dto.getItems().stream()
                            .map(InvoiceItemDto::getVatAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));

        return dto;
    }

    private org.estatio.canonical.invoice.v1.PaymentMethod toDto(final PaymentMethod paymentMethod) {
        switch (paymentMethod) {
        case DIRECT_DEBIT:
            return org.estatio.canonical.invoice.v1.PaymentMethod.DIRECT_DEBIT;
        case BILLING_ACCOUNT:
            return null;
        case BANK_TRANSFER:
            return org.estatio.canonical.invoice.v1.PaymentMethod.BANK_TRANSFER;
        case CASH:
            return org.estatio.canonical.invoice.v1.PaymentMethod.CASH;
        case CHEQUE:
            return org.estatio.canonical.invoice.v1.PaymentMethod.CHEQUE;
        default:
            // shouldn't happen, above switch is complete.
            throw new IllegalArgumentException(String.format(
                    "Payment method '%s' not recognized.", paymentMethod));
        }
    }

    @Inject
    DtoMappingHelper mappingHelper;
    @Inject
    FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;
}
