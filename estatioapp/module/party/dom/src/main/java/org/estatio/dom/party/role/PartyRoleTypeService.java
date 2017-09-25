package org.estatio.dom.party.role;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
    public List<Person> membersOf(final IPartyRoleType partyRoleType) {
        return aggregate(inferenceService -> inferenceService.inferMembersOf(partyRoleType));
    }

    @Programmatic
    public List<Person> membersOf(final IPartyRoleType partyRoleType, final Object domainObject) {
        return aggregate(inferenceService -> inferenceService.inferMembersOf(partyRoleType, domainObject));
    }

    @Programmatic
    public Person firstMemberOf(final IPartyRoleType partyRoleType, final Object domainObject) {
        final List<Person> persons = membersOf(partyRoleType, domainObject);
        return persons != null && !persons.isEmpty() ? persons.get(0) : null;
    }

    /**
     * If there is only one {@link #membersOf(IPartyRoleType, Object)}, then returned; otherwise null.
     */
    @Programmatic
    public Person onlyMemberOfElseNone(final IPartyRoleType partyRoleType, final Object domainObject) {
        final List<Person> persons = membersOf(partyRoleType, domainObject);
        return persons != null && !persons.isEmpty() ? persons.get(0) : null;
    }

    @Programmatic
    public PartyRole createRole(final Party party, final IPartyRoleType iPartyRoleType) {
        final PartyRoleType partyRoleType = iPartyRoleType.findUsing(partyRoleTypeRepository);
        return partyRoleRepository.findOrCreate(party, partyRoleType);
    }


    private List<Person> aggregate(final Function<PartyRoleMemberInferenceService, List<Person>> function) {
        final List<Person> persons = Lists.newArrayList();
        for (final PartyRoleMemberInferenceService inferenceService : inferenceServices) {
            List<Person> personsFromService = function.apply(inferenceService);
            if (personsFromService != null) {
                persons.addAll(thoseWithUsername(personsFromService));
            }
        }
        return Lists.newArrayList(Sets.newLinkedHashSet(persons));
    }
    private static List<Person> thoseWithUsername(final List<Person> persons) {
        return persons.stream().filter(x -> x.getUsername() != null ).collect(Collectors.toList());
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
