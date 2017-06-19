package org.estatio.dom.party.role;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.party.Party;
import org.estatio.dom.party.Person;

@DomainService(nature = NatureOfService.DOMAIN)
public class PartyRoleTypeService {

    @PostConstruct
    @Programmatic
    public void init() {
        for (PartyRoleTypeServiceSupport supportService : supportServices) {
            List<IPartyRoleType> partyRoleTypes = supportService.listAll();
            for (IPartyRoleType partyRoleType : partyRoleTypes) {
                partyRoleType.findOrCreateUsing(partyRoleTypeRepository);
            }
        }
    }

    @Programmatic
    public List<Person> membersOf(final IPartyRoleType partyRoleType, final Object domainObject) {
        for (PartyRoleMemberInferenceService inferenceService : inferenceServices) {
            List<Person> persons = inferenceService.inferMembersOf(partyRoleType, domainObject);
            if(persons != null) {
                return persons;
            }
        }
        return null;
    }

    @Programmatic
    public PartyRole createRole(final Party party, final IPartyRoleType iPartyRoleType) {
        final PartyRoleType partyRoleType = iPartyRoleType.findUsing(partyRoleTypeRepository);
        return partyRoleRepository.findOrCreate(party, partyRoleType);
    }

    @Inject
    List<PartyRoleTypeServiceSupport> supportServices;

    @Inject
    List<PartyRoleMemberInferenceService> inferenceServices;

    @Inject
    PartyRoleRepository partyRoleRepository;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

}
