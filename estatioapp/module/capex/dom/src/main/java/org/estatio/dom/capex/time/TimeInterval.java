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
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainObject2;

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
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.capex.time.TimeInterval "
                        + "WHERE name == :name "),
        @Query(
                name = "findByStartDateAndCalendarType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.capex.time.TimeInterval "
                        + "WHERE startDate    == :startDate "
                        + "   && calendarType == :calendarType ")
})
@Unique(name = "TimeInterval_name_UNQ", members = { "name" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "capex.TimeInterval"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class TimeInterval extends UdoDomainObject2<TimeInterval> {

    public TimeInterval(
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final CalendarType calendarType,
            final TimeInterval naturalParent,
            final TimeInterval financialParent) {
        super("name");
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.calendarType = calendarType;
        this.naturalParent = naturalParent;
        this.financialParent = financialParent;
    }

    @Column(allowsNull = "false")
    @Getter @Setter
    private String name;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate startDate;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate endDate;

    @Column(allowsNull = "false")
    @Getter @Setter
    private CalendarType calendarType;

    @Column(allowsNull = "true")
    @Getter @Setter
    private TimeInterval naturalParent;

    @Column(allowsNull = "true")
    @Getter @Setter
    private TimeInterval financialParent;


    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false"
    )
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String atPath;

    @Property(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getAtPath());
    }


    //region > compareTo, toString
    @Override
    public int compareTo(final TimeInterval other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "startDate desc, name");
    }
    //endregion

}
