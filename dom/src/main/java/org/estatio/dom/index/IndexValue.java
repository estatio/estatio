package org.estatio.dom.index;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioRefDataObject;
import org.joda.time.LocalDate;

@PersistenceCapable
@Immutable
public class IndexValue extends EstatioRefDataObject implements Comparable<IndexValue> {

    // {{ IndexBase (property)
    private IndexBase indexBase;

    @Title(sequence = "1")
    @MemberOrder(sequence = "1")
    public IndexBase getIndexBase() {
        return indexBase;
    }

    public void setIndexBase(final IndexBase indexBase) {
        this.indexBase = indexBase;
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

    // {{ Value (property)
    private BigDecimal value;

    @MemberOrder(sequence = "4")
    @Column(scale = 4)
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    // }}

    @Override
    public int compareTo(IndexValue o) {
        return o.getStartDate().compareTo(this.getStartDate());
    }

}