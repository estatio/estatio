package org.estatio.dom.project;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioMutableObject;
import org.estatio.dom.asset.Property;
import org.estatio.dom.party.Party;

@PersistenceCapable(identityType = IdentityType.DATASTORE)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Queries({
        @Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.project.Project " +
                        "WHERE reference == :reference "),
        @Query(
                name = "findByReferenceOrName", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.project.Project " +
                        "WHERE reference.matches(matcher) || name.matches(matcher) "),
        @Query(
                name = "findByResponsible", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.project.Project " +
                        "WHERE responsible == :responsible "),
        @Query(
                name = "findByProperty", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.project.Project " +
                        "WHERE property == :property ")
})
public class Project extends EstatioMutableObject<Project> {

    public Project() {
        super("reference,startDate");
    }

    // //////////////////////////////////////

    private String reference;

    @Column(allowsNull = "false")
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String name;

    @Title
    @Column(allowsNull = "false")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @Column(allowsNull = "true")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // //////////////////////////////////////

    private LocalDate endDate;

    @Column(allowsNull = "true")
    @Persistent
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

    private Property property;

    @Column(allowsNull = "true")
    @Persistent
    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    // //////////////////////////////////////

    private Party responsible;

    @Column(allowsNull = "false")
    public Party getResponsible() {
        return responsible;
    }

    public void setResponsible(Party responsible) {
        this.responsible = responsible;
    }

    // //////////////////////////////////////

    public Project postponeOneWeek(@Named("Reason") String reason) {
        setStartDate(getStartDate().plusWeeks(1));
        return this;
    }

}
