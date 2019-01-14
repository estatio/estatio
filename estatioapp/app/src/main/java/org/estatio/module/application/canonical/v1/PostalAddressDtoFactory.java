package org.estatio.module.application.canonical.v1;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.State;

import org.estatio.canonical.communicationchannel.v1.PostalAddressDto;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class PostalAddressDtoFactory extends DtoFactoryAbstract<PostalAddress, PostalAddressDto> {

    public PostalAddressDtoFactory() {
        super(PostalAddress.class, PostalAddressDto.class);
    }

    protected PostalAddressDto newDto(final PostalAddress postalAddress) {
        final PostalAddressDto dto = new PostalAddressDto();

        dto.setSelf(mappingHelper.oidDtoFor(postalAddress));
        dto.setAtPath(postalAddress.getAtPath());

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

}
