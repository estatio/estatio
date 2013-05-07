package org.estatio.jdo;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.query.QueryDefault;

public class AgreementRolesJdo extends AgreementRoles {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    @NotContributed
    public AgreementRole findAgreementRole(Agreement agreement, Party party, AgreementRoleType type, LocalDate startDate) {
        return firstMatch(queryForFind(agreement, party, type, startDate));
    }

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    @NotContributed
    public AgreementRole findAgreementRoleWithType(Agreement agreement, AgreementRoleType type, @Named("Date") LocalDate date) {
        return firstMatch(queryForFindWithType(agreement, type, date));
    }

    private static QueryDefault<AgreementRole> queryForFind(Agreement agreement, Party party, AgreementRoleType type, LocalDate startDate) {
        return new QueryDefault<AgreementRole>(AgreementRole.class, "agreementRole_find", "agreement", agreement, "party", party, "type", type, "startDate", startDate);
    }

    private static QueryDefault<AgreementRole> queryForFindWithType(Agreement agreement, AgreementRoleType type, LocalDate date) {
        return new QueryDefault<AgreementRole>(AgreementRole.class, "agreementRole_findWithType", "agreement", agreement, "type", type, "date", date);
    }

}
