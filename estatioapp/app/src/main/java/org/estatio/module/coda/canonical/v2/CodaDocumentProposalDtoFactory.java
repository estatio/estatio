package org.estatio.module.coda.canonical.v2;

import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.canonical.codadocumentproposal.v2.CodaDocumentLineType;
import org.estatio.canonical.codadocumentproposal.v2.CodaDocumentProposalDto;
import org.estatio.canonical.codadocumentproposal.v2.DocumentType;
import org.estatio.canonical.codadocumentproposal.v2.LineSense;
import org.estatio.canonical.codadocumentproposal.v2.LineType;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;
import org.estatio.module.coda.dom.CodaDocumentType;
import org.estatio.module.coda.dom.codadocument.CodaDocument;
import org.estatio.module.coda.dom.codadocument.CodaDocumentLine;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "coda.canonical.v2.CodaDocumentProposalDtoFactory"
)
public class CodaDocumentProposalDtoFactory extends DtoFactoryAbstract<CodaDocument, CodaDocumentProposalDto> {

    public CodaDocumentProposalDtoFactory() {
        super(CodaDocument.class, CodaDocumentProposalDto.class);
    }

    protected CodaDocumentProposalDto newDto(final CodaDocument codaDocument) {
        final CodaDocumentProposalDto dto = new CodaDocumentProposalDto();
        dto.setMajorVersion("2");
        dto.setMinorVersion("0");

        dto.setSelf(mappingHelper.oidDtoFor(codaDocument));
        dto.setDocumentType(documentTypeOf(codaDocument.getDocumentType()));
        dto.setCmpCode(codaDocument.getCmpCode());
        dto.setDocCode(codaDocument.getDocCode());
        dto.setDocNum(codaDocument.getDocNum()); // TODO: at time of posting is null, so is this needed?
        dto.setCodaDocDate(asXMLGregorianCalendar(codaDocument.getDocDate())); // TODO: at the moment null...
        dto.setCodaPeriod(codaDocument.getCodaPeriod());
        final List<CodaDocumentLineType> lineDtos = new ArrayList<>();
        for (CodaDocumentLine line : codaDocument.getLines()){

            final CodaDocumentLineType lineType = new CodaDocumentLineType();
            lineType.setSelf(mappingHelper.oidDtoFor(line));
            lineType.setLineNumber(line.getLineNumber());
            lineType.setLineType(lineTypeFor(line.getLineType()));
            lineType.setElement1(line.getElement1());
            lineType.setElement2(line.getElement2());
            lineType.setElement3(line.getElement3());
            lineType.setElement4(line.getElement4());
            lineType.setElement5(line.getElement5());
            lineType.setElement6(line.getElement6());
            lineType.setValueDate(asXMLGregorianCalendar(line.getValueDate()));
            lineType.setHomeValue(line.getHomeValue());
            lineType.setDocValue(line.getDocValue());
            lineType.setLineSense(lineSenseFor(line.getLineSense()));
            lineType.setDescription(line.getDescription());
            lineType.setExternalReference1(line.getExternalReference1());
            lineType.setExternalReference2(line.getExternalReference2());
            lineType.setExternalReference3(line.getExternalReference3());
            lineType.setExternalReference4(line.getExternalReference4());
            lineType.setExternalReference5(line.getExternalReference5());
            lineType.setExternalReference6(line.getExternalReference6());

            lineDtos.add(lineType);

        }
        dto.setLines(lineDtos);
        return dto;
    }

    private DocumentType documentTypeOf(final CodaDocumentType codaDocumentType){
        switch (codaDocumentType){
        case INITIAL_COVID_AMORTISATION:
            return DocumentType.INITIAL_COVID_AMORTISATION;
        case RECURRING_COVID_AMORTISATION:
            return DocumentType.RECURRING_COVID_AMORTISATION;
        default:
            return null;
        }
    }

    private LineType lineTypeFor(final org.estatio.module.coda.dom.LineType lineType){
        switch (lineType){
        case SUMMARY:
            return LineType.SUMMARY;
        case ANALYSIS:
            return LineType.ANALYSIS;
        default:
            return null;
        }
    }

    private LineSense lineSenseFor(final org.estatio.module.coda.dom.LineSense lineSense){
        switch (lineSense){
        case DEBIT:
            return LineSense.DEBIT;
        case CREDIT:
            return LineSense.CREDIT;
        default:
            return null;
        }
    }


}
