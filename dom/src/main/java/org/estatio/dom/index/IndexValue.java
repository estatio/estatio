package org.estatio.dom.index;

import java.math.BigDecimal;

import javax.jdo.annotations.Extension;

import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.utils.Orderings;

@javax.jdo.annotations.PersistenceCapable/*(extensions={
        @Extension(vendorName="datanucleus", key="multitenancy-column-name", value="iid"),
        @Extension(vendorName="datanucleus", key="multitenancy-column-length", value="4"),
    })*/
public class IndexValue extends EstatioRefDataObject implements Comparable<IndexValue> {

    
    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @MemberOrder(sequence = "1")
    @Title(sequence = "2", prepend = ":")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    
    private IndexBase indexBase;

    @Hidden(where = Where.PARENTED_TABLES)
    @Title(sequence = "2")
    @MemberOrder(sequence = "2")
    public IndexBase getIndexBase() {
        return indexBase;
    }

    public void setIndexBase(final IndexBase indexBase) {
        this.indexBase = indexBase;
    }

    public void modifyIndexBase(final IndexBase indexBase) {
        IndexBase currentIndexBase = getIndexBase();
        if (indexBase == null || indexBase.equals(currentIndexBase)) {
            return;
        }
        indexBase.addToValues(this);
    }

    public void clearIndexBase() {
        IndexBase currentIndexBase = getIndexBase();
        if (currentIndexBase == null) {
            return;
        }
        currentIndexBase.removeFromValues(this);
    }

    
    
    @javax.jdo.annotations.Column(scale = 4)
    private BigDecimal value;

    @MemberOrder(sequence = "4")
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    @Prototype
    public void remove() {
        getContainer().remove(this);
    }

    
    // {{ Comparable impl
    @Override
    public int compareTo(IndexValue o) {
        return ORDERING_BY_START_DATE.compare(this, o);
    }
    // }}

    public final static Ordering<IndexValue> ORDERING_BY_START_DATE = new Ordering<IndexValue>() {
        public int compare(IndexValue p, IndexValue q) {
            return Orderings.LOCAL_DATE_NATURAL_NULLS_FIRST.compare(p.getStartDate(), q.getStartDate());
        }
    };

}