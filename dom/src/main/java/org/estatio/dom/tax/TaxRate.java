package org.estatio.dom.tax;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name = "findByTaxAndDate", language = "JDOQL",
            value = "SELECT "
                    + "FROM org.estatio.dom.tax.TaxRate "
                    + "WHERE tax == :tax"
                    + "  && startDate >= :date"
                    + "  && (endDate == null || endDate <= :date)"),
    @javax.jdo.annotations.Query(
            name = "findByTaxAndStartDate", language = "JDOQL",
            value = "SELECT "
                    + "FROM org.estatio.dom.tax.TaxRate "
                    + "WHERE tax == :tax "
                    + "&& startDate == :startDate"),
    @javax.jdo.annotations.Query(
            name = "findByTaxAndEndDate", language = "JDOQL",
            value = "SELECT "
                    + "FROM org.estatio.dom.tax.TaxRate "
                    + "WHERE tax == :tax "
                    + "&& endDate == :endDate"),
})
@MemberGroups({ "General", "Dates", "Related" })
public class TaxRate extends EstatioTransactionalObject<TaxRate> implements WithInterval<TaxRate> {

    public TaxRate() {
        super("tax, startDate desc");
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "TAX_ID")
    private Tax tax;

    @Title
    @MemberOrder(sequence = "1")
    public Tax getTax() {
        return tax;
    }

    public void setTax(final Tax tax) {
        this.tax = tax;
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @Optional
    @MemberOrder(name = "Dates", sequence = "2")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    public void modifyStartDate(final LocalDate startDate) {
        final LocalDate currentStartDate = getStartDate();
        if (startDate == null || startDate.equals(currentStartDate)) {
            return;
        }
        setStartDate(startDate);
    }

    public void clearStartDate() {
        LocalDate currentStartDate = getStartDate();
        if (currentStartDate == null) {
            return;
        }
        setStartDate(null);
    }


    // //////////////////////////////////////

    private LocalDate endDate;

    @MemberOrder(name = "Dates", sequence = "3")
    @Optional
    @Disabled
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    public void modifyEndDate(final LocalDate endDate) {
        final LocalDate currentEndDate = getEndDate();
        if (endDate == null || endDate.equals(currentEndDate)) {
            return;
        }
        setEndDate(endDate);
    }

    public void clearEndDate() {
        LocalDate currentEndDate = getEndDate();
        if (currentEndDate == null) {
            return;
        }
        setEndDate(null);
    }

    // //////////////////////////////////////

    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    // //////////////////////////////////////

    private BigDecimal percentage;

    @Title
    @MemberOrder(sequence = "4")
    @Column(scale = 2)
    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(final BigDecimal percentage) {
        this.percentage = percentage;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "PREVIOUS_ID")
    @javax.jdo.annotations.Persistent(mappedBy = "next")
    private TaxRate previous;

    @Hidden(where = Where.ALL_TABLES)
    @MemberOrder(name = "Related", sequence = "9.1")
    @Named("Previous Rate")
    @Disabled
    @Optional
    @Override
    public TaxRate getPrevious() {
        return previous;
    }

    public void setPrevious(final TaxRate previous) {
        this.previous = previous;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "NEXT_ID")
    private TaxRate next;

    @Hidden(where = Where.ALL_TABLES)
    @MemberOrder(name = "Related", sequence = "9.2")
    @Named("Next Rate")
    @Disabled
    @Optional
    @Override
    public TaxRate getNext() {
        return next;
    }

    public void setNext(final TaxRate next) {
        this.next = next;
    }

    public void modifyNext(final TaxRate next) {
        TaxRate currentNextRate = getNext();
        if (next == null || next.equals(currentNextRate)) {
            return;
        }
        clearNext();
        next.setPrevious(this);
        setNext(next);
    }

    public void clearNext() {
        TaxRate currentNext = getNext();
        if (currentNext == null) {
            return;
        }
        currentNext.setPrevious(null);
        setNext(null);
    }

    // //////////////////////////////////////

    public TaxRate newRate(
            final @Named("Start Date") LocalDate startDate,
            final @Named("Percentage") BigDecimal percentage) {
        TaxRate rate = this.getTax().newRate(startDate, percentage);
        setNext(rate);
        rate.setPrevious(this);
        return rate;
    }

}
