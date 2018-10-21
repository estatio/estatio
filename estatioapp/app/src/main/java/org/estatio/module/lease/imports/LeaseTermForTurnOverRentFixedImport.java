package org.estatio.module.lease.imports;

import java.math.BigDecimal;
import java.util.List;

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
import org.estatio.module.lease.dom.LeaseTermForFixed;
import org.estatio.module.lease.dom.LeaseTermRepository;
import org.estatio.module.lease.dom.LeaseTermStatus;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.LeaseTermForTurnOverRentFixedImport"
)
public class LeaseTermForTurnOverRentFixedImport implements ExcelFixtureRowHandler, Importable {

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
        if (leaseToUpdate.findItemsOfType(LeaseItemType.TURNOVER_RENT_FIXED).size()>1){
            messageService.warnUser(String.format("Multiple lease items of type TURNOVER_RENT_FIXED found on lease with reference %s; could not update.", getLeaseReference()));
            return Lists.newArrayList();
        }
        LeaseItem itemToUpdate = leaseToUpdate.findFirstItemOfType(LeaseItemType.TURNOVER_RENT_FIXED);
        if (itemToUpdate == null) {
            messageService.warnUser(String.format("No lease item of type TURNOVER_RENT_FIXED found on lease with reference %s", getLeaseReference()));
            return Lists.newArrayList();
        }

        if (getValuePreviousYear()!=null && getStartDatePreviousYear() !=null) {
            updateOrCreateTerm(itemToUpdate, getStartDatePreviousYear(), getStartDate()!=null ? getStartDate().minusDays(1) : null, getValuePreviousYear());
        }
        if (getValue()!=null && getStartDate() !=null) {
            updateOrCreateTerm(itemToUpdate, getStartDate(), getEndDate(), getValue());
        }

        return Lists.newArrayList();
    }

    String reasonLineInValid(){

        StringBuilder builder = new StringBuilder();
        if (getValuePreviousYear()!=null && (getStartDatePreviousYear()==null || getEndDatePreviousYear()==null)){
            builder.append(String.format("Missing date found for previous year for lease with reference %s; please correct.", getLeaseReference()));
        }

        if (getValue()!=null && (getStartDate()==null || getEndDate()==null)){
            builder.append(String.format("Missing date found for lease with reference %s; please correct.", getLeaseReference()));
        }

        LocalDateInterval previous = new LocalDateInterval(getStartDatePreviousYear(), getEndDatePreviousYear());
        LocalDateInterval current = new LocalDateInterval(getStartDate(), getEndDate());
        if (getValuePreviousYear()!=null && getValue()!=null && previous.overlaps(current)){
            builder.append(String.format("Overlapping interval found for lease with reference %s; please correct.", getLeaseReference()));
        }

        return builder.toString().isEmpty() ? null : builder.toString();
    }

    void updateOrCreateTerm(final LeaseItem itemToUpdate, final LocalDate startDate, final LocalDate endDate, final BigDecimal value) {
        LeaseTermForFixed termToUpdate = (LeaseTermForFixed) leaseTermRepository.findByLeaseItemAndStartDate(itemToUpdate, startDate);
        if (termToUpdate!=null){
            termToUpdate.setValue(value);
            termToUpdate.setEndDate(endDate!=null ? endDate : termToUpdate.getEndDate());
            // TODO: this means that a term with turnover rent value of 0.00 never will be approved by this import. Is that what the users expect?
            // TODO: also I observed a case with negative value! So maybe abs must be taken?
            if (value!=null && value.compareTo(BigDecimal.ZERO) > 0) termToUpdate.setStatus(LeaseTermStatus.APPROVED);
        } else {
            LeaseTermForFixed newTerm = (LeaseTermForFixed) itemToUpdate.newTerm(startDate, endDate!=null ? endDate : startDate.plusYears(1).minusDays(1));
            newTerm.setValue(value);
            // TODO: this means that a term with turnover rent value of 0.00 never will be approved by this import. Is that what the users expect?
            // TODO: also I observed a case with negative value! So maybe abs must be taken?
            if (value!=null && value.compareTo(BigDecimal.ZERO) > 0) newTerm.setStatus(LeaseTermStatus.APPROVED);
        }
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    LeaseTermRepository leaseTermRepository;

    @Inject
    MessageService messageService;
}
