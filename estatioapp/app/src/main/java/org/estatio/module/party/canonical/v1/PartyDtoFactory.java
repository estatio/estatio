package org.estatio.module.party.canonical.v1;

import java.util.SortedSet;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.dto.DtoMappingHelper;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

import org.estatio.canonical.party.v1.PartyDto;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;
import org.estatio.module.party.dom.Party;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class PartyDtoFactory extends DtoFactoryAbstract {

    @Programmatic
    public PartyDto newDto(final Party party) {
        PartyDto dto = new PartyDto();
        dto.setSelf(mappingHelper.oidDtoFor(party));
        dto.setAtPath(party.getAtPath());

        dto.setName(party.getName());
        dto.setReference(party.getReference());

        final SortedSet<CommunicationChannel> postalAddresses =
                communicationChannelRepository.findByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS);
        postalAddresses.stream()
                .filter(CommunicationChannel::isLegal)
                .findFirst()
                .ifPresent(
                    postalAddress -> dto.setLegalPostalAddress(mappingHelper.oidDtoFor(postalAddress))
                );

        return dto;
    }

    @Inject
    DtoMappingHelper mappingHelper;

    @Inject
    CommunicationChannelRepository communicationChannelRepository;
}
