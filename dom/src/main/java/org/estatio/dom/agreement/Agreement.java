package org.estatio.dom.agreement;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class Agreement extends EstatioTransactionalObject implements Comparable<Agreement> {

    // {{ Reference (property)
    private String reference;

    @MemberOrder(sequence = "1")
    @Title()
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
    public Party getPrimaryParty() {
        Iterable<Party> parties = Iterables.transform(Iterables.filter(getRoles(), currentAgreementRoleOfType(AgreementRoleType.LANDLORD)), partyOfAgreementRole());
        return firstElseNull(parties);
    }

    @MemberOrder(sequence = "4")
    public Party getSecondaryParty() {
        Iterable<Party> parties = Iterables.transform(Iterables.filter(getRoles(), currentAgreementRoleOfType(AgreementRoleType.TENANT)), partyOfAgreementRole());
        return firstElseNull(parties);
    }

    private Party firstElseNull(Iterable<Party> parties) {
        Iterator<Party> iterator = parties.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    private Function<AgreementRole, Party> partyOfAgreementRole() {
        return new Function<AgreementRole, Party>() {
            public Party apply(AgreementRole agreementRole) {
                return agreementRole.getParty();
            }
        };
    }

    protected static Predicate<AgreementRole> currentAgreementRoleOfType(final AgreementRoleType lat) {
        return new Predicate<AgreementRole>() {
            public boolean apply(AgreementRole candidate) {
                return candidate.getType() == lat && candidate.isCurrent();
            }
        };
    }

    // }}

    // {{ StartDate (property)
    private LocalDate startDate;

    @Persistent
    @MemberOrder(sequence = "5")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    // }}

    // {{ EndDate (property)
    private LocalDate endDate;

    @Persistent
    @MemberOrder(sequence = "6")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // }}

    // {{ TerminationDate (property)
    private LocalDate terminationDate;

    @Persistent
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
    @Persistent(mappedBy = "agreement")
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
            agreementRole = agreementRolesService.newAgreementRole(this, party, type, startDate, endDate);
        }
        agreementRole.setEndDate(endDate);
        return agreementRole;
    }

    // }}

    @Hidden
    public AgreementRole findRole(Party party, AgreementRoleType type, LocalDate startDate) {
        return agreementRolesService.findAgreementRole(this, party, type, startDate);
    }

    @Hidden
    public AgreementRole findRoleWithType(AgreementRoleType agreementRoleType, LocalDate date) {
        return agreementRolesService.findAgreementRoleWithType(this, agreementRoleType, date);
    }

    @Override
    public int compareTo(Agreement other) {
        return this.getReference().compareTo(other.getReference());
    }

    private AgreementRoles agreementRolesService;

    public void setAgreementRoles(final AgreementRoles agreementRoles) {
        this.agreementRolesService = agreementRoles;
    }

    // }}
}
