package org.estatio.module.lease.canonical.v2;

import java.util.Optional;
import java.util.SortedSet;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.dto.DtoMappingHelper;

import org.estatio.canonical.invoice.v2.InvoiceItemType;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeGroup;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRate;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "lease.canonical.v2.InvoiceItemForLeaseDtoFactory"
)
public class InvoiceItemForLeaseDtoFactory extends DtoFactoryAbstract {

    @Programmatic
    public InvoiceItemType newDto(final InvoiceItem item) {
        InvoiceItemType dto = new InvoiceItemType();

        dto.setSelf(mappingHelper.oidDtoFor(item));

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
            dto.setAdjustment(invoiceItemForLease.getAdjustment() != null && invoiceItemForLease.getAdjustment());
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
        final TaxRate rate = item.getTaxRate();
        dto.setTaxReference(tax.getReference());
        dto.setTaxName(tax.getName());
        dto.setTaxDescription(tax.getDescription());
        dto.setTaxExternalReference(rate == null || rate.getExternalReference() == null ? tax.getExternalReference() : rate.getExternalReference());

        dto.setNetAmount(item.getNetAmount());
        dto.setGrossAmount(item.getGrossAmount());
        dto.setVatAmount(item.getVatAmount());

        dto.setDescription(item.getDescription());

        dto.setStartDate(asXMLGregorianCalendar(item.getStartDate()));
        dto.setEndDate(asXMLGregorianCalendar(item.getEndDate()));
        dto.setEffectiveStartDate(asXMLGregorianCalendar(firstNonNull(item.getEffectiveStartDate(), item.getStartDate())));
        dto.setEffectiveEndDate(asXMLGregorianCalendar(firstNonNull(item.getEffectiveEndDate(), item.getEndDate())));

        if (item  instanceof InvoiceItemForLease) {
            final InvoiceItemForLease invoiceItemForLease = (InvoiceItemForLease) item;

            final InvoiceForLease invoice =
                    (InvoiceForLease)invoiceItemForLease.getInvoice();
            final Lease leaseIfAny = invoice.getLease();
            if(leaseIfAny != null) {
                final SortedSet<Occupancy> occupancies = leaseIfAny.getOccupancies();
                if (!occupancies.isEmpty()) {
                    //                final Optional<Occupancy> occupancyIfAny =
                    //                        occupancies.stream().filter(x -> x.getInterval().overlaps(item.getEffectiveInterval())).findFirst();
                    final Optional<Occupancy> occupancyIfAny =
                            Optional.ofNullable(occupancies.first());
                    if (occupancyIfAny.isPresent()) {
                        final Occupancy occupancy = occupancyIfAny.orElse(occupancies.last());
                        final Brand brand = occupancy.getBrand();
                        dto.setOccupancyBrand(brand == null ? null : brand.getName());
                        if (dto.getFixedAssetReference()== null) {
                            // the unit was not retrieved through the invoice item, so get it from the occupancy then.
                            dto.setFixedAssetReference(occupancy.getUnit().getReference());
                            dto.setFixedAssetExternalReference(occupancy.getUnit().getExternalReference());
                        }
                    } else {
                        //                    throw new IllegalArgumentException("Invoice has an effective date range outside the scope of the occupanies");
                        throw new IllegalArgumentException("No Occupancy Found");
                    }
                }
            }

        }
        return dto;
    }

    @Inject
    DtoMappingHelper mappingHelper;
}
