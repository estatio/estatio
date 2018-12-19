package org.estatio.module.party.canonical.v1;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.dto.DtoMappingHelper;
import org.apache.isis.applib.services.jaxb.JaxbService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.schema.common.v1.OidDto;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

import org.estatio.canonical.party.v1.PartyDto;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyDtoFactory_Test {

    private JaxbService.Simple jaxbService = new JaxbService.Simple();


    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DtoMappingHelper mockMappingHelper;
    @Mock
    private CommunicationChannelRepository mockCommunicationChannelRepository;

    private PartyDtoFactory partyDtoFactory;

    @Before
    public void setUp() throws Exception {
        partyDtoFactory = new PartyDtoFactory() {
            PartyDtoFactory withMappingHelper(DtoMappingHelper mappingHelper) {
                super.mappingHelper = mappingHelper;
                return this;
            }
        }.withMappingHelper(mockMappingHelper);
        partyDtoFactory.communicationChannelRepository = mockCommunicationChannelRepository;
    }

    @Test
    public void happy_case() throws Exception {

        // given
        final Party p = new Organisation() {
            @Override public String getAtPath() {
                return "/FRA";
            }
        };
        p.setReference("12345678");
        p.setName("New Company");

        // expecting
        final OidDto partyOidDto = new OidDto();

        context.checking(new Expectations() {{
            oneOf(mockMappingHelper).oidDtoFor(p);
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