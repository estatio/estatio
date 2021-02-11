package org.estatio.module.lease.dom.amendments;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.isisaddons.module.security.app.user.MeService;

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

    public LeaseAmendmentManager(final  Property property){
        this();
        this.property = property;
    }

    public LeaseAmendmentManager(final Property property, final LeaseAmendmentTemplate leaseAmendmentTemplate, final LeaseAmendmentState leaseAmendmentState){
        this();
        this.property = property;
        this.leaseAmendmentTemplate = leaseAmendmentTemplate;
        this.state = leaseAmendmentState;
    }

    public String title(){
        return "Lease amendment manager";
    }

    @Getter @Setter
    private Property property;

    @Getter @Setter
    private LeaseAmendmentTemplate leaseAmendmentTemplate;

    @Getter @Setter
    private LeaseAmendmentState state;

    private String myAtPath() {
        if (meService==null) return "/";
        return meService.me().getAtPath();
    }

    final private ArrayList<String> myAtPaths = Lists.newArrayList(myAtPath().split(";"));

    @Action(semantics = SemanticsOf.SAFE)
    public List<LeaseAmendmentImportLine> getLines(){
        List<LeaseAmendmentImportLine> result = new ArrayList<>();

        if (getProperty()==null) {

            if (getLeaseAmendmentTemplate() != null) {
                if (getState() == null) {
                    createLinesAndAddToResult(result, leaseAmendmentRepository.findByTemplate(getLeaseAmendmentTemplate()));
                } else {
                    createLinesAndAddToResult(result, leaseAmendmentRepository.findByTemplateAndState(
                            getLeaseAmendmentTemplate(), getState()));
                }
            } else {
                if (getState() != null) {
                    for (String at : myAtPaths){
                        createLinesAndAddToResult(result, leaseAmendmentRepository.findByState(getState()).stream()
                                .filter(la->la.getAtPath().contains(at))
                                .collect(Collectors.toList()));
                    }
                } else {
                    for (String at : myAtPaths){
                        createLinesAndAddToResult(result, leaseAmendmentRepository.listAll().stream()
                                .filter(la->la.getAtPath().contains(at))
                                .collect(Collectors.toList()));
                    }
                }
            }


        } else {

            if (getLeaseAmendmentTemplate() != null) {
                if (getState() == null) {
                    createLinesAndAddToResult(result, leaseAmendmentRepository.findByTemplateAndProperty(
                            getLeaseAmendmentTemplate(), getProperty()));
                } else {
                    createLinesAndAddToResult(result, leaseAmendmentRepository.findByTemplateAndStateAndProperty(
                            getLeaseAmendmentTemplate(), getState(), getProperty()));
                }
            } else {
                if (getState() != null) {
                    createLinesAndAddToResult(result,
                            leaseAmendmentRepository.findByPropertyAndState(getProperty(), getState()));
                } else {
                    createLinesAndAddToResult(result, leaseAmendmentRepository.findByProperty(getProperty()));
                }
            }
        }
        return result
                .stream()
                .sorted(Comparator.comparing(
                        LeaseAmendmentImportLine::getLeaseReference)
                        .thenComparing(LeaseAmendmentImportLine::getLeaseAmendmentTemplate))
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
    public LeaseAmendmentManager filterByTemplate(@Nullable final LeaseAmendmentTemplate leaseAmendmentTemplate){
        return new LeaseAmendmentManager(getProperty(), leaseAmendmentTemplate, getState());
    }

    public List<LeaseAmendmentTemplate> choices0FilterByTemplate() {
        if (getProperty()!=null) {

            return Arrays.asList(LeaseAmendmentTemplate.values())
                    .stream()
                    .filter(lat -> getProperty().getAtPath().startsWith(lat.getAtPath()))
                    .collect(Collectors.toList());

        } else {

            final List<LeaseAmendmentTemplate> result = new ArrayList<>();
            for (String at : myAtPaths){
                result.addAll(Arrays.asList(LeaseAmendmentTemplate.values()).stream()
                        .filter(lt->lt.getAtPath().contains(at))
                        .collect(Collectors.toList()));
            }
            return result;

        }
    }

    @Action(semantics = SemanticsOf.SAFE)
    public LeaseAmendmentManager filterByState(@Nullable final LeaseAmendmentState leaseAmendmentState){
        return new LeaseAmendmentManager(getProperty(), getLeaseAmendmentTemplate(), leaseAmendmentState);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public LeaseAmendmentManager selectProperty(@Nullable final Property property){
        return new LeaseAmendmentManager(property, getLeaseAmendmentTemplate(), getState());
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public  LeaseAmendmentManager applyAllWithStatusApply(){
        for (LeaseAmendmentImportLine line : getLines()){
            if (line.getLeaseAmendmentState()==LeaseAmendmentState.APPLY) {
                final Lease lease = leaseRepository.findLeaseByReference(line.getLeaseReference());
                if (lease != null) {
                    final LeaseAmendment amendment = leaseAmendmentRepository
                            .findUnique(lease, line.getLeaseAmendmentTemplate());
                    if (amendment != null && amendment.getState() != LeaseAmendmentState.APPLIED) {
                        if (amendment.getDateSigned()==null){
                            amendment.sign(clockService.now());
                        }
                        backgroundService2.execute(amendment)
                                .apply();
                    }
                }
            }
        }
        return new LeaseAmendmentManager(getProperty(), getLeaseAmendmentTemplate(), getState());
    }

    public String disableApplyAllWithStatusApply(){
        final Optional<LeaseAmendmentImportLine> optional = getLines().stream()
                .filter(l -> l.getLeaseAmendmentState() == LeaseAmendmentState.APPLY).findFirst();
        if (optional.isPresent()) {
            return null;
        } else {
            if (getProperty()!=null) {
                return String.format("No amendments with status APPLY present for property %s and type %s",
                        getProperty().getReference(),
                        getLeaseAmendmentTemplate() != null ? getLeaseAmendmentTemplate() : "all types");
            } else {
                return String.format("No amendments with status APPLY present for type %s",
                        getLeaseAmendmentTemplate() != null ? getLeaseAmendmentTemplate() : "all types");
            }
        }
    }


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public LeaseAmendmentManager applyAll(){
        for (LeaseAmendmentImportLine line : getLines()){
            final Lease lease = leaseRepository.findLeaseByReference(line.getLeaseReference());
            if (lease!=null) {
                final LeaseAmendment amendment = leaseAmendmentRepository.findUnique(lease, getLeaseAmendmentTemplate());
                if (amendment!=null && amendment.getState()!=LeaseAmendmentState.APPLIED){
                    backgroundService2.execute(amendment).apply(); // we do not wrap on purpose here; when type has allowsBulkApply==true we do not care for the state of the amendment
                }
            }
        }
        return new LeaseAmendmentManager(getProperty(), getLeaseAmendmentTemplate(), getState());
    }

    public boolean hideApplyAll(){
        if (getLeaseAmendmentTemplate()!=null && getLeaseAmendmentTemplate().getAllowsBulkApply()==true) return false;
        return true;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public LeaseAmendmentManager importAmendments(final Blob excelsheet){
        final List<LeaseAmendmentImportLine> lines = excelService
                .fromExcel(excelsheet, LeaseAmendmentImportLine.class, "lines");
        LeaseAmendmentImportLine previous = null;
        for (LeaseAmendmentImportLine line : lines){
            line.importData(previous);
            previous = line;
        }
        return this;
    }

    @Action(commandPersistence = CommandPersistence.NOT_PERSISTED)
    public Blob downloadNewAmendmentProposalsForTemplate(final LeaseAmendmentTemplate leaseAmendmentTemplate, @Nullable final String fileName){
        List<LeaseAmendmentImportLine> newLines = new ArrayList<>();
        for (Lease lease : activeLeasesOnAmendmentStartdateForProperty(leaseAmendmentTemplate)){
            final LeaseAmendment amendment = leaseAmendmentRepository.findUnique(lease, leaseAmendmentTemplate);
            if (amendment==null) newLines.addAll(newLinesForLease(lease, leaseAmendmentTemplate));
        }
        String fileNameToUse;
        if (fileName==null) {
            fileNameToUse = "New amendments-" + property.getReference();
            fileNameToUse = fileNameToUse + "-" + leaseAmendmentTemplate.toString();
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

    public LeaseAmendmentTemplate default0DownloadNewAmendmentProposalsForTemplate(){
        return this.getLeaseAmendmentTemplate();
    }

    public String disableDownloadNewAmendmentProposalsForTemplate(){
        if (getProperty()==null) return "A property should be chosen in order to use this action";
        return null;
    }

    List<LeaseAmendmentImportLine> newLinesForLease(final Lease lease, final LeaseAmendmentTemplate leaseAmendmentTemplate){
        List<LeaseAmendmentImportLine> result = new ArrayList<>();
        final List<LeaseItem> discountCandidates = Lists.newArrayList(lease.getItems()).stream()
                .filter(i-> leaseAmendmentTemplate.getDiscountAppliesTo()!=null)
                .filter(i -> leaseAmendmentTemplate.getDiscountAppliesTo().contains(i.getType()))
                .filter(i->i.getEffectiveInterval()!= null) // guard for 'inconsistent' data
                .filter(i->i.getEffectiveInterval().overlaps(LocalDateInterval.including(leaseAmendmentTemplate.getDiscountStartDate(), leaseAmendmentTemplate
                        .getDiscountEndDate())))
                .collect(Collectors.toList());
        final List<LeaseItem> frequencyChangeCandidates = Lists.newArrayList(lease.getItems()).stream()
                .filter(i-> leaseAmendmentTemplate.getFrequencyChangeAppliesTo()!=null)
                .filter(i-> leaseAmendmentTemplate.getFrequencyChangeAppliesTo().contains(i.getType()))
                .filter(i->hasChangingFrequency(i, leaseAmendmentTemplate))
                .filter(i->i.getEffectiveInterval()!=null)
                .filter(i->i.getEffectiveInterval().overlaps(LocalDateInterval.including(leaseAmendmentTemplate.getFrequencyChangeStartDate(), leaseAmendmentTemplate
                        .getFrequencyChangeEndDate())))
                .collect(Collectors.toList());
        LeaseAmendmentImportLine newLine = new LeaseAmendmentImportLine();
        newLine.setLeaseAmendmentState(LeaseAmendmentState.PROPOSED);
        newLine.setLeaseReference(lease.getReference());
        newLine.setLeaseAmendmentTemplate(leaseAmendmentTemplate);
        newLine.setStartDate(leaseAmendmentTemplate.getAmendmentStartDate());
        if (!discountCandidates.isEmpty()){
            newLine.setDiscountPercentage(leaseAmendmentTemplate.getDiscountPercentage());
            newLine.setDiscountStartDate(leaseAmendmentTemplate.getDiscountStartDate());
            newLine.setDiscountEndDate(leaseAmendmentTemplate.getDiscountEndDate());
            newLine.setDiscountApplicableTo(LeaseAmendmentItem.applicableToToString(leaseAmendmentTemplate.getDiscountAppliesTo()));
        }
        if (!frequencyChangeCandidates.isEmpty()){
            newLine.setFrequencyChangeStartDate(leaseAmendmentTemplate.getFrequencyChangeStartDate());
            newLine.setFrequencyChangeEndDate(leaseAmendmentTemplate.getFrequencyChangeEndDate());
            newLine.setFrequencyChangeApplicableTo(LeaseAmendmentItem.applicableToToString(leaseAmendmentTemplate.getFrequencyChangeAppliesTo()));
            // TODO: now we pick the invoicing frequency from the first item encountered; .. this is cosmetics only and when we use the amendment proposal for import
            final LeaseAmendmentTemplate.Tuple<InvoicingFrequency, InvoicingFrequency> tuple = leaseAmendmentService
                    .findInvoiceFrequencyTupleOnfirstFrequencyChangeCandidate(lease, leaseAmendmentTemplate);
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
            if (getProperty()!=null) {
                fileNameToUse = "Amendments-" + property.getReference();
            } else {
                fileNameToUse = "Amendments-";
            }
            if (getLeaseAmendmentTemplate()==null) {
                fileNameToUse = fileNameToUse + "-all-types";
            } else {
                fileNameToUse = fileNameToUse + "-" + getLeaseAmendmentTemplate().toString();
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

    private boolean hasChangingFrequency(final LeaseItem i, final LeaseAmendmentTemplate leaseAmendmentTemplate){
        final LeaseAmendmentTemplate.Tuple<InvoicingFrequency, InvoicingFrequency> tuple = leaseAmendmentTemplate.getFrequencyChanges()
                .stream()
                .filter(t -> t.oldValue == i.getInvoicingFrequency())
                .findFirst().orElse(null);
        return tuple != null;
    }

    @Programmatic
    public List<Lease> activeLeasesOnAmendmentStartdateForProperty(final LeaseAmendmentTemplate leaseAmendmentTemplate){
        return property==null ?
                Collections.EMPTY_LIST :  leaseRepository.findByAssetAndActiveOnDate(property, leaseAmendmentTemplate.getAmendmentStartDate());
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

    @Inject
    MeService meService;

}
