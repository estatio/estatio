package org.estatio.canonical.party.v1;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.services.jaxb.JaxbService;

import org.estatio.canonical.party.PartyDtoFactory;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyDtoFactoryTest {

    private PartyDtoFactory partyDtoFactory;
    private JaxbService.Simple jaxbService = new JaxbService.Simple();

    @Before
    public void setUp() throws Exception {
        partyDtoFactory = new PartyDtoFactory();
    }

    @Test
    public void happy_case() throws Exception {

        // given
        Party p = new Organisation();
        p.setReference("12345678");
        p.setName("New Company");

        // when
        PartyDto partyDto = partyDtoFactory.newDto(p);

        // and when roundtrip
        String xml = jaxbService.toXml(partyDto);
        PartyDto partyDtoAfter = jaxbService.fromXml(PartyDto.class, xml);

        // then
        assertThat(partyDtoAfter.getName()).isEqualTo(p.getName());
        assertThat(partyDtoAfter.getReference()).isEqualTo(p.getReference());
    }


}