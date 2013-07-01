package org.estatio.dom.agreement;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name = "findByRoleAndTypeAndStartDate", language = "JDOQL", 
            value = "SELECT " +
                    "FROM org.estatio.dom.agreement.AgreementRoleCommunicationChannel " +
                    "WHERE role == :agreementRole " +
                    "&& type == :type " +
                    "&& startDate == :startDate"),
    @javax.jdo.annotations.Query(
            name = "findByRoleAndTypeAndEndDate", language = "JDOQL", 
            value = "SELECT " +
                    "FROM org.estatio.dom.agreement.AgreementRoleCommunicationChannel " +
                    "WHERE role == :agreementRole " +
                    "&& type == :type " +
                    "&& endDate == :endDate"),
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
@MemberGroups({"General", "Dates", "Related"})
public class AgreementRoleCommunicationChannel extends EstatioTransactionalObject<AgreementRoleCommunicationChannel> implements WithInterval<AgreementRoleCommunicationChannel> {

    public AgreementRoleCommunicationChannel() {
        super("startDate desc, type, communicationChannel, role");
    }

    // //////////////////////////////////////

    @Column(name = "LEASEROLE_ID")
    private AgreementRole role;

    @Title(sequence="2")
    @MemberOrder(sequence = "1")
    @Hidden(where = Where.REFERENCES_PARENT)
    @Disabled
    public AgreementRole getRole() {
        return role;
    }

    public void setRole(AgreementRole agreementRole) {
        this.role = agreementRole;
    }

    public void modifyRole(final AgreementRole role) {
        AgreementRole currentRole = getRole();
        if (role == null || role.equals(currentRole)) {
            return;
        }
        setRole(role);
    }

    public void clearRole() {
        AgreementRole currentRole = getRole();
        if (currentRole == null) {
            return;
        }
        setRole(null);
    }

    // //////////////////////////////////////

    private AgreementRoleCommunicationChannelType type;

    @MemberOrder(sequence = "2")
    @Title(sequence="1", append=":")
    @Disabled
    public AgreementRoleCommunicationChannelType getType() {
        return type;
    }

    public void setType(AgreementRoleCommunicationChannelType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    @Column(name = "COMMUNICATIONCHANNEL_ID")
    private CommunicationChannel communicationChannel;

    @Title(sequence="3", prepend=",")
    @MemberOrder(sequence = "3")
    public CommunicationChannel getCommunicationChannel() {
        return communicationChannel;
    }

    public void setCommunicationChannel(CommunicationChannel communicationChannel) {
        this.communicationChannel = communicationChannel;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @MemberOrder(name="Dates", sequence = "4")
    @Optional
    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @MemberOrder(name="Dates", sequence = "5")
    @Optional
    @Disabled
    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(LocalDate localDate) {
        this.endDate = localDate;
    }

    
    // //////////////////////////////////////

    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    // //////////////////////////////////////

    @Hidden // TODO (where = Where.ALL_TABLES)
    @MemberOrder(name="Related", sequence="1")
    @Named("Previous Channel")
    @Disabled
    @Optional
    @Override
    public AgreementRoleCommunicationChannel getPrevious() {
        return null;
    }

    @Hidden // TODO (where = Where.ALL_TABLES)
    @MemberOrder(name="Related", sequence="2")
    @Named("Next Channel")
    @Disabled
    @Optional
    @Override
    public AgreementRoleCommunicationChannel getNext() {
        return null;
    }


}
