package org.estatio.dom.agreement;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.ComparableByReference;
import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.ValueUtils;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
            name = "agreement_findAgreementByReference", language = "JDOQL", 
            value = "SELECT FROM org.estatio.dom.agreement.Agreement WHERE reference.matches(:r)"),
        @javax.jdo.annotations.Query(
            name = "findByAgreementTypeAndRoleTypeAndParty", language = "JDOQL", 
            value = "SELECT " + 
                    "FROM org.estatio.dom.agreement.Agreement " + 
                    "WHERE agreementType == :agreementType" +
                    " && roles.contains(role)" +
                    " && role.type == :roleType" +
                    " && role.party == :party" +
                    " VARIABLES org.estatio.dom.agreement.AgreementRole role")
})
@Bookmarkable
@MemberGroups({"General", "Dates", "Related"})
public abstract class Agreement extends EstatioTransactionalObject<Agreement> implements ComparableByReference<Agreement>, WithInterval {

    public Agreement() {
        super("reference");
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Unique(name = "AGREEMENT_REFERENCE_IDX")
    private String reference;

    @MemberOrder(sequence = "1")
    @Title
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String name;

    @MemberOrder(sequence = "2")
    @Optional
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "3")
    public abstract Party getPrimaryParty();

    @MemberOrder(sequence = "4")
    public abstract Party getSecondaryParty();

    // //////////////////////////////////////

    protected Party findParty(final String agreementRoleTypeTitle) {
        final AgreementRoleType art = agreementRoleTypes.find(agreementRoleTypeTitle);
        return findParty(art);
    }

    protected Party findParty(AgreementRoleType agreementRoleType) {
        final Predicate<AgreementRole> currentAgreementRoleOfType = currentAgreementRoleOfType(agreementRoleType);
        final Iterable<Party> parties = Iterables.transform(Iterables.filter(getRoles(), currentAgreementRoleOfType), partyOfAgreementRole());
        return ValueUtils.firstElseNull(parties);
    }

    private static Function<AgreementRole, Party> partyOfAgreementRole() {
        return new Function<AgreementRole, Party>() {
            public Party apply(AgreementRole agreementRole) {
                return agreementRole != null? agreementRole.getParty(): null;
            }
        };
    }

