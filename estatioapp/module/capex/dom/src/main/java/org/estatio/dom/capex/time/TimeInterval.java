package org.estatio.dom.capex.time;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "capex",
        table = "TimeInterval"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "find", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.capex.time.TimeInterval "),
        @Query(
                name = "findByNameContains", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.capex.time.TimeInterval "
                        + "WHERE name.indexOf(:name) >= 0 "),
        @Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.capex.time.TimeInterval "
                        + "WHERE name == :name ")
})
@Unique(name = "TimeInterval_name_UNQ", members = { "name" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "capex.TimeInterval"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class TimeInterval implements Comparable<TimeInterval> {

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private String name;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private LocalDate startDate;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private LocalDate endDate;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private boolean financial;

    //region > compareTo, toString
    @Override
    public int compareTo(final TimeInterval other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "name");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "name");
    }
    //endregion

}
