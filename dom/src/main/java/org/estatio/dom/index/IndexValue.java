package org.estatio.dom.index;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.common.collect.Ordering;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.utils.Orderings;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

@PersistenceCapable
public class IndexValue extends EstatioRefDataObject implements Comparable<IndexValue> {

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

    private BigDecimal value;

    @MemberOrder(sequence = "4")
    @Column(scale = 4)
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

    @Override
    public int compareTo(IndexValue o) {
        return ORDERING_BY_START_DATE.compare(this, o);
    }

    public final static Ordering<IndexValue> ORDERING_BY_START_DATE = new Ordering<IndexValue>() {
        public int compare(IndexValue p, IndexValue q) {
            return Orderings.lOCAL_DATE_NATURAL_NULLS_FIRST.compare(p.getStartDate(), q.getStartDate());
        }
    };

}