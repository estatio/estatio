package org.estatio.dom.agreement;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.party.Party;

@Hidden
public class AgreementRoles extends EstatioDomainService<AgreementRole> {

    public AgreementRoles() {
        super(AgreementRoles.class, AgreementRole.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
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
    @NotContributed
    public AgreementRole findByAgreementAndPartyAndTypeAndStartDate(Agreement agreement, Party party, AgreementRoleType type, LocalDate startDate) {
        return firstMatch("findByAgreementAndPartyAndTypeAndStartDate", "agreement", agreement, "party", party, "type", type, "startDate", startDate);
    }

    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    @NotContributed
    public AgreementRole findByAgreementAndTypeAndContainsDate(Agreement agreement, AgreementRoleType type, LocalDate date) {
        return firstMatch("findByAgreementAndTypeAndContainsDate", "agreement", agreement, "type", type, "date", date);
    }

}
