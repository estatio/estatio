package org.estatio.dom.index;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.estatio.dom.EstatioRefDataObject;
import org.joda.time.LocalDate;

@PersistenceCapable
public class IndexValue extends EstatioRefDataObject implements Comparable<IndexValue> {

    // {{ StartDate (property)
    private LocalDate startDate;
    
    @Persistent
    @MemberOrder(sequence = "1")
    @Title(sequence = "2", prepend = ":")
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }
    
    // }}

    // {{ IndexBase (property)
    private IndexBase indexBase;

    @Hidden(where=Where.PARENTED_TABLES)
    @Title(sequence = "2")
    @MemberOrder(sequence = "2")
    public IndexBase getIndexBase() {
        return indexBase;
    }

    public void setIndexBase(final IndexBase indexBase) {
        this.indexBase = indexBase;
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

    @Prototype
    public void remove() {
        getContainer().remove(this);
    }

    @Override
    public int compareTo(IndexValue o) {
        return o.getStartDate().compareTo(this.getStartDate());
    }

}