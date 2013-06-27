package org.estatio.dom.agreement;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.joda.time.LocalDate;

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

@PersistenceCapable
@MemberGroups({"General", "Dates", "Related"})
// TODO: does this really need to implement WithInterval?  
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

    @Disabled
    @Title(sequence="1", append=":")
    @MemberOrder(sequence = "2")
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

    @Persistent
    private LocalDate startDate;

    @Override
    @MemberOrder(name="Dates", sequence = "4")
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // //////////////////////////////////////

    @Persistent
    private LocalDate endDate;

    @Override
    @MemberOrder(name="Dates", sequence = "5")
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
