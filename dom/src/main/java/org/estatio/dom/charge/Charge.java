package org.estatio.dom.charge;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.WithCodeUnique;
import org.estatio.dom.WithDescriptionUnique;
import org.estatio.dom.WithReferenceUnique;
import org.estatio.dom.tax.Tax;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Query(
        name = "findByReference", language = "JDOQL",
        value = "SELECT " +
                "FROM org.estatio.dom.charge.Charge " +
                "WHERE reference.matches(:reference)")
@Bounded
@Immutable
public class Charge extends EstatioRefDataObject<Charge> implements WithReferenceUnique, WithCodeUnique, WithDescriptionUnique {

    public Charge() {
        super("code");
    }

    // //////////////////////////////////////

    private String reference;

    @Title(sequence = "1")
    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Unique(name = "CHARGE_CODE_UNIQUE_IDX")
    private String code;

    @MemberOrder(sequence = "2")
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "TAX_ID")
    private Tax tax;

    @MemberOrder(sequence = "3")
    public Tax getTax() {
        return tax;
    }

    public void setTax(final Tax tax) {
        this.tax = tax;
    }

    // //////////////////////////////////////

    private String description;

    @MemberOrder(sequence = "4")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "GROUP_ID")
    private ChargeGroup group;

    @MemberOrder(sequence = "5")
    public ChargeGroup getGroup() {
        return group;
    }

    public void setGroup(final ChargeGroup group) {
        this.group = group;
    }

}
