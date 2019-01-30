package org.estatio.module.asset.canonical.v2;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.canonical.fixedasset.v2.FixedAssetDto;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "asset.canonical.v2.FixedAssetDtoFactory"
)
public class FixedAssetDtoFactory extends DtoFactoryAbstract<FixedAsset, FixedAssetDto> {

    public FixedAssetDtoFactory() {
        super(FixedAsset.class, FixedAssetDto.class);
    }

    @Override
    protected FixedAssetDto newDto(final FixedAsset fixedAsset) {
        FixedAssetDto dto = new FixedAssetDto();
        dto.setMajorVersion("2");
        dto.setMinorVersion("0");

        dto.setSelf(mappingHelper.oidDtoFor(fixedAsset));
        dto.setReference(fixedAsset.getReference());

        return dto;
    }
}
