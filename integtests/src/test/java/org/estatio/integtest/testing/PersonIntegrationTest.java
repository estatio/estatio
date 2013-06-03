package org.estatio.integtest.testing;

import org.junit.Test;

import org.estatio.dom.party.Person;

public class PersonIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void cannotModifyName() throws Exception {
        Person party = wrap((Person)parties.findParties("Doe, Jo*").get(0));
        
        expectedExceptions.expectMessage("Cannot be updated directly; derived from first and last names");
        party.setName("Cannot change name directly");
    }

}
