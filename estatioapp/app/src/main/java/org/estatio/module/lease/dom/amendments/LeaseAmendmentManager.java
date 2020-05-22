package org.estatio.module.lease.dom.amendments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.CommandPersistence;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.lease.dom.amendments.LeaseAmendmentManager",
        auditing = Auditing.DISABLED,
        publishing = Publishing.DISABLED)
public class LeaseAmendmentManager {

    public LeaseAmendmentManager(){}

    public LeaseAmendmentManager(final Property property, final LeaseAmendmentType leaseAmendmentType){
        this();
        this.property = property;
        this.leaseAmendmentType = leaseAmendmentType;
    }

    public String title(){
        return "Lease amendment manager";
    }

    @Getter @Setter
    private Property property;

    @Getter @Setter
    private LeaseAmendmentType leaseAmendmentType;

    @Action(semantics = SemanticsOf.SAFE)
    public List<LeaseAmendmentImportLine> getLines(){
        List<LeaseAmendmentImportLine> result = new ArrayList<>();
        for (Lease lease : activeLeasesOnAmendmentStartdateForProperty()){
            final LeaseAmendment amendment = leaseAmendmentRepository.findUnique(lease, leaseAmendmentType);
            if (amendment!=null) {
                result.addAll(linesFromAmendment(amendment));
            } else {
                result.addAll(newLinesForLease(lease, leaseAmendmentType));
            }
        }
        return result;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public LeaseAmendmentManager importAmendments(final Blob excelsheet){
        final List<LeaseAmendmentImportLine> lines = excelService
                .fromExcel(excelsheet, LeaseAmendmentImportLine.class, "lines");
        lines.forEach(l->l.importData());
        return this;
    }

    private List<LeaseAmendmentImportLine> linesFromAmendment(final LeaseAmendment amendment){
        List<LeaseAmendmentImportLine> result = new ArrayList<>();
        result.add(new LeaseAmendmentImportLine(amendment));
        return result;
    }

    List<LeaseAmendmentImportLine> newLinesForLease(final Lease lease, final LeaseAmendmentType leaseAmendmentType){
        List<LeaseAmendmentImportLine> result = new ArrayList<>();
        final List<LeaseItem> discountCandidates = Lists.newArrayList(lease.getItems()).stream()
                .filter(i->leaseAmendmentType.getDiscountAppliesTo()!=null)
                .filter(i -> leaseAmendmentType.getDiscountAppliesTo().contains(i.getType()))
                .filter(i->i.getEffectiveInterval().overlaps(LocalDateInterval.including(leaseAmendmentType.getDiscountStartDate(), leaseAmendmentType
                        .getDiscountEndDate())))
                .collect(Collectors.toList());
        final List<LeaseItem> frequencyChangeCandidates = Lists.newArrayList(lease.getItems()).stream()
                .filter(i->leaseAmendmentType.getFrequencyChangeAppliesTo()!=null)
                .filter(i-> leaseAmendmentType.getFrequencyChangeAppliesTo().contains(i.getType()))
                .filter(i->hasChangingFrequency(i, leaseAmendmentType))
                .filter(i->i.getEffectiveInterval()!=null)
                .filter(i->i.getEffectiveInterval().overlaps(LocalDateInterval.including(leaseAmendmentType.getFrequencyChangeStartDate(), leaseAmendmentType
                        .getFrequencyChangeEndDate())))
                .collect(Collectors.toList());
        LeaseAmendmentImportLine newLine = new LeaseAmendmentImportLine();
        newLine.setLeaseAmendmentState(LeaseAmendmentState.PROPOSED);
        newLine.setLeaseReference(lease.getReference());
        newLine.setLeaseAmendmentType(leaseAmendmentType);
        newLine.setStartDate(leaseAmendmentType.getAmendmentStartDate());
        if (!discountCandidates.isEmpty()){
            newLine.setDiscountPercentage(leaseAmendmentType.getDiscountPercentage());
            newLine.setDiscountStartDate(leaseAmendmentType.getDiscountStartDate());
            newLine.setDiscountEndDate(leaseAmendmentType.getDiscountEndDate());
            newLine.setDiscountApplicableTo(LeaseAmendmentItem.applicableToToString(leaseAmendmentType.getDiscountAppliesTo()));
        }
        if (!frequencyChangeCandidates.isEmpty()){
            newLine.setFrequencyChangeStartDate(leaseAmendmentType.getFrequencyChangeStartDate());
            newLine.setFrequencyChangeEndDate(leaseAmendmentType.getFrequencyChangeEndDate());
            newLine.setFrequencyChangeApplicableTo(LeaseAmendmentItem.applicableToToString(leaseAmendmentType.getFrequencyChangeAppliesTo()));
            // TODO: now we pick the invoicing frequency from the first item encountered; .. this is cosmetics only and when we use the amendment proposal for import
            final LeaseAmendmentType.Tuple<InvoicingFrequency, InvoicingFrequency> tuple = leaseAmendmentService
                    .findInvoiceFrequencyTupleOnfirstFrequencyChangeCandidate(lease, leaseAmendmentType);
            newLine.setInvoicingFrequencyOnLease(tuple.x);
            newLine.setAmendedInvoicingFrequency(tuple.y);
        }
        result.add(newLine);
        return result;
    }

    @Action(commandPersistence = CommandPersistence.NOT_PERSISTED)
    public Blob download(@Nullable final String fileName){
        String fileNameToUse;
        if (fileName==null) {
            fileNameToUse = "Amendments-" + property.getReference() + "-" + getLeaseAmendmentType().toString() + "-" +  clockService.now().toString() +".xlsx";
        } else {
            if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
                fileNameToUse = fileName.concat(".xlsx");
            } else {
                fileNameToUse = fileName;
            }
        }
        return excelService.toExcel(getLines(), LeaseAmendmentImportLine.class, "lines", fileNameToUse);
    }

    private boolean hasChangingFrequency(final LeaseItem i, final LeaseAmendmentType leaseAmendmentType){
        final LeaseAmendmentType.Tuple<InvoicingFrequency, InvoicingFrequency> tuple = leaseAmendmentType.getFrequencyChanges()
                .stream()
                .filter(t -> t.x == i.getInvoicingFrequency())
                .findFirst().orElse(null);
        return tuple != null;
    }

    @Programmatic
    public List<Lease> activeLeasesOnAmendmentStartdateForProperty(){
        return property==null ?
                Collections.EMPTY_LIST :  leaseRepository.findByAssetAndActiveOnDate(property, leaseAmendmentType.getAmendmentStartDate());
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    LeaseAmendmentRepository leaseAmendmentRepository;

    @Inject
    LeaseAmendmentService leaseAmendmentService;

    @Inject
    ExcelService excelService;

    @Inject
    ClockService clockService;

}
