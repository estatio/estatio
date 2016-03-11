package org.estatio.canonical.party;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.canonical.party.v1.PartyDto;
import org.estatio.dom.DtoMappingHelper;
import org.estatio.dom.party.Party;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class PartyDtoFactory {

    @Programmatic
    public PartyDto newDto(final Party party) {
        PartyDto dto = new PartyDto();
        dto.setName(party.getName());
        dto.setReference(party.getReference());
        return dto;
    }

    @Inject
    DtoMappingHelper mappingHelper;
}
