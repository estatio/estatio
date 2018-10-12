package org.estatio.module.coda.dom.supplier;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.value.TimeStamp;

import org.estatio.module.party.dom.Organisation;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "coda"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.inbound.dom.impl.CodaSupplier "
                        + "WHERE reference == :reference "),
        @Query(
                name = "findHighWaterMark",
                value = "SELECT "
                        + "max(modifyDate) "
                        + "FROM org.estatio.module.coda.inbound.dom.impl.CodaSupplier"
        )
})
@Unique(name = "CodaSupplier_reference_UNQ", members = { "reference" })
@Indices({
        @Index(
                name = "CodaSupplier_modify_IDX",
                members = { "modifyDate" }
        )
})
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CodaSupplier implements Comparable<CodaSupplier> {

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private String reference;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private String shortName;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private TimeStamp modifyDate;

    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private Organisation organisation;

    //region > compareTo, toString
    @Override
    public int compareTo(final CodaSupplier other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "reference");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "reference");
    }
    //endregion

}
