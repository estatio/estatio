package org.estatio.module.coda.dom.supplier;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
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
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;

import org.estatio.module.party.dom.Organisation;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "coda",
        table = "CodaSupplier"
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
                        + "FROM org.estatio.module.coda.dom.supplier.CodaSupplier "
                        + "WHERE reference == :reference ")
})
@Unique(name = "CodaSupplier_reference_UNQ", members = { "reference" })
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CodaSupplier implements Comparable<CodaSupplier> {

    public CodaSupplier(){}
    public CodaSupplier(final String reference, final String shortName, final Organisation organisation) {
        this.reference = reference;
        this.shortName = shortName;
        this.organisation = organisation;
    }

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private String reference;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private String shortName;

    // TODO: REVIEW: EST-1862: this is optional, but do we make this mandatory and automatically create Estatio orgs if missing?
    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private Organisation organisation;

    /**
     * TODO: EST-1862: do we maintain this list of Coda BankAccounts, or do we simply maintain the Estatio BankAccounts of the corresponding Estatio Organisations?
     */
    @javax.jdo.annotations.Persistent(mappedBy = "supplier", defaultFetchGroup = "true")
    @CollectionLayout(defaultView = "table", paged = 999)
    @Getter @Setter
    private SortedSet<CodaBankAccount> bankAccounts = new TreeSet<>();

    @Programmatic
    public CodaBankAccount upsertBankAccount(final String iban, final String bic) {
        return codaBankAccountRepository.upsert(this, iban, bic);
    }

    //region > compareTo, toString
    @Override
    public int compareTo(final CodaSupplier other) {
        return ComparisonChain.start()
                .compare(getReference(), other.getReference())
                .result();
    }

    @Override public String toString() {
        return "CodaSupplier {" +
                "reference='" + getReference()
                + '\'' +
                '}';
    }

    //endregion

    @Inject
    CodaBankAccountRepository codaBankAccountRepository;
}
