package org.estatio.module.coda.dom.supplier;

import org.apache.isis.applib.annotation.*;

import javax.jdo.annotations.*;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "impl",
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
                name = "find", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.inbound.dom.impl.CodaBankAccount "),
        @Query(
                name = "findByIbanContains", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.inbound.dom.impl.CodaBankAccount "
                        + "WHERE iban.indexOf(:iban) >= 0 "),
        @Query(
                name = "findByIban", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.inbound.dom.impl.CodaBankAccount "
                        + "WHERE iban == :iban ")
})
@Unique(name = "CodaBankAccount_iban_UNQ", members = { "iban" })
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CodaBankAccount implements Comparable<CodaBankAccount> {

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private String iban;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private String bic;

    //region > compareTo, toString
    @Override
    public int compareTo(final CodaBankAccount other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "iban");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "iban");
    }
    //endregion

}
