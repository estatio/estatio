package org.estatio.canonical.communicationchannel;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.canonical.communicationchannel.v1.PostalAddressDto;
import org.estatio.dom.DtoMappingHelper;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.communicationchannel.PostalAddress;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class PostalAddressDtoFactory {

    @Programmatic
    public PostalAddressDto newDto(final PostalAddress postalAddress) {
        PostalAddressDto dto = new PostalAddressDto();

        dto.setSelf(mappingHelper.oidDtoFor(postalAddress));
        dto.setAddress1(postalAddress.getAddress1());
        dto.setAddress2(postalAddress.getAddress2());
        dto.setAddress3(postalAddress.getAddress3());
        dto.setCity(postalAddress.getCity());

        final State postalAddressState = postalAddress.getState();
        if (postalAddressState != null) {
            dto.setStateReference(postalAddressState.getReference());
            dto.setStateName(postalAddressState.getName());
        }

        final Country postalAddressCountry = postalAddress.getCountry();
        if(postalAddressCountry != null) {
            dto.setCountryReference(postalAddressCountry.getReference());
            dto.setCountryAlpha2Code(postalAddressCountry.getAlpha2Code());
            dto.setCountryName(postalAddressCountry.getName());
        }

        return dto;
    }

    @Inject
    DtoMappingHelper mappingHelper;

    @Inject
    CommunicationChannels communicationChannels;
}
