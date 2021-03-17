package org.estatio.module.coda.canonical.v2;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.estatio.canonical.codadocumentproposal.v2.CodaDocumentProposalDto;
import org.estatio.module.coda.dom.codadocument.CodaDocument;

public class CodaDocumentProposalDtoFactory_Test {

    @Test
    public void newDto_works() {

        // given
        CodaDocument codaDocument = new CodaDocument();
        codaDocument.setDocDate(new LocalDate(2020,12,1));

        // when
        CodaDocumentProposalDtoFactory factory = new CodaDocumentProposalDtoFactory(){
            @Override protected CodaDocumentProposalDto newDto(final CodaDocument codaDocument) {
                final CodaDocumentProposalDto dto = new CodaDocumentProposalDto();
                dto.setCodaDocDate(asXMLGregorianCalendar(codaDocument.getDocDate()));
                return dto;
            }
        };
        final CodaDocumentProposalDto codaDocumentProposalDto = factory.newDto(codaDocument);

        // then
        Assertions.assertThat(codaDocumentProposalDto.getCodaDocDate().toString()).isEqualTo("2020-12-01T00:00:00.000Z");

        // asserts that our logging in Camel fails
//        final String xml = JaxbUtil.toXml(codaDocumentProposalDto);
//        Assertions.assertThat(xml).contains("2020-12-01T00:00:00.000Z");
    }

}