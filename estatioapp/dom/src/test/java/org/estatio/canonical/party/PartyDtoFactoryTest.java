package org.estatio.canonical.party;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.jaxb.JaxbService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.schema.common.v1.OidDto;

import org.estatio.canonical.party.v1.PartyDto;
import org.estatio.dom.DtoMappingHelper;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannelRepository;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyDtoFactoryTest {

    private JaxbService.Simple jaxbService = new JaxbService.Simple();


    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DtoMappingHelper mockDtoMappingHelper;
    @Mock
    private CommunicationChannelRepository mockCommunicationChannelRepository;

    private PartyDtoFactory partyDtoFactory;

    @Before
    public void setUp() throws Exception {
        partyDtoFactory = new PartyDtoFactory();
        partyDtoFactory.mappingHelper = mockDtoMappingHelper;
        partyDtoFactory.communicationChannelRepository = mockCommunicationChannelRepository;
    }

    @Test
    public void happy_case() throws Exception {

        // given
        final Party p = new Organisation();
        p.setReference("12345678");
        p.setName("New Company");

        // expecting
        final OidDto partyOidDto = new OidDto();

        context.checking(new Expectations() {{
            oneOf(mockDtoMappingHelper).oidDtoFor(p);
            will(returnValue(partyOidDto));

            oneOf(mockCommunicationChannelRepository).findByOwnerAndType(p, CommunicationChannelType.POSTAL_ADDRESS);
            will(returnValue(Collections.emptySortedSet()));
        }});

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