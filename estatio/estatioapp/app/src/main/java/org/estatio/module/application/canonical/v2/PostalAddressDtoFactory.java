package org.estatio.module.application.canonical.v2;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.State;

import org.estatio.canonical.communicationchannel.v2.PostalAddressDto;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "application.canonical.v2.PostalAddressDtoFactory"
)
public class PostalAddressDtoFactory extends DtoFactoryAbstract<PostalAddress, PostalAddressDto> {

    public PostalAddressDtoFactory() {
        super(PostalAddress.class, PostalAddressDto.class);
    }

    @Programmatic
    public PostalAddressDto newDto(final PostalAddress postalAddress) {
        PostalAddressDto dto = new PostalAddressDto();
        dto.setMajorVersion("2");
        dto.setMinorVersion("0");

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
