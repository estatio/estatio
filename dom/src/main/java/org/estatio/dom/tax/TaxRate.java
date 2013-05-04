package org.estatio.dom.tax;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.estatio.dom.EstatioTransactionalObject;
import org.joda.time.LocalDate;

@PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class TaxRate extends EstatioTransactionalObject implements Comparable<TaxRate> {

    // {{ Tax (property)
    private Tax tax;

    @Title
    @MemberOrder(sequence = "1")
    public Tax getTax() {
        return tax;
    }

    public void setTax(final Tax tax) {
        this.tax = tax;
    }

    // }}

    // {{ StartDate (property)
    private LocalDate startDate;

    @Persistent
    @MemberOrder(sequence = "2")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    // }}

    // {{ EndDate (property)
    private LocalDate endDate;

    @Persistent
    @MemberOrder(sequence = "3")
    @Optional
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // {{ Percentage (property)
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

    // }}

    // {{ PreviousRate (property)
    private TaxRate propertyName;

    @Hidden
    @MemberOrder(sequence = "1")
    public TaxRate getPreviousRate() {
        return propertyName;
    }

    public void setPreviousRate(final TaxRate propertyName) {
        this.propertyName = propertyName;
    }

    // }}

    // {{ NextRate (property)
    private TaxRate nextRate;

    @Hidden
    @MemberOrder(sequence = "1")
    public TaxRate getNextRate() {
        return nextRate;
    }

    public void setNextRate(final TaxRate nextRate) {
        this.nextRate = nextRate;
    }

    // }}

    // {{ NewRate (action)
    public TaxRate newRate(@Named("Start Date") LocalDate startDate, @Named("Percentage") BigDecimal percentage) {
        TaxRate rate = this.getTax().newRate(startDate, percentage);
        setNextRate(rate);
        rate.setPreviousRate(this);
        return rate;
    }

    // }}

    @Override
    public int compareTo(TaxRate other) {
        return this.getStartDate().compareTo(other.getStartDate());
    }
}
