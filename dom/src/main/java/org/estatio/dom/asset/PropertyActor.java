package org.estatio.dom.asset;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.VersionStrategy;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;


@PersistenceCapable
@javax.jdo.annotations.Query(name = "propact_find", 
    language = "JDOQL", 
    value = "SELECT FROM org.estatio.dom.asset.Property WHERE reference.matches(:r)")
@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
public class PropertyActor extends EstatioTransactionalObject implements Comparable<PropertyActor> {

    // {{ Property (property)
    private Property property;

    @Title(sequence="1")
    @MemberOrder(sequence = "1")
    @Hidden(where=Where.REFERENCES_PARENT)
    @Disabled
    public Property getProperty() {
        return property;
    }

    public void setProperty(final Property property) {
        this.property = property;
    }

    // }}

    // {{ Party (property)
    private Party party;

    @Title(sequence="2", prepend=", ")
    @MemberOrder(sequence = "2")
    @Disabled
    @Hidden(where=Where.REFERENCES_PARENT)
    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }

    // }}

    // {{ Type (property)
    private PropertyActorType type;

    @Disabled
    @MemberOrder(sequence = "3")
    public PropertyActorType getType() {
        return type;
    }

    public void setType(final PropertyActorType type) {
        this.type = type;
    }

    // }}

    // {{ StartDate (property)
    private LocalDate startDate;

    @MemberOrder(sequence = "4")
    @Optional
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    // }}

    // {{ EndDate (property)
    private LocalDate endDate;

    @MemberOrder(sequence = "5")
    @Optional
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }
    // }}

    @Hidden
    @Override
    public int compareTo(PropertyActor o) {
        return this.getType().compareTo(o.getType());
    }

}
