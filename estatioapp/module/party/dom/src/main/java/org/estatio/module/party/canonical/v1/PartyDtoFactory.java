package org.estatio.module.party.canonical.v1;

import java.util.Optional;
import java.util.SortedSet;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

import org.estatio.dom.dto.DtoMappingHelper;
import org.estatio.canonical.party.v1.PartyDto;
import org.estatio.module.party.dom.Party;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class PartyDtoFactory {

    @Programmatic
    public PartyDto newDto(final Party party) {
        PartyDto dto = new PartyDto();
        dto.setSelf(mappingHelper.oidDtoFor(party));
        dto.setName(party.getName());
        dto.setReference(party.getReference());

        final SortedSet<CommunicationChannel> postalAddresses = communicationChannelRepository.findByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS);
        final Optional<CommunicationChannel> postalAddressIfAny = postalAddresses.stream().filter(x -> x.isLegal()).findFirst();

        if(postalAddressIfAny.isPresent()) {
            dto.setLegalPostalAddress(mappingHelper.oidDtoFor(postalAddressIfAny.get()));
        }
        return dto;
    }

    @Inject
    DtoMappingHelper mappingHelper;

    @Inject
    CommunicationChannelRepository communicationChannelRepository;
}
