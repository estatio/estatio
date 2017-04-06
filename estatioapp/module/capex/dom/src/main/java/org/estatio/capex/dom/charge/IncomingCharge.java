package org.estatio.capex.dom.charge;

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

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;

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
public class IncomingCharge implements Comparable<IncomingCharge> {

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private String name;

    @Persistent(mappedBy = "parent", dependentElement = "true")
    @Collection()
    @Getter @Setter
    private SortedSet<IncomingCharge> children = new TreeSet<IncomingCharge>();

    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private IncomingCharge parent;


    //region > compareTo, toString
    @Override
    public int compareTo(final IncomingCharge other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "name");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "name");
    }
    //endregion

}
