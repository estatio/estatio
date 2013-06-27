package org.estatio.dom.agreement;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Where;

@PersistenceCapable
@Bounded
public class AgreementRoleCommunicationChannel extends EstatioTransactionalObject<AgreementRoleCommunicationChannel> implements WithInterval<AgreementRoleCommunicationChannel> {

    public AgreementRoleCommunicationChannel() {
        super("startDate desc, type, communicationChannel, role");
    }

    // //////////////////////////////////////

    @Column(name = "LEASEROLE_ID")
    private AgreementRole role;

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

    private AgremeentRoleCommunicationChannelType type;

    @MemberOrder(sequence = "2")
    public AgremeentRoleCommunicationChannelType getType() {
        return type;
    }

    public void setType(AgremeentRoleCommunicationChannelType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    @Column(name = "COMMUNICATIONCHANNEL_ID")
    private CommunicationChannel communicationChannel;

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
    @MemberOrder(sequence = "4")
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
    @MemberOrder(sequence = "5")
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

    @Override
    @Hidden(where = Where.ALL_TABLES)
    @Disabled
    @Optional
    public AgreementRoleCommunicationChannel getPrevious() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Hidden(where = Where.ALL_TABLES)
    @Disabled
    @Optional
    public AgreementRoleCommunicationChannel getNext() {
        // TODO Auto-generated method stub
        return null;
    }

    // //////////////////////////////////////

}
