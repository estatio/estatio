package org.estatio.module.coda.dom.supplier;

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

import com.google.common.collect.ComparisonChain;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "CodaAddress"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findBySupplier", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.supplier.CodaAddress "
                        + "WHERE supplier == :supplier "),
        @Query(
                name = "findBySupplierAndTag", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.supplier.CodaAddress "
                        + "WHERE supplier == :supplier "
                        + "   && tag      == :tag ")
})
@Unique(name = "CodaAddress_supplier_tag_UNQ", members = { "supplier", "tag" })
@DomainObject(
        objectType = "coda.CodaAddress",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CodaAddress implements Comparable<CodaAddress> {

    public CodaAddress() {}
    public CodaAddress(final CodaSupplier codaSupplier, final short tag, final String name) {
        this.supplier = codaSupplier;
        this.tag = tag;
        this.name = name;
    }

    @Column(allowsNull = "false", name = "supplierId")
    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private CodaSupplier supplier;

    @Getter @Setter
    @Property()
    private short tag;

    @Column(allowsNull = "false", length = 80)
    @Property()
    @Getter @Setter
    private String name;

    @Property()
    @Getter @Setter
    private boolean defaultAddress;

    @Column(allowsNull = "true", length = 35)
    @Property()
    @Getter @Setter
    private String address1;

    @Column(allowsNull = "true", length = 35)
    @Property()
    @Getter @Setter
    private String address2;

    @Column(allowsNull = "true", length = 35)
    @Property()
    @Getter @Setter
    private String address3;

    @Column(allowsNull = "true", length = 35)
    @Property()
    @Getter @Setter
    private String address4;

    @Column(allowsNull = "true", length = 35)
    @Property()
    @Getter @Setter
    private String address5;

    @Column(allowsNull = "true", length = 35)
    @Property()
    @Getter @Setter
    private String address6;

    @Column(allowsNull = "true", length = 12)
    @Property()
    @Getter @Setter
    private String postCode;

    @Column(allowsNull = "true", length = 20)
    @Property()
    @Getter @Setter
    private String tel;

    @Column(allowsNull = "true", length = 20)
    @Property()
    @Getter @Setter
    private String fax;

    @Column(allowsNull = "true", length = 255)
    @Property()
    @Getter @Setter
    private String country;

    @Column(allowsNull = "true", length = 20)
    @Property()
    @Getter @Setter
    private String language;

    @Column(allowsNull = "true", length = 12)
    @Property()
    @Getter @Setter
    private String category;

    @Column(allowsNull = "true", length = 128)
    @Property()
    @Getter @Setter
    private String eMail;


    //region > compareTo, toString
    @Override
    public int compareTo(final CodaAddress other) {
        return ComparisonChain.start()
                .compare(getSupplier(), other.getSupplier())
                .compare(getTag(), other.getTag())
                .result();
    }

    @Override
    public String toString() {
        return "CodaAddress{" +
                "supplier=" + getSupplier() +
                ", tag=" + getTag() +
                '}';
    }

    //endregion

}
