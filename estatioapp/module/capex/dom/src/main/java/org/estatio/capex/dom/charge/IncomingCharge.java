package org.estatio.capex.dom.charge;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Ordering;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainObject2;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "capex",
        table = "IncomingCharge"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByNameContains", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.charge.IncomingCharge "
                        + "WHERE name.indexOf(:name) >= 0 "),
        @Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.charge.IncomingCharge "
                        + "WHERE name == :name ")
})
@Unique(name = "IncomingCharge_name_UNQ", members = { "name" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "capex.IncomingCharge"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class IncomingCharge extends UdoDomainObject2<IncomingCharge> {

    public IncomingCharge() {
        super("name");
    }

    public IncomingCharge(final String name, final IncomingCharge parent, final String atPath) {
        this();
        this.name = name;
        this.parent = parent;
        this.atPath = atPath;
    }

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private String name;

    @Persistent(mappedBy = "parent", dependentElement = "true")
    @Getter @Setter
    private SortedSet<IncomingCharge> children = new TreeSet<IncomingCharge>();

    @Column(allowsNull = "true")
    @Getter @Setter
    private IncomingCharge parent;


    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false"
    )
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String atPath;

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getAtPath());
    }

    //region > compareTo, toString

    @Override
    public String toString() {
        return getName();
    }

    private final Comparator<IncomingCharge> comparator =
            Ordering.natural()
                    .onResultOf(IncomingCharge::getName);

    @Override
    public int compareTo(final IncomingCharge other) {
        return comparator.compare(this, other);
    }

    //endregion

}
