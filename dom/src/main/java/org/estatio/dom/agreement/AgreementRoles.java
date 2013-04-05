package org.estatio.dom.agreement;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

@Named("Agreement Roles")
public class AgreementRoles extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "agreementRoles";
    }

    public String iconName() {
        return "AgreementRole";
    }
    // }}

    // {{ newAgreementRole
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    @NotContributed
    public AgreementRole newAgreementRole(
            final Agreement agreement, 
            final Party party, 
            final AgreementRoleType type, 
            final @Named("Start Date") LocalDate startDate, 
            final @Named("End Date") LocalDate endDate) {
        final AgreementRole agreementRole = newTransientInstance(AgreementRole.class);
        agreementRole.setParty(party);
        agreementRole.setAgreement(agreement);
        agreementRole.setStartDate(startDate);
        agreementRole.setEndDate(endDate);
        agreementRole.setType(type);
        persist(agreementRole);
        return agreementRole;
    }
    // }}

    // {{ findAgreementRole
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    @NotContributed
    public AgreementRole findAgreementRole(
            final Agreement agreement, 
            final Party party, 
            final AgreementRoleType type, 
            final @Named("Start Date") LocalDate startDate, 
            final @Named("End Date") LocalDate endDate) {
        
        return firstMatch(AgreementRole.class, new Filter<AgreementRole>() {
            @Override
            public boolean accept(final AgreementRole agreementRole) {
                return agreementRole.getAgreement().equals(agreement) & agreementRole.getParty().equals(party) & agreementRole.getType().equals(type) 
                        //TODO handle optional condition fields as they can contain null
                        // agreementRole.getStartDate().equals(startDate) & agreementRole.getEndDate().equals(endDate)
                        ;
            }
        });
    }
    // }}

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    @NotContributed
    public AgreementRole findAgreementRoleWithType(
            final Agreement agreement, 
            final AgreementRoleType type, 
            final @Named("Date") LocalDate date) {
        
        return firstMatch(AgreementRole.class, new Filter<AgreementRole>() {
            @Override
            public boolean accept(final AgreementRole agreementRole) {
                return agreementRole.getAgreement().equals(agreement) & type.equals(type);
                        //TODO handle optional condition fields as they can contain null
                        // agreementRole.getStartDate().equals(startDate) & agreementRole.getEndDate().equals(endDate)
            }
        });
    }

    
    // {{ allAgreementRoles
    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<AgreementRole> allAgreementRoles() {
        return allInstances(AgreementRole.class);
    }
    // }}


}
