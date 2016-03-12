package org.estatio.canonical.invoice;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.canonical.invoice.v1.InvoiceDto;
import org.estatio.canonical.invoice.v1.InvoiceItemDto;
import org.estatio.dom.DtoMappingHelper;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItem;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class InvoiceDtoFactory {

    @Programmatic
    public InvoiceDto newDto(final Invoice invoice) {
        InvoiceDto dto = new InvoiceDto();

        dto.setBuyerParty(mappingHelper.oidDtoFor(invoice.getBuyer()));
        dto.setSellerParty(mappingHelper.oidDtoFor(invoice.getSeller()));

        final BankMandate paidBy = invoice.getLease().getPaidBy();
        dto.setPaidByMandate(mappingHelper.oidDtoFor(paidBy));

        dto.setPaidByMandateBankAccount(mappingHelper.oidDtoFor(paidBy.getBankAccount()));

        for (InvoiceItem invoiceItem : invoice.getItems()) {
            final InvoiceItemDto itemDto = new InvoiceItemDto();

            itemDto.setAmount(invoiceItem.getNetAmount());

            dto.getItems().add(itemDto);
        }

        return dto;
    }

    @Inject
    DtoMappingHelper mappingHelper;
}
