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

/**
 * TODO: REVIEW: EST-1862: I'm not sure if we need this entity, or simply to maintain the Estatio BankAccounts of the corresponding Estatio Organisations?
 */
@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "CodaBankAccount"
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
                        + "FROM org.estatio.module.coda.dom.supplier.CodaBankAccount "
                        + "WHERE supplier == :supplier "),
        @Query(
                name = "findBySupplierAndIban", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.supplier.CodaBankAccount "
                        + "WHERE supplier == :supplier "
                        + "   && iban     == :iban "),
        @Query(
                name = "findByIban", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.supplier.CodaBankAccount "
                        + "WHERE iban == :iban ")
})
@Unique(name = "CodaBankAccount_supplier_iban_UNQ", members = { "supplier", "iban" })
@DomainObject(
        objectType = "coda.CodaBankAccount",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CodaBankAccount implements Comparable<CodaBankAccount> {

    public CodaBankAccount() {}
    public CodaBankAccount(final CodaSupplier codaSupplier, final String iban, final String bic) {
        this.supplier = codaSupplier;
        this.iban = iban;
        this.bic = bic;
    }

    @Column(allowsNull = "false", name = "supplierId")
    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private CodaSupplier supplier;

    @Column(allowsNull = "false", length = 36)
    @Property()
    @Getter @Setter
    private String iban;

    @Column(allowsNull = "true", length = 36)
    @Property()
    @Getter @Setter
    private String bic;


    //region > compareTo, toString
    @Override
    public int compareTo(final CodaBankAccount other) {
        return ComparisonChain.start()
                .compare(getSupplier(), other.getSupplier())
                .compare(getIban(), other.getIban())
                .result();
    }

    @Override
    public String toString() {
        return "CodaBankAccount{" +
                "supplier=" + getSupplier() +
                ", iban='" + getIban() + '\'' +
                '}';
    }

    //endregion

}
