package org.estatio.dom.charge;

import javax.jdo.annotations.Extension;

import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.index.IndexValue;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.utils.Orderings;

@javax.jdo.annotations.PersistenceCapable
@Bounded
@Immutable
public class Charge extends EstatioRefDataObject implements Comparable<Charge> {

    // {{ Reference (property)
    private String reference;

    @Title(sequence="1")
    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    
    // {{ Code (property)
    private String code;

    @MemberOrder(sequence = "2")
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    // }}

    // {{ Tax (property)
    private Tax tax;

    @MemberOrder(sequence = "3")
    public Tax getTax() {
        return tax;
    }

    public void setTax(final Tax tax) {
        this.tax = tax;
    }

    // }}

    // {{ Description (property)
    private String description;

    @MemberOrder(sequence = "4")
    @Title(sequence = "2", prepend = "-")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // }}

    // {{ Group (property)
    private ChargeGroup group;

    @MemberOrder(sequence = "5")
    public ChargeGroup getGroup() {
        return group;
    }

    public void setGroup(final ChargeGroup group) {
        this.group = group;
    }
    // }}

    // {{ Comparable impl
    @Override
    public int compareTo(Charge other) {
        return ORDERING_BY_CODE.compare(this, other);
    }
    // }}

    public final static Ordering<Charge> ORDERING_BY_CODE = new Ordering<Charge>() {
        public int compare(Charge p, Charge q) {
            return Ordering.<String>natural().nullsFirst().compare(p.getCode(), q.getCode());
        }
    };

}
