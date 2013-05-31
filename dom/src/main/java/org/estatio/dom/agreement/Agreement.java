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
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.WithReference;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.ValueUtils;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@Bookmarkable
public abstract class Agreement extends EstatioTransactionalObject implements WithReference<Agreement>, WithInterval {

    // {{ Reference (property)
    private String reference;

    @MemberOrder(sequence = "1")
    @Title
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    // {{ Name (property)
    private String name;

    @MemberOrder(sequence = "2")
    @Optional
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    // {{ Derived attribute

    @MemberOrder(sequence = "3")
    public abstract Party getPrimaryParty();

    @MemberOrder(sequence = "4")
    public abstract Party getSecondaryParty();

    protected Party findParty(final String agreementRoleTypeTitle) {
        final AgreementRoleType art = agreementRoleTypes.find(agreementRoleTypeTitle);
        final Predicate<AgreementRole> currentAgreementRoleOfType = currentAgreementRoleOfType(art);
        final Iterable<Party> parties = Iterables.transform(
                Iterables.filter(
                        getRoles(), currentAgreementRoleOfType), partyOfAgreementRole());
        return ValueUtils.firstElseNull(parties);
    }

    protected Party findParty(AgreementRoleType agreementRoleType) {
        Iterable<Party> parties = Iterables.transform(
                Iterables.filter(
                        getRoles(), currentAgreementRoleOfType(agreementRoleType)), partyOfAgreementRole());
        return ValueUtils.firstElseNull(parties);
    }

    private static Function<AgreementRole, Party> partyOfAgreementRole() {
        return new Function<AgreementRole, Party>() {
            public Party apply(AgreementRole agreementRole) {
                return agreementRole.getParty();
            }
        };
    }

    private static Predicate<AgreementRole> currentAgreementRoleOfType(final AgreementRoleType lat) {
        return new Predicate<AgreementRole>() {
            public boolean apply(AgreementRole candidate) {
                return candidate.getType() == lat && candidate.isCurrent();
            }
        };
    }

    // }}

    // {{ StartDate, EndDate (WithInterval)
    private LocalDate startDate;

    @javax.jdo.annotations.Persistent
    @MemberOrder(sequence = "5")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    private LocalDate endDate;

    @javax.jdo.annotations.Persistent
    @MemberOrder(sequence = "6")
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
    // }}

    // {{ TerminationDate (property)
    private LocalDate terminationDate;

    @javax.jdo.annotations.Persistent
    @MemberOrder(sequence = "7")
    @Optional
    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(final LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    // }}

    // {{ Type (property)
    private AgreementType agreementType;

    @MemberOrder(sequence = "8")
    public AgreementType getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(final AgreementType type) {
        this.agreementType = type;
    }

    // }}

    // {{ PreviousAgreement (property)
    private Agreement previousAgreement;

    @Disabled
    @MemberOrder(sequence = "9")
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
        // associate new
        setPreviousAgreement(previousAgreement);
        // additional business logic
        onModifyPreviousAgreement(currentPreviousAgreement, previousAgreement);
    }

    public void clearPreviousAgreement() {
        Agreement currentPreviousAgreement = getPreviousAgreement();
        // check for no-op
        if (currentPreviousAgreement == null) {
            return;
        }
        // dissociate existing
        setPreviousAgreement(null);
        // additional business logic
        onClearPreviousAgreement(currentPreviousAgreement);
    }

    protected void onModifyPreviousAgreement(final Agreement oldPreviousAgreement, final Agreement newPreviousAgreement) {
        if (oldPreviousAgreement != null) {
            oldPreviousAgreement.setNextAgreement(null);
        }
        if (newPreviousAgreement != null) {
            newPreviousAgreement.setNextAgreement(this);
        }
    }

    protected void onClearPreviousAgreement(final Agreement oldPreviousAgreement) {
        oldPreviousAgreement.setNextAgreement(null);
    }

    // }}

    // {{ NextAgreement (property)
    private Agreement nextAgreement;

    @Disabled
    @MemberOrder(sequence = "10")
    public Agreement getNextAgreement() {
        return nextAgreement;
    }

    public void setNextAgreement(final Agreement nextAgreement) {
        this.nextAgreement = nextAgreement;
    }

    // }}

    // {{ Roles (Collection)
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
    }

    public void removeFromRoles(final AgreementRole agreementRole) {
        // check for no-op
        if (agreementRole == null || !getRoles().contains(agreementRole)) {
            return;
        }
        // dissociate existing
        getRoles().remove(agreementRole);
    }

    @MemberOrder(name = "Roles", sequence = "11")
    public AgreementRole addRole(@Named("party") Party party, @Named("agreementType") AgreementRoleType type, @Named("startDate") @Optional LocalDate startDate, @Named("endDate") @Optional LocalDate endDate) {
        AgreementRole agreementRole = findRole(party, type, startDate);
        if (agreementRole == null) {
            agreementRole = agreementRoles.newAgreementRole(this, party, type, startDate, endDate);
        }
        agreementRole.setEndDate(endDate);
        return agreementRole;
    }

    // }}

    @Hidden
    public AgreementRole findRole(Party party, AgreementRoleType type, LocalDate startDate) {
        return agreementRoles.findAgreementRole(this, party, type, startDate);
    }

    @Hidden
    public AgreementRole findRoleWithType(AgreementRoleType agreementRoleType, LocalDate date) {
        return agreementRoles.findAgreementRoleWithType(this, agreementRoleType, date);
    }


    // {{ Comparable impl
    @Override
    public int compareTo(Agreement other) {
        return ORDERING_BY_REFERENCE.compare(this, other);
    }
    // }}

    // {{ injected
    private AgreementRoles agreementRoles;
    public void injectAgreementRoles(final AgreementRoles agreementRoles) {
        this.agreementRoles = agreementRoles;
    }
    private AgreementRoleTypes agreementRoleTypes;
    public void injectAgreementRoleTypes(final AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }

    // }}
}
