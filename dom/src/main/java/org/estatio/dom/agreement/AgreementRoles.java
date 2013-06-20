package org.estatio.dom.agreement;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.party.Party;

@Hidden
public class AgreementRoles extends EstatioDomainService<AgreementRole> {

    public AgreementRoles() {
        super(AgreementRoles.class, AgreementRole.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    public AgreementRole newAgreementRole(final Agreement agreement, final Party party, final AgreementRoleType type, final @Named("Start Date") LocalDate startDate, final @Named("End Date") LocalDate endDate) {
        AgreementRole agreementRole = newTransientInstance();
        persistIfNotAlready(agreementRole);
        agreementRole.setStartDate(startDate);
        agreementRole.setEndDate(endDate);
        agreementRole.setType(type); // must do before associate with agreement, since part of AgreementRole#compareTo impl.
        agreementRole.modifyParty(party);
        agreementRole.modifyAgreement(agreement);
        return agreementRole;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    public AgreementRole findAgreementRole(Agreement agreement, Party party, AgreementRoleType type, LocalDate startDate) {
        return firstMatch("agreementRole_find", "agreement", agreement, "party", party, "type", type, "startDate", startDate);
    }

    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    public AgreementRole findAgreementRoleWithType(Agreement agreement, AgreementRoleType type, @Named("Date") LocalDate date) {
        return firstMatch("agreementRole_findWithType", "agreement", agreement, "type", type, "date", date);
    }

}
