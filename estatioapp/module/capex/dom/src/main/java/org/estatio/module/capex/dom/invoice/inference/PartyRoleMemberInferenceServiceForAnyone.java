package org.estatio.module.capex.dom.invoice.inference;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.app.user.MeService;

import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleMemberInferenceService;

@DomainService(nature = NatureOfService.DOMAIN, menuOrder = "100")
public class PartyRoleMemberInferenceServiceForAnyone implements PartyRoleMemberInferenceService {

    @Override
    public List<Person> inferMembersOf(final IPartyRoleType partyRoleType) {

        // as used to provide a list of choices for the end-user to select for the next task

        final String myPath = meService.me().getAtPath();
        final List<Person> people = personRepository.findWithUsername();
        return people.stream()
                .filter(x -> Objects.equals(x.getApplicationTenancyPath(), myPath))
                .collect(Collectors.toList());
    }

    @Override
    public List<Person> inferMembersOf(
            final IPartyRoleType partyRoleType,
            final Object domainObjectAsContext) {
        // don't get involved here
        return null;
    }

    @Inject
    MeService meService;

    @Inject
    PersonRepository personRepository;

}
