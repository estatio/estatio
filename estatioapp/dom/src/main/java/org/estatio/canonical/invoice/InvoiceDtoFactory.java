package org.estatio.canonical.invoice;

import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.canonical.DtoFactoryAbstract;
import org.estatio.canonical.invoice.v1.InvoiceDto;
import org.estatio.dom.DtoMappingHelper;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.lease.Lease;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class InvoiceDtoFactory extends DtoFactoryAbstract {

    @Programmatic
    public InvoiceDto newDto(final Invoice invoice) {
        InvoiceDto dto = new InvoiceDto();

        dto.setBuyerParty(mappingHelper.oidDtoFor(invoice.getBuyer()));
        dto.setSellerParty(mappingHelper.oidDtoFor(invoice.getSeller()));

        dto.setDueDate(convert(invoice.getDueDate()));
        dto.setInvoiceDate(convert(invoice.getInvoiceDate()));
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setCollectionNumber(invoice.getCollectionNumber());

        final Lease lease = invoice.getLease();
        if (lease != null){
            dto.setAgreementReference(lease.getReference());
            final BankMandate paidBy = lease.getPaidBy();
            dto.setPaidByMandate(mappingHelper.oidDtoFor(paidBy));
            dto.setPaidByMandateBankAccount(mappingHelper.oidDtoFor(paidBy.getBankAccount()));
        }

        final Optional<FixedAsset> fixedAsset = Optional.of(invoice.getFixedAsset());
        if (fixedAsset.isPresent()) {
            dto.setFixedAssetReference(fixedAsset.get().getReference());
            dto.setFixedAssetExternalReference(fixedAsset.get().getExternalReference());
        }

        for (InvoiceItem invoiceItem : invoice.getItems()) {
            dto.getItems().add(new InvoiceItemDtoFactory().newDto(invoiceItem));
        }

        return dto;
    }

    @Inject
    DtoMappingHelper mappingHelper;
}
