package org.estatio.module.lease.dom.amendments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
import org.apache.isis.applib.services.background.BackgroundService2;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseStatus;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.lease.dom.amendments.LeaseAmendmentManager",
        auditing = Auditing.DISABLED,
        publishing = Publishing.DISABLED)
public class LeaseAmendmentManager {

    public LeaseAmendmentManager(){}

    public LeaseAmendmentManager(final  Property property){
        this();
        this.property = property;
    }

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
        if (getProperty()==null) return result; // Should not be possible
        if (getLeaseAmendmentType()!=null){
            final List<LeaseAmendment> amendments = leaseAmendmentRepository.findByType(getLeaseAmendmentType())
                    .stream()
                    .filter(a -> a.getLease().getProperty() == getProperty())
                    .collect(Collectors.toList());
            createLinesAndAddToResult(result, amendments);
        } else {
            final List<Lease> leasesByProperty = leaseRepository.findLeasesByProperty(property)
                    .stream()
                    .filter(l->l.getStatus()== LeaseStatus.ACTIVE || l.getStatus()==LeaseStatus.SUSPENDED_PARTIALLY)
                    .filter(l->getLeaseAmendmentType()!=null ? (l.getTenancyEndDate()==null || l.getTenancyEndDate().isAfter(getLeaseAmendmentType().getAmendmentStartDate())) : (l.getTenancyEndDate()==null || l.getTenancyEndDate().isAfter(clockService.now().withDayOfMonth(1))))
                    .collect(Collectors.toList());
            for (Lease lease : leasesByProperty){
                final List<LeaseAmendment> amendmentsForLeaseOfAllTypes = leaseAmendmentRepository.findByLease(lease);
                createLinesAndAddToResult(result, amendmentsForLeaseOfAllTypes);
            }
        }
        return result
                .stream()
                .sorted(Comparator.comparing(
                        LeaseAmendmentImportLine::getLeaseReference)
                        .thenComparing(LeaseAmendmentImportLine::getLeaseAmendmentType))
                .collect(Collectors.toList());
    }

    private void createLinesAndAddToResult(
            final List<LeaseAmendmentImportLine> result,
            final List<LeaseAmendment> amendments) {
        for (LeaseAmendment amendment : amendments){
            final List<LeaseAmendmentItem> discountItems = amendment.findItemsOfType(LeaseAmendmentItemType.DISCOUNT);
            if (discountItems.size()<2) {
                result.add(new LeaseAmendmentImportLine(amendment));
            } else {
                boolean first = true;
                for (LeaseAmendmentItem item : discountItems) {
                    if (first) {
                        result.add(new LeaseAmendmentImportLine(amendment));
                        first = false;
                    } else {
                        LeaseAmendmentItemForDiscount castedItem = (LeaseAmendmentItemForDiscount) item;
                        result.add(new LeaseAmendmentImportLine(amendment, castedItem));
                    }
                }
            }
        }
    }

    @Action(semantics = SemanticsOf.SAFE)
    public LeaseAmendmentManager filterByType(@Nullable final LeaseAmendmentType leaseAmendmentType){
        return new LeaseAmendmentManager(property, leaseAmendmentType);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public  LeaseAmendmentManager applyAllWithStatusApply(){
        for (LeaseAmendmentImportLine line : getLines()){
            if (line.getLeaseAmendmentState()==LeaseAmendmentState.APPLY) {
                final Lease lease = leaseRepository.findLeaseByReference(line.getLeaseReference());
                if (lease != null) {
                    final LeaseAmendment amendment = leaseAmendmentRepository
                            .findUnique(lease, line.getLeaseAmendmentType());
                    if (amendment != null && amendment.getState() != LeaseAmendmentState.APPLIED) {
                        backgroundService2.execute(amendment)
                                .apply();
                    }
                }
            }
        }
        return new LeaseAmendmentManager(getProperty(), getLeaseAmendmentType());
    }

    public String disableApplyAllWithStatusApply(){
        final Optional<LeaseAmendmentImportLine> optional = getLines().stream()
                .filter(l -> l.getLeaseAmendmentState() == LeaseAmendmentState.APPLY).findFirst();
        if (optional.isPresent()) {
            return null;
        } else {
            return String.format("No amendments with status APPLY present for property %s and type %s", getProperty().getReference(), getLeaseAmendmentType()!=null ? getLeaseAmendmentType() : "all types");
        }
    }


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public LeaseAmendmentManager applyAll(){
        for (LeaseAmendmentImportLine line : getLines()){
            final Lease lease = leaseRepository.findLeaseByReference(line.getLeaseReference());
            if (lease!=null) {
                final LeaseAmendment amendment = leaseAmendmentRepository.findUnique(lease, getLeaseAmendmentType());
                if (amendment!=null && amendment.getState()!=LeaseAmendmentState.APPLIED){
                    backgroundService2.execute(amendment).apply(); // we do not wrap on purpose here; when type has allowsBulkApply==true we do not care for the state of the amendment
                }
            }
        }
        return new LeaseAmendmentManager(getProperty(), getLeaseAmendmentType());
    }

    public boolean hideApplyAll(){
        if (getLeaseAmendmentType()!=null && getLeaseAmendmentType().getAllowsBulkApply()==true) return false;
        return true;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public LeaseAmendmentManager importAmendments(final Blob excelsheet){
        final List<LeaseAmendmentImportLine> lines = excelService
                .fromExcel(excelsheet, LeaseAmendmentImportLine.class, "lines");
        lines.forEach(l->l.importData());
        return this;
    }

    @Action(commandPersistence = CommandPersistence.NOT_PERSISTED)
    public Blob downloadNewAmendmentProposalsForType(final LeaseAmendmentType leaseAmendmentType, @Nullable final String fileName){
        List<LeaseAmendmentImportLine> newLines = new ArrayList<>();
        for (Lease lease : activeLeasesOnAmendmentStartdateForProperty(leaseAmendmentType)){
            final LeaseAmendment amendment = leaseAmendmentRepository.findUnique(lease, leaseAmendmentType);
            if (amendment==null) newLines.addAll(newLinesForLease(lease, leaseAmendmentType));
        }
        String fileNameToUse;
        if (fileName==null) {
            fileNameToUse = "New amendments-" + property.getReference();
            fileNameToUse = fileNameToUse + "-" + leaseAmendmentType.toString();
            fileNameToUse = fileNameToUse + "-" +  clockService.now().toString() +".xlsx";
        } else {
            if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
                fileNameToUse = fileName.concat(".xlsx");
            } else {
                fileNameToUse = fileName;
            }
        }
        return excelService.toExcel(newLines, LeaseAmendmentImportLine.class, "lines", fileNameToUse);
    }

    public LeaseAmendmentType default0DownloadNewAmendmentProposalsForType(){
        return this.getLeaseAmendmentType();
    }

    List<LeaseAmendmentImportLine> newLinesForLease(final Lease lease, final LeaseAmendmentType leaseAmendmentType){
        List<LeaseAmendmentImportLine> result = new ArrayList<>();
        final List<LeaseItem> discountCandidates = Lists.newArrayList(lease.getItems()).stream()
                .filter(i->leaseAmendmentType.getDiscountAppliesTo()!=null)
                .filter(i -> leaseAmendmentType.getDiscountAppliesTo().contains(i.getType()))
                .filter(i->i.getEffectiveInterval()!= null) // guard for 'inconsistent' data
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
            newLine.setInvoicingFrequencyOnLease(tuple.oldValue);
            newLine.setAmendedInvoicingFrequency(tuple.newValue);
        }
        result.add(newLine);
        return result;
    }

    @Action(commandPersistence = CommandPersistence.NOT_PERSISTED)
    public Blob download(@Nullable final String fileName){
        String fileNameToUse;
        if (fileName==null) {
            fileNameToUse = "Amendments-" + property.getReference();
            if (getLeaseAmendmentType()==null) {
                fileNameToUse = fileNameToUse + "-all-types";
            } else {
                fileNameToUse = fileNameToUse + "-" + getLeaseAmendmentType().toString();
            }
            fileNameToUse = fileNameToUse + "-" +  clockService.now().toString() +".xlsx";
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
                .filter(t -> t.oldValue == i.getInvoicingFrequency())
                .findFirst().orElse(null);
        return tuple != null;
    }

    @Programmatic
    public List<Lease> activeLeasesOnAmendmentStartdateForProperty(final LeaseAmendmentType leaseAmendmentType){
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

    @Inject
    BackgroundService2 backgroundService2;

}
