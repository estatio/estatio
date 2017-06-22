package org.estatio.capex.dom.invoice.inference;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.dom.tenancy.HasAtPath;

import org.estatio.dom.party.PartyRoleTypeEnum;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonRepository;
import org.estatio.dom.party.role.PartyRoleMemberInferenceServiceAbstract;

@DomainService(nature = NatureOfService.DOMAIN)
public class PartyRoleMemberInferenceServiceForPartyRoleAndHasAtPath
        extends PartyRoleMemberInferenceServiceAbstract<PartyRoleTypeEnum, HasAtPath> {

    public PartyRoleMemberInferenceServiceForPartyRoleAndHasAtPath() {
        super(HasAtPath.class,
                PartyRoleTypeEnum.OFFICE_ADMINISTRATOR,
                PartyRoleTypeEnum.LEGAL_MANAGER,
                PartyRoleTypeEnum.COUNTRY_DIRECTOR,
                PartyRoleTypeEnum.TREASURER
                );
    }

    protected final List<Person> doInfer(
            final PartyRoleTypeEnum partyRoleType,
            final HasAtPath hasAtPath) {

        // infer the country / "org unit" from the document
        String atPath = hasAtPath.getAtPath();

        return personRepository.findByRoleTypeAndAtPath(partyRoleType, atPath);
    }


    @Inject
    PersonRepository personRepository;

}
