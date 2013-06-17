package org.estatio.dom.agreement;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.party.Party;

@Named("Agreement Roles")
@Hidden
public class AgreementRoles extends EstatioDomainService {

    protected AgreementRoles() {
        super(AgreementRoles.class, AgreementRole.class);
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
    public AgreementRole newAgreementRole(final Agreement agreement, final Party party, final AgreementRoleType type, final @Named("Start Date") LocalDate startDate, final @Named("End Date") LocalDate endDate) {
        AgreementRole agreementRole = newTransientInstance(AgreementRole.class);
        persistIfNotAlready(agreementRole);
        agreementRole.setStartDate(startDate);
        agreementRole.setEndDate(endDate);
        agreementRole.setType(type); // must do before associate with agreement, since part of AgreementRole#compareTo impl.
        agreementRole.modifyParty(party);
        agreementRole.modifyAgreement(agreement);
        return agreementRole;
    }

    @ActionSemantics(Of.SAFE)
    @NotContributed
    public AgreementRole findAgreementRole(final Agreement agreement, final Party party, final AgreementRoleType type, final @Named("Start Date") LocalDate startDate) {
        throw new NotImplementedException();
    }

    @ActionSemantics(Of.SAFE)
    @NotContributed
    public AgreementRole findAgreementRoleWithType(final Agreement agreement, final AgreementRoleType type, final @Named("Date") LocalDate date) {
        throw new NotImplementedException();
    }

    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<AgreementRole> allAgreementRoles() {
        return allInstances(AgreementRole.class);
    }

}
