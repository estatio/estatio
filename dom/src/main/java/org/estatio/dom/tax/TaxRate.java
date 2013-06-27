package org.estatio.dom.tax;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
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
@javax.jdo.annotations.Query(name = "findByTaxAndDate", language = "JDOQL", value = "SELECT FROM org.estatio.dom.tax.TaxRate WHERE tax == :tax  && startDate >= :date && (endDate == null || endDate <= :date)")
public class TaxRate extends EstatioTransactionalObject<TaxRate> implements WithInterval<TaxRate> {

    public TaxRate() {
        super("tax, startDate desc");
    }
    
    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name="TAX_ID")
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

    @Persistent
    @MemberOrder(sequence = "2")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    private LocalDate endDate;

    @Persistent
    @MemberOrder(sequence = "3")
    @Optional
    @Disabled
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Hidden // TODO (where=Where.ALL_TABLES)
    @MemberOrder(name="Related", sequence = "9.1")
    @Named("Previous Rate")
    @Disabled
    @Optional
    @Override
    public TaxRate getPrevious() {
        return null;
    }

    @Hidden // TODO (where=Where.ALL_TABLES)
    @MemberOrder(name="Related", sequence = "9.2")
    @Named("Next Rate")
    @Disabled
    @Optional
    @Override
    public TaxRate getNext() {
        return null;
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

    @javax.jdo.annotations.Column(name="PREVIOUSRATE_ID")
    @javax.jdo.annotations.Persistent(mappedBy="nextRate")
    private TaxRate previousRate;

    @Hidden
    @MemberOrder(sequence = "1")
    public TaxRate getPreviousRate() {
        return previousRate;
    }

    public void setPreviousRate(final TaxRate previousRate) {
        this.previousRate = previousRate;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name="NEXTRATE_ID")
    private TaxRate nextRate;

    @Hidden
    @MemberOrder(sequence = "1")
    public TaxRate getNextRate() {
        return nextRate;
    }

    public void setNextRate(final TaxRate nextRate) {
        this.nextRate = nextRate;
    }

    public void modifyNextRate(final TaxRate nextRate) {
        TaxRate currentNextRate = getNextRate();
        if (nextRate == null || nextRate.equals(currentNextRate)) {
            return;
        }
        clearNextRate();
        nextRate.setPreviousRate(this);
        setNextRate(nextRate);
    }

    public void clearNextRate() {
        TaxRate currentNextRate = getNextRate();
        if (currentNextRate == null) {
            return;
        }
        currentNextRate.setPreviousRate(null);
        setNextRate(null);
    }

    // //////////////////////////////////////

    public TaxRate newRate(@Named("Start Date") LocalDate startDate, @Named("Percentage") BigDecimal percentage) {
        TaxRate rate = this.getTax().newRate(startDate, percentage);
        setNextRate(rate);
        rate.setPreviousRate(this);
        return rate;
    }


}
