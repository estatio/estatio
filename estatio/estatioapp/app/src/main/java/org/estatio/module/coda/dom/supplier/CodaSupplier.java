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
        schema = "dbo",
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
        objectType = "coda.CodaSupplier",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CodaSupplier implements Comparable<CodaSupplier> {

    public CodaSupplier(){}
    public CodaSupplier(
            final String reference,
            final String name,
            final String shortName,
            final Organisation organisation) {
        this.reference = reference;
        this.name = name;
        this.shortName = shortName;
        this.organisation = organisation;
    }

    @Column(allowsNull = "false", length = 72)
    @Property()
    @Getter @Setter
    private String reference;

    @Column(allowsNull = "false", length = 36)
    @Property()
    @Getter @Setter
    private String name;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private String shortName;

    // TODO: REVIEW: EST-1862: this is optional, but do we make this mandatory and automatically create Estatio orgs if missing?
    @Column(allowsNull = "true", name="organisationId")
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

    @javax.jdo.annotations.Persistent(mappedBy = "supplier", defaultFetchGroup = "false")
    @CollectionLayout(defaultView = "table", paged = 999)
    @Getter @Setter
    private SortedSet<CodaAddress> addresses = new TreeSet<>();

    @Programmatic
    public CodaAddress upsertAddress(
            final short tag,
            final String name,
            boolean defaultAddress,
            String address1,
            String address2,
            String address3,
            String address4,
            String address5,
            String address6,
            String postCode,
            String tel,
            String fax,
            String country,
            String language,
            String category,
            String eMail) {
        return codaAddressRepository.upsert(this, tag, name, defaultAddress, address1, address2, address3, address4, address5, address6, postCode, tel, fax, country, language, category, eMail);
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
    @Inject
    CodaAddressRepository codaAddressRepository;
}
