package org.estatio.module.coda.canonical.v2;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.canonical.coda.v2.CodaDocHeadDto;
import org.estatio.canonical.coda.v2.CodaDocHeadType;
import org.estatio.canonical.common.v2.CodaDocKey;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;
import org.estatio.module.coda.dom.doc.CodaDocHead;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "coda.canonical.v2.CodaDocHeadDtoFactory"
)
public class CodaDocHeadDtoFactory extends DtoFactoryAbstract<CodaDocHead, CodaDocHeadDto> {

    public CodaDocHeadDtoFactory() {
        super(CodaDocHead.class, CodaDocHeadDto.class);
    }

    protected CodaDocHeadDto newDto(final CodaDocHead codaDocHead) {
        final CodaDocHeadDto dto = new CodaDocHeadDto();
        dto.setMajorVersion("2");
        dto.setMinorVersion("0");

        copyOver(codaDocHead, dto);

        return dto;
    }

    CodaDocHeadType newType(final CodaDocHead codaDocHead) {
        final CodaDocHeadType dto = new CodaDocHeadType();
        copyOver(codaDocHead, dto);
        return dto;
    }

    private void copyOver(final CodaDocHead codaDocHead, final CodaDocHeadType dto) {
        dto.setSelf(mappingHelper.oidDtoFor(codaDocHead));

        final CodaDocKey docKey = new CodaDocKey();
        docKey.setCmpCode(codaDocHead.getCmpCode());
        docKey.setDocCode(codaDocHead.getDocCode());
        docKey.setDocNum(codaDocHead.getDocNum());
        dto.setCodaDocKey(docKey);

        dto.setCodaPeriod(codaDocHead.getCodaPeriod());

        dto.setIncomingInvoice(mappingHelper.oidDtoFor(codaDocHead.getIncomingInvoice()));
    }


}
