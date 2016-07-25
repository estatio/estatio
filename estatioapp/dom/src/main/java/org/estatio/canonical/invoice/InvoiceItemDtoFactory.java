package org.estatio.canonical.invoice;

import java.util.Optional;
import java.util.SortedSet;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.canonical.DtoFactoryAbstract;
import org.estatio.canonical.invoice.v1.InvoiceItemDto;
import org.estatio.dom.DtoMappingHelper;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.tax.Tax;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class InvoiceItemDtoFactory extends DtoFactoryAbstract {

    @Programmatic
    public InvoiceItemDto newDto(final InvoiceItem item) {
        InvoiceItemDto dto = new InvoiceItemDto();

        if (item  instanceof InvoiceItemForLease) {
            InvoiceItemForLease invoiceItemForLease = (InvoiceItemForLease) item;
            final Lease lease = invoiceItemForLease.getLease();
            if (lease !=null) {
                dto.setAgreementReference(lease.getReference());
            }
            final FixedAsset fixedAsset = invoiceItemForLease.getFixedAsset();
            if (fixedAsset !=null) {
                dto.setFixedAssetReference(fixedAsset.getReference());
                dto.setFixedAssetExternalReference(fixedAsset.getExternalReference());
            }
        }

        final Charge charge = item.getCharge();
        dto.setChargeReference(charge.getReference());
        dto.setChargeDescription(charge.getDescription());
        dto.setChargeExternalReference(charge.getExternalReference());
        dto.setChargeName(charge.getName());

        final ChargeGroup group = charge.getGroup();
        dto.setChargeGroupReference(group.getReference());
        dto.setChargeGroupName(group.getName());

        final Tax tax = item.getTax();
        dto.setTaxReference(tax.getReference());
        dto.setTaxName(tax.getName());
        dto.setTaxDescription(tax.getDescription());
        dto.setTaxExternalReference(tax.getExternalReference());

        dto.setNetAmount(item.getNetAmount());
        dto.setGrossAmount(item.getGrossAmount());
        dto.setVatAmount(item.getVatAmount());

        dto.setDescription(item.getDescription());

        dto.setStartDate(asXMLGregorianCalendar(item.getStartDate()));
        dto.setEndDate(asXMLGregorianCalendar(item.getEndDate()));
        dto.setEffectiveStartDate(asXMLGregorianCalendar(item.getEffectiveStartDate()));
        dto.setEffectiveEndDate(asXMLGregorianCalendar(item.getEffectiveEndDate()));

        final Lease leaseIfAny = item.getInvoice().getLease();
        if(leaseIfAny != null) {
            final SortedSet<Occupancy> occupancies = leaseIfAny.getOccupancies();
            if (!occupancies.isEmpty()) {
                final Optional<Occupancy> occupancyIfAny =
                        occupancies.stream().filter(x -> x.getInterval().overlaps(item.getEffectiveInterval())).findFirst();
                if (occupancyIfAny.isPresent()) {
                    final Occupancy occupancy = occupancyIfAny.orElse(occupancies.last());
                    final Brand brand = occupancy.getBrand();
                    dto.setOccupancyBrand(brand == null ? null : brand.getName());
                    dto.setFixedAssetReference(occupancy.getUnit().getReference());
                    dto.setFixedAssetExternalReference(occupancy.getUnit().getExternalReference());
                } else {
                    throw new IllegalArgumentException("Invoice has an effective date range outside the scope of the occupanies");
                }
            }
        }
        return dto;
    }

    @Inject
    DtoMappingHelper mappingHelper;
}
