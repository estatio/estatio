package org.estatio.module.coda.dom.doc;

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

import org.estatio.module.capex.dom.invoice.IncomingInvoice;

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
                name = "findByCompanyCodeAndDocCodeAndDocNum", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.inbound.dom.impl.CodaDocument "
                        + "WHERE companyCode == :companyCode && "
                        + "docCode ==:docCode && "
                        + "docNum ==:docNum "),
        @Query(
                name = "findHighWaterMark",
                value = "SELECT "
                        + "max(modifyDate) "
                        + "FROM org.estatio.module.coda.inbound.dom.impl.CodaDocument"
        )
})
@Unique(name = "CodaDocument_companyCode_docCode_docNum_UNQ", members = { "companyCode", "docCode", "docNum" })
@Indices({
        @Index(
                name = "CodaDocument_modify_IDX",
                members = { "modifyDate" }
        )
})
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CodaDocument implements Comparable<CodaDocument> {

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private String companyCode;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private String docCode;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private String docNum;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private TimeStamp modifyDate;

    // TODO: are extrefs allowed to be null?

    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private String extRef3;

    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private String extRef4;

    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private String extRef5;

    @Column(allowsNull = "true")
    @Property
    @Getter @Setter
    private IncomingInvoice incomingInvoice;

    //region > compareTo, toString
    @Override
    public int compareTo(final CodaDocument other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "companyCode");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "companyCode");
    }
    //endregion

}
