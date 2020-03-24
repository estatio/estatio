package org.estatio.module.lease.imports;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.message.MessageService;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForFixed;
import org.estatio.module.lease.dom.LeaseTermForTurnoverRent;
import org.estatio.module.lease.dom.LeaseTermRepository;
import org.estatio.module.lease.dom.LeaseTermStatus;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.LeaseTermForTurnOverRentSweImport"
)
public class LeaseTermForTurnOverRentSweImport implements ExcelFixtureRowHandler, Importable {

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String leaseReference;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String leaseExternalReference;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private LocalDate startDatePreviousYear;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private LocalDate endDatePreviousYear;

    @Getter @Setter
    @MemberOrder(sequence = "5")
    private BigDecimal valuePreviousYear;

    @Getter @Setter
    @MemberOrder(sequence = "6")
    private LocalDate startDate;

    @Getter @Setter
    @MemberOrder(sequence = "7")
    private LocalDate endDate;

    @Getter @Setter
    @MemberOrder(sequence = "8")
    private BigDecimal value;

    @Getter @Setter
    @MemberOrder(sequence = "9")
    private BigDecimal percentage;

    @Getter @Setter
    @MemberOrder(sequence = "10")
    private Integer year;

    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object o) {
        return importData(null);
    }

    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(Object previousRow) {
        Lease leaseToUpdate = leaseRepository.findLeaseByReference(getLeaseReference());
        if (leaseToUpdate == null) {
            messageService.warnUser(String.format("Lease with reference %s not found", getLeaseReference()));
            return Lists.newArrayList();
        }
        if (reasonLineInValid()!=null){
            messageService.warnUser(reasonLineInValid());
            return Lists.newArrayList();
        }
        if (leaseToUpdate.findItemsOfType(LeaseItemType.TURNOVER_RENT).size()>1){
            messageService.warnUser(String.format("Multiple lease items of type TURNOVER_RENT found on lease with reference %s; could not update.", getLeaseReference()));
            return Lists.newArrayList();
        }
        LeaseItem itemToUpdate = leaseToUpdate.findFirstItemOfType(LeaseItemType.TURNOVER_RENT);
        if (itemToUpdate == null) {
            messageService.warnUser(String.format("No lease item of type TURNOVER_RENT found on lease with reference %s", getLeaseReference()));
            return Lists.newArrayList();
        }

        if (getValuePreviousYear()!=null) {
            updateTerm(itemToUpdate, getStartDatePreviousYear(), getEndDatePreviousYear(), getValuePreviousYear());
        }
        if (getValue()!=null) {
            updateOrCreateTerm(itemToUpdate, getStartDate(), getEndDate(), getValue(), getPercentage());
        }

        return Lists.newArrayList();
    }

    String reasonLineInValid(){

        StringBuilder builder = new StringBuilder();
        if (getValuePreviousYear()!=null) {
            if (getStartDatePreviousYear()==null || getEndDatePreviousYear()==null){
                builder.append(String.format("Missing date found for previous year for lease with reference %s; please correct.", getLeaseReference()));
            }
            if (getStartDatePreviousYear()!=null && getStartDatePreviousYear().getYear() != getYear()-1){
                builder.append(String.format("Start date previous year should be in %s for lease with reference %s; please correct.", getYear()-1, getLeaseReference()));
            }
        }

        if (getValue()!=null) {
            if (getStartDate()==null || getEndDate()==null) {
                builder.append(String.format("Missing date found for lease with reference %s; please correct.", getLeaseReference()));
            }
            if (getStartDate()!=null && getStartDate().getYear() != getYear()){
                builder.append(String.format("Start date should be in %s for lease with reference %s; please correct.", getYear(), getLeaseReference()));
            }
        }


        LocalDateInterval previous = new LocalDateInterval(getStartDatePreviousYear(), getEndDatePreviousYear());
        LocalDateInterval current = new LocalDateInterval(getStartDate(), getEndDate());
        if (getValuePreviousYear()!=null && getValue()!=null && previous.overlaps(current)){
            builder.append(String.format("Overlapping interval found for lease with reference %s; please correct.", getLeaseReference()));
        }

        return builder.toString().isEmpty() ? null : builder.toString();
    }

    void updateOrCreateTerm(final LeaseItem itemToUpdate, final LocalDate startDate, final LocalDate endDate, final BigDecimal value, final BigDecimal percentage) {
        LeaseTermForTurnoverRent termToUpdate = (LeaseTermForTurnoverRent) leaseTermRepository.findByLeaseItemAndStartDate(itemToUpdate, startDate);
        final List<LeaseTerm> possibleOverlappingTerms = getOverlappingTerms(itemToUpdate, startDate, endDate);

        if (!possibleOverlappingTerms.isEmpty() && possibleOverlappingTerms.size()>1){

            messageService.warnUser(String.format("Multiple overlapping previous terms found for lease %s; please correct", getLeaseReference()));

        } else {

            if (termToUpdate==null) {
                if (!possibleOverlappingTerms.isEmpty()){
                    LeaseTerm overlappingTerm = possibleOverlappingTerms.get(0);
                    if (overlappingTerm.getStartDate().isAfter(startDate)){
                        termToUpdate = (LeaseTermForTurnoverRent) overlappingTerm;
                    } else {
                        overlappingTerm.setEndDate(startDate.minusDays(1));
                    }
                }
            }

            if (termToUpdate!=null){
                termToUpdate.setStartDate(startDate);
                termToUpdate.setEndDate(endDate);
                termToUpdate.setManualTurnoverRent(value);
                setPercentageOfTerm(termToUpdate, percentage);
                termToUpdate.setStatus(LeaseTermStatus.APPROVED);
            } else {
                LeaseTermForTurnoverRent newTerm = (LeaseTermForTurnoverRent) itemToUpdate.newTerm(startDate, endDate);
                newTerm.setManualTurnoverRent(value);
                setPercentageOfTerm(newTerm, percentage);
                newTerm.setStatus(LeaseTermStatus.APPROVED);
            }

        }

    }

    void setPercentageOfTerm(LeaseTermForTurnoverRent term, BigDecimal percentage) {
        if (percentage!=null) {
            term.setTurnoverRentRule(percentageToTurnoverRentRuleString(percentage));
        } else {
            LeaseTermForTurnoverRent previousTerm = (LeaseTermForTurnoverRent) term.getPrevious();
            while(previousTerm!=null) {
                if (previousTerm.getTurnoverRentRule()!=null) {
                    term.setTurnoverRentRule(previousTerm.getTurnoverRentRule());
                    return;
                } else {
                    previousTerm = (LeaseTermForTurnoverRent) previousTerm.getPrevious();
                }
            }
            messageService.warnUser(String.format("Percentage not set and no previous term found for lease %s; please correct", getLeaseReference()));
        }
    }

    @Programmatic
    public static String percentageToTurnoverRentRuleString(final BigDecimal percentage){
        if (percentage==null) return null;
        return percentage.toString().replaceFirst("\\.0*$", "");
    }


    void updateTerm(final LeaseItem itemToUpdate, final LocalDate startDate, final LocalDate endDate, final BigDecimal value){
        LeaseTermForTurnoverRent termToUpdate = (LeaseTermForTurnoverRent) leaseTermRepository.findByLeaseItemAndStartDate(itemToUpdate, startDate);
        final List<LeaseTerm> possibleOverlappingTerms = getOverlappingTerms(itemToUpdate, startDate, endDate);

        if (termToUpdate!=null){
            termToUpdate.setManualTurnoverRent(value);
            termToUpdate.setEndDate(endDate);
            termToUpdate.setStatus(LeaseTermStatus.APPROVED);
        } else if (!possibleOverlappingTerms.isEmpty() && possibleOverlappingTerms.size()>1){
            messageService.warnUser(String.format("Multiple overlapping previous terms found for lease %s; please correct", getLeaseReference()));
        } else if (!possibleOverlappingTerms.isEmpty()){
                LeaseTerm overlappingTerm = possibleOverlappingTerms.get(0);
                if (overlappingTerm.getStartDate().isBefore(startDate)){
                    termToUpdate = (LeaseTermForTurnoverRent) overlappingTerm.split(startDate);
                    termToUpdate.setManualTurnoverRent(value);
                    termToUpdate.setEndDate(endDate);
                    termToUpdate.setStatus(LeaseTermStatus.APPROVED);
                }
        } else {
            messageService.warnUser(String.format("Previous term not found for lease %s", getLeaseReference()));
        }

    }

    private List<LeaseTerm> getOverlappingTerms(
            final LeaseItem itemToUpdate,
            final LocalDate startDate,
            final LocalDate endDate) {
        return Lists.newArrayList(itemToUpdate.getTerms())
                .stream()
                .filter(term -> term.getInterval().overlaps(new LocalDateInterval(startDate, endDate)))
                .filter(term -> term.getStatus() == LeaseTermStatus.APPROVED)
                .collect(Collectors.toList());
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    LeaseTermRepository leaseTermRepository;

    @Inject
    MessageService messageService;
}
