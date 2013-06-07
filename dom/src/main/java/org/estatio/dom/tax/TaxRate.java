package org.estatio.dom.tax;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Objects;
import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class TaxRate extends EstatioTransactionalObject implements Comparable<TaxRate>, WithInterval {

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

    // }}

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

    private TaxRate propertyName;

    @Hidden
    @MemberOrder(sequence = "1")
    public TaxRate getPreviousRate() {
        return propertyName;
    }

    public void setPreviousRate(final TaxRate propertyName) {
        this.propertyName = propertyName;
    }

    // //////////////////////////////////////
    
    private TaxRate nextRate;

    @Hidden
    @MemberOrder(sequence = "1")
    public TaxRate getNextRate() {
        return nextRate;
    }

    public void setNextRate(final TaxRate nextRate) {
        this.nextRate = nextRate;
    }

    // //////////////////////////////////////
    
    public TaxRate newRate(@Named("Start Date") LocalDate startDate, @Named("Percentage") BigDecimal percentage) {
        TaxRate rate = this.getTax().newRate(startDate, percentage);
        setNextRate(rate);
        rate.setPreviousRate(this);
        return rate;
    }

    // //////////////////////////////////////

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("tax", getTax()!=null?getTax().getReference():null)
                .add("startDate", getStartDate())
                .toString();
    }

    // //////////////////////////////////////

    @Override
    public int compareTo(TaxRate other) {
        return ORDERING_BY_TAX
                .compound(ORDERING_BY_START_DATE_DESC)
                .compare(this, other);
    }

    private final static Ordering<TaxRate> ORDERING_BY_TAX = new Ordering<TaxRate>(){
        public int compare(TaxRate p, TaxRate q) {
            return Ordering.natural().nullsFirst().compare(p.getTax(), q.getTax());
        }
    };

}