    private static Predicate<AgreementRole> currentAgreementRoleOfType(final AgreementRoleType art) {
        return new Predicate<AgreementRole>() {
            public boolean apply(AgreementRole candidate) {
                return candidate != null? candidate.getType() == art && candidate.isCurrent(): false;
            }
        };
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @javax.jdo.annotations.Persistent
    @MemberOrder(name="Dates", sequence = "5")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    private LocalDate endDate;

    @javax.jdo.annotations.Persistent
    @MemberOrder(name="Dates", sequence = "6")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    // //////////////////////////////////////

    private LocalDate terminationDate;

    @javax.jdo.annotations.Persistent
    @MemberOrder(name="Dates", sequence = "7")
    @Optional
    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(final LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    @Programmatic
    public LocalDateInterval getEffectiveInterval() {
        return LocalDateInterval.including(getStartDate(), getTerminationDate());
    }

    // //////////////////////////////////////

    private AgreementType agreementType;

    @MemberOrder(sequence = "8")
    public AgreementType getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(final AgreementType type) {
        this.agreementType = type;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "nextAgreement")
    private Agreement previousAgreement;

    @Disabled
    @Optional
    @MemberOrder(name="Related", sequence = "9")
    public Agreement getPreviousAgreement() {
        return previousAgreement;
    }

    public void setPreviousAgreement(final Agreement previousAgreement) {
        this.previousAgreement = previousAgreement;
    }

    public void modifyPreviousAgreement(final Agreement previousAgreement) {
        Agreement currentPreviousAgreement = getPreviousAgreement();
        // check for no-op
        if (previousAgreement == null || previousAgreement.equals(currentPreviousAgreement)) {
            return;
        }
        // dissociate existing
        clearPreviousAgreement();
        // associate new
        previousAgreement.setNextAgreement(this);
        setPreviousAgreement(previousAgreement);
    }

    public void clearPreviousAgreement() {
        Agreement currentPreviousAgreement = getPreviousAgreement();
        // check for no-op
        if (currentPreviousAgreement == null) {
            return;
        }
        // dissociate existing
        currentPreviousAgreement.setNextAgreement(null);
        setPreviousAgreement(null);
    }

    // //////////////////////////////////////

    private Agreement nextAgreement;

    @Disabled
    @Optional
    @MemberOrder(name="Related", sequence = "10")
    public Agreement getNextAgreement() {
        return nextAgreement;
    }

    public void setNextAgreement(final Agreement nextAgreement) {
        this.nextAgreement = nextAgreement;
    }

    public void modifyNextAgreement(final Agreement nextAgreement) {
        Agreement currentNextAgreement = getNextAgreement();
        // check for no-op
        if (nextAgreement == null || nextAgreement.equals(currentNextAgreement)) {
            return;
        }
        // delegate to parent(s) to (re-)associate
        if (currentNextAgreement != null) {
            currentNextAgreement.clearPreviousAgreement();
        }
        nextAgreement.modifyPreviousAgreement(this);
    }

    public void clearNextAgreement() {
        Agreement currentNextAgreement = getNextAgreement();
        // check for no-op
        if (currentNextAgreement == null) {
            return;
        }
        // delegate to parent to dissociate
        currentNextAgreement.clearPreviousAgreement();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "agreement")
    private SortedSet<AgreementRole> roles = new TreeSet<AgreementRole>();

    @MemberOrder(name = "Roles", sequence = "11")
    @Render(Type.EAGERLY)
    public SortedSet<AgreementRole> getRoles() {
        return roles;
    }

    public void setRoles(final SortedSet<AgreementRole> actors) {
        this.roles = actors;
    }

    public void addToRoles(final AgreementRole agreementRole) {
        // check for no-op
        if (agreementRole == null || getRoles().contains(agreementRole)) {
            return;
        }
        // associate new
        getRoles().add(agreementRole);
        agreementRole.setAgreement(this);
    }

    public void removeFromRoles(final AgreementRole agreementRole) {
        // check for no-op
        if (agreementRole == null || !getRoles().contains(agreementRole)) {
            return;
        }
        // dissociate existing
        getRoles().remove(agreementRole);
        agreementRole.setAgreement(null);
    }

    /**
     * TODO: need logic ensure that there cannot be two {@link AgreementRole}s 
     * of the same type at the same point in time.
     */
    @MemberOrder(name = "Roles", sequence = "11")
    public AgreementRole addRole(@Named("party") Party party, @Named("agreementType") AgreementRoleType type, @Named("startDate") @Optional LocalDate startDate, @Named("endDate") @Optional LocalDate endDate) {
        AgreementRole agreementRole = findRole(party, type, startDate);
        if (agreementRole == null) {
            agreementRole = agreementRoles.newAgreementRole(this, party, type, startDate, endDate);
        }
        agreementRole.setEndDate(endDate);
        return agreementRole;
    }

    // //////////////////////////////////////

    @Programmatic
    AgreementRole findRole(Party party, AgreementRoleType type, LocalDate startDate) {
        return agreementRoles.findAgreementRole(this, party, type, startDate);
    }

    @Programmatic
    public AgreementRole findRoleWithType(AgreementRoleType agreementRoleType, LocalDate date) {
        return agreementRoles.findAgreementRoleWithType(this, agreementRoleType, date);
    }

    // //////////////////////////////////////

    protected Agreements agreements;
    public void injectAgreements(Agreements agreements) {
        this.agreements = agreements;
    }
    protected AgreementRoles agreementRoles;
    public void injectAgreementRoles(final AgreementRoles agreementRoles) {
        this.agreementRoles = agreementRoles;
    }

    protected AgreementRoleTypes agreementRoleTypes;
    public void injectAgreementRoleTypes(final AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }

    protected AgreementTypes agreementTypes;
    public void injectAgreementTypes(AgreementTypes agreementTypes) {
        this.agreementTypes = agreementTypes;
    }

}
