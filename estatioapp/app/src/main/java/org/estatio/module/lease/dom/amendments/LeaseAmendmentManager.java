package org.estatio.module.lease.dom.amendments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.CommandPersistence;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.servletapi.dom.HttpServletRequestProvider;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseRepository;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@DomainObject(objectType = "org.estatio.module.lease.dom.amendments.LeaseAmendmentManager",
        auditing = Auditing.DISABLED,
        publishing = Publishing.DISABLED)
@XmlRootElement
@XmlType()
@XmlAccessorType(XmlAccessType.FIELD)
public class LeaseAmendmentManager {

    public LeaseAmendmentManager(){}

    public LeaseAmendmentManager(final Property property, final LeaseAmendmentType leaseAmendmentType){
        this.property = property;
        this.leaseAmendmentType = leaseAmendmentType;
    }

    public String title(){
        return "Lease amendment manager";
    }

    @Getter
    private Property property;

    @Getter
    private LeaseAmendmentType leaseAmendmentType;

    // INCSUP-535: Because of https://issues.apache.org/jira/browse/ISIS-2358, we use the session to preserve the state of the viewmodel
    public List<LeaseAmendmentImportLine> getLines() {
        final List<LeaseAmendmentImportLine> attribute = (List<LeaseAmendmentImportLine>) httpServletRequestProvider
                .getServletRequest().getSession().getAttribute(this.getClass().getName());
        return attribute!=null ? attribute : org.assertj.core.util.Lists.emptyList();
    }

    public void setLines(final List<LeaseAmendmentImportLine> lines) {
        httpServletRequestProvider.getServletRequest().getSession().setAttribute(this.getClass().getName(), lines);
    }

    @Action
    public LeaseAmendmentManager generateLines(){
        getLines().clear();
        List<LeaseAmendmentImportLine> result = new ArrayList<>();
        for (Lease lease : activeLeasesOnAmendmentStartdateForProperty()){
            final List<LeaseItem> discountCandidates = Lists.newArrayList(lease.getItems()).stream()
                    .filter(i -> leaseAmendmentType.getDiscountAppliesTo().contains(i.getType()))
                    .filter(i->i.getEffectiveInterval().overlaps(LocalDateInterval.including(leaseAmendmentType.getDiscountStartDate(), leaseAmendmentType
                            .getDiscountEndDate())))
                    .collect(Collectors.toList());
            final List<LeaseItem> frequencyChangeCandidates = Lists.newArrayList(lease.getItems()).stream()
                    .filter(i-> leaseAmendmentType.getFrequencyChangeAppliesTo().contains(i.getType()))
                    .filter(i->hasChangingFrequency(i, leaseAmendmentType))
                    .filter(i->i.getEffectiveInterval().overlaps(LocalDateInterval.including(leaseAmendmentType.getFrequencyChangeStartDate(), leaseAmendmentType
                            .getFrequencyChangeEndDate())))
                    .collect(Collectors.toList());
            LeaseAmendmentImportLine newLine = new LeaseAmendmentImportLine();
            newLine.setLeaseReference(lease.getReference());
            newLine.setLeaseAmendmentType(leaseAmendmentType);
            if (!discountCandidates.isEmpty()){
                newLine.setDiscountPercentage(leaseAmendmentType.getDiscountPercentage());
                newLine.setDiscountStartDate(leaseAmendmentType.getDiscountStartDate());
                newLine.setDiscountEndDate(leaseAmendmentType.getDiscountEndDate());
                newLine.setDiscountApplicableTo(LeaseAmendmentItem.applicableToToString(leaseAmendmentType.getDiscountAppliesTo()));
            }
            if (!frequencyChangeCandidates.isEmpty()){
                newLine.setFrequencyStartDate(leaseAmendmentType.getFrequencyChangeStartDate());
                newLine.setFrequencyEndDate(leaseAmendmentType.getFrequencyChangeEndDate());
                newLine.setFrequencyApplicableTo(LeaseAmendmentItem.applicableToToString(leaseAmendmentType.getFrequencyChangeAppliesTo()));

                // TODO: now we pick the invoicing frequency from the first item encountered; .. this is cosmetics only and when we use the amendment proposal for import
                final InvoicingFrequency freqOnLeaseItem = frequencyChangeCandidates.stream()
                        .map(i -> i.getInvoicingFrequency()).findFirst().orElse(null);
                newLine.setInvoicingFrequencyOnLease(freqOnLeaseItem);
                final InvoicingFrequency amendedFreq = leaseAmendmentType.getFrequencyChanges().stream()
                        .filter(t -> t.x == freqOnLeaseItem).map(t -> t.y).findFirst().orElse(null);
                newLine.setAmendedInvoicingFrequency(amendedFreq);
            }
            result.add(newLine);
        }
        setLines(result);
        return this;
    }

    @Action(commandPersistence = CommandPersistence.NOT_PERSISTED, associateWith = "lines", associateWithSequence = "1")
    public Blob download(
            @Nullable final String fileName,
            @Nullable final List<LeaseAmendmentImportLine> lines){
        String fileNameToUse;
        if (fileName==null) {
            fileNameToUse = "Amendments.xlsx";
        } else {
            if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
                fileNameToUse = fileName.concat(".xlsx");
            } else {
                fileNameToUse = fileName;
            }
        }
        return excelService.toExcel(lines, LeaseAmendmentImportLine.class, "lines", fileNameToUse);
    }

    public LeaseAmendmentManager upload(final Blob excelsheet){
        final List<LeaseAmendmentImportLine> lines = excelService
                .fromExcel(excelsheet, LeaseAmendmentImportLine.class, "lines");
        setLines(lines);
        return this;
    }

    @Action(associateWith = "lines", associateWithSequence = "2")
    public LeaseAmendmentManager apply(@Nullable final List<LeaseAmendmentImportLine> lines){
        lines.forEach(l->l.importData());
        return this;
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
        return leaseRepository.findByAssetAndActiveOnDate(property, leaseAmendmentType.getAmendmentStartDate());
    }

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    LeaseRepository leaseRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    ExcelService excelService;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    HttpServletRequestProvider httpServletRequestProvider;

}
