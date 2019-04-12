package org.estatio.module.party.dom.role;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import org.estatio.module.party.dom.Person;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyRoleTypeService_Test {

    PartyRoleTypeService partyRoleTypeService;

    Person person1, person2;

    List<Person> members = Lists.newArrayList();

    @Before
    public void setUp() throws Exception {

        person1 = new Person();
        person2 = new Person();

        partyRoleTypeService = new PartyRoleTypeService() {

            @Override public List<Person> membersOf(final IPartyRoleType partyRoleType, final Object domainObject) {
                return members;
            }
        };

    }


    @Test
    public void onlyMemberOfElseNone_when_multiple() throws Exception {
        // given
        members.add(person1);
        members.add(person2);

        // when
        final Person person = partyRoleTypeService.onlyMemberOfElseNone((IPartyRoleType) null, null);

        // when
        assertThat(person).isNull();
    }

    @Test
    public void onlyMemberOfElseNone_when_one() throws Exception {
        // given
        members.add(person2);

        // when
        final Person person = partyRoleTypeService.onlyMemberOfElseNone((IPartyRoleType) null, null);

        // when
        assertThat(person).isSameAs(person2);
    }

}