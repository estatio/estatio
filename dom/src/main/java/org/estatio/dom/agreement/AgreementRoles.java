package org.estatio.dom.agreement;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.party.Party;

@Named("Agreement Roles")
@Hidden
public class AgreementRoles extends EstatioDomainService {

    public AgreementRoles() {
        super(AgreementRoles.class, AgreementRole.class);
    }

    // //////////////////////////////////////

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


    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    @NotContributed
    public AgreementRole findAgreementRole(Agreement agreement, Party party, AgreementRoleType type, LocalDate startDate) {
        return firstMatch(queryForFind(agreement, party, type, startDate));
    }

    private static QueryDefault<AgreementRole> queryForFind(Agreement agreement, Party party, AgreementRoleType type, LocalDate startDate) {
        return new QueryDefault<AgreementRole>(AgreementRole.class, "agreementRole_find", "agreement", agreement, "party", party, "type", type, "startDate", startDate);
    }

    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    @NotContributed
    public AgreementRole findAgreementRoleWithType(Agreement agreement, AgreementRoleType type, @Named("Date") LocalDate date) {
        return firstMatch(queryForFindWithType(agreement, type, date));
    }

    private static QueryDefault<AgreementRole> queryForFindWithType(Agreement agreement, AgreementRoleType type, LocalDate date) {
        return new QueryDefault<AgreementRole>(AgreementRole.class, "agreementRole_findWithType", "agreement", agreement, "type", type, "date", date);
    }


    // //////////////////////////////////////
    
    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<AgreementRole> allAgreementRoles() {
        return allInstances(AgreementRole.class);
    }
    

}
