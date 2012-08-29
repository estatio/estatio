package com.eurocommercialproperties.estatio.dom.lease;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.runtimes.dflt.objectstores.jdo.applib.annotations.Auditable;

@javax.jdo.annotations.PersistenceCapable(schema = "lease", identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@ObjectType("LEAS")
@Auditable
public class Lease extends AbstractDomainObject {
    

    // {{ Reference (property)
    private String reference;

    @MemberOrder(sequence = "1")
    @Title()
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    // {{ Name (property)
    private String name;

    @MemberOrder(sequence = "2")
    @Optional
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    // {{ StartDate (property)
    private Date startDate;

    @MemberOrder(sequence = "3")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    // }}

    // {{ EndDate (property)
    private Date endDate;

    @MemberOrder(sequence = "4")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(final Date endDate) {
        this.endDate = endDate;
    }

    // }}

    // {{ TerminationDate (property)
    private Date terminationDate;

    @MemberOrder(sequence = "5")
    @Optional
    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(final Date terminationDate) {
        this.terminationDate = terminationDate;
    }
    // }}
    
    //TODO Add LeaseActor: is there an easy way for this recurring actor pattern?

}
