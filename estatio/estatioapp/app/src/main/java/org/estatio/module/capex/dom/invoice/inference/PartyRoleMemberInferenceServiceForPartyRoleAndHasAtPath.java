package org.estatio.module.capex.dom.invoice.inference;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.dom.tenancy.HasAtPath;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.role.PartyRoleMemberInferenceServiceAbstract;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;


@DomainService(nature = NatureOfService.DOMAIN)
public class PartyRoleMemberInferenceServiceForPartyRoleAndHasAtPath
        extends PartyRoleMemberInferenceServiceAbstract<PartyRoleTypeEnum, HasAtPath> {

    public static final Logger LOG = LoggerFactory.getLogger(PartyRoleMemberInferenceServiceForPartyRoleAndHasAtPath.class);

    public PartyRoleMemberInferenceServiceForPartyRoleAndHasAtPath() {
        super(HasAtPath.class, PartyRoleTypeEnum.class);
    }

    protected final List<Person> doInferMembersOf(
            final PartyRoleTypeEnum partyRoleType,
            final HasAtPath hasAtPath) {

        // infer the country / "org unit" from the document
        String atPath = hasAtPath.getAtPath();
        List<Person> personsWithRoleType = doInferMembersOf(partyRoleType);

        for (Person person : personRepository.findWithUsername()) {
            if (applicationUserRepository.findByUsername(person.getUsername()) == null) {
                LOG.warn(String.format("Username %s could not be found for Person with reference %s", person.getUsername(), person.getReference()));
            }
        }

        return personRepository.findWithUsername()
                .stream()
                .map(person -> applicationUserRepository.findByUsername(person.getUsername()))
                .filter(applicationUser -> applicationUser != null && applicationUser.getAtPath() != null && applicationUser.getAtPath().contains(atPath)) // if atPath is null on applicationUser, then the user won't show up in suggestions (which is intended because they should have one)
                .map(applicationUser -> personRepository.findByUsername(applicationUser.getUsername()))
                .filter(personsWithRoleType::contains)
                .collect(Collectors.toList());
    }

    @Override
    protected List<Person> doInferMembersOf(final PartyRoleTypeEnum partyRoleType) {
        return personRepository.findByRoleType(partyRoleType);
    }

    @Inject
    PersonRepository personRepository;

    @Inject ApplicationUserRepository applicationUserRepository;

}
