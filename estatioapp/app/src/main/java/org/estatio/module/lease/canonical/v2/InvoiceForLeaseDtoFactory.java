package org.estatio.module.lease.canonical.v2;

import java.math.BigDecimal;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.dto.DtoMappingHelper;

import org.estatio.canonical.invoice.v2.InvoiceDto;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "lease.canonical.v2.InvoiceForLeaseDtoFactory"
)
public class InvoiceForLeaseDtoFactory extends DtoFactoryAbstract {

    @Programmatic
    public InvoiceDto newDto(final InvoiceForLease invoiceForLease) {
        InvoiceDto dto = new InvoiceDto();

        dto.setSelf(mappingHelper.oidDtoFor(invoiceForLease));
        dto.setAtPath(invoiceForLease.getApplicationTenancyPath());

        dto.setBuyerParty(mappingHelper.oidDtoFor(invoiceForLease.getBuyer()));
        dto.setSellerParty(mappingHelper.oidDtoFor(invoiceForLease.getSeller()));

        dto.setDueDate(asXMLGregorianCalendar(invoiceForLease.getDueDate()));
        dto.setInvoiceDate(asXMLGregorianCalendar(invoiceForLease.getInvoiceDate()));
        dto.setInvoiceNumber(invoiceForLease.getInvoiceNumber());
        dto.setPaymentMethod(toDto(invoiceForLease.getPaymentMethod()));
        dto.setCollectionNumber(invoiceForLease.getCollectionNumber());

        final Lease lease = invoiceForLease.getLease();
        if (lease != null){
            dto.setAgreementReference(lease.getReference());
            final BankMandate paidBy = lease.getPaidBy();
            if (paidBy != null) {
                dto.setPaidByMandate(mappingHelper.oidDtoFor(paidBy));
                dto.setBuyerBankAccount(mappingHelper.oidDtoFor(paidBy.getBankAccount()));
            }
        }

        final Optional<FixedAsset> fixedAssetIfAny = Optional.ofNullable(invoiceForLease.getFixedAsset());
        if (fixedAssetIfAny.isPresent()) {
            final FixedAsset fixedAsset = fixedAssetIfAny.get();
            dto.setFixedAssetReference(fixedAsset.getReference());
            dto.setFixedAssetExternalReference(fixedAsset.getExternalReference());

            // there should be only one
            dto.setSellerBankAccount(mappingHelper.oidDtoFor(invoiceForLease.getSellerBankAccount()));
        }

        invoiceForLease.getItems().stream().forEach(item -> dto.getItems().add(invoiceItemForLeaseDtoFactory.newDto(item)));

        dto.setNetAmount(dto.getItems().stream()
                            .map(x -> valueElseZero(x.getNetAmount()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setGrossAmount(dto.getItems().stream()
                            .map(x -> valueElseZero(x.getGrossAmount()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setVatAmount(dto.getItems().stream()
                            .map(x -> valueElseZero(x.getVatAmount()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add));

        return dto;
    }

    private static org.estatio.canonical.invoice.v2.PaymentMethod toDto(final PaymentMethod paymentMethod) {
        switch (paymentMethod) {
        case DIRECT_DEBIT:
            return org.estatio.canonical.invoice.v2.PaymentMethod.DIRECT_DEBIT;
        case BILLING_ACCOUNT:
            return null;
        case BANK_TRANSFER:
            return org.estatio.canonical.invoice.v2.PaymentMethod.BANK_TRANSFER;
        case CASH:
            return org.estatio.canonical.invoice.v2.PaymentMethod.CASH;
        case CHEQUE:
            return org.estatio.canonical.invoice.v2.PaymentMethod.CHEQUE;
        default:
            // shouldn't happen, above switch is complete.
            throw new IllegalArgumentException(String.format(
                    "Payment method '%s' not recognized.", paymentMethod));
        }
    }

    @Inject
    InvoiceItemForLeaseDtoFactory invoiceItemForLeaseDtoFactory;

    @Inject
    DtoMappingHelper mappingHelper;
}
