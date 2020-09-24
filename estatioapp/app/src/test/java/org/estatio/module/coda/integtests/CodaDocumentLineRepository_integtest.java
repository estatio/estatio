package org.estatio.module.coda.integtests;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.coda.dom.CodaDocumentType;
import org.estatio.module.coda.dom.LineType;
import org.estatio.module.coda.dom.codadocument.CodaDocument;
import org.estatio.module.coda.dom.codadocument.CodaDocumentLine;
import org.estatio.module.coda.dom.codadocument.CodaDocumentLineRepository;
import org.estatio.module.coda.dom.codadocument.CodaDocumentRepository;

public class CodaDocumentLineRepository_integtest extends CodaModuleIntegTestAbstract {

    @Test
    public void create_and_finder_works() throws Exception {

        final String cmpCode = "IT01";
        final String docCode = "IT00COV19";
        final String atPath = "/ITA";
        final int lineNumber = 1;
        final LineType lineType = LineType.ANALYSIS;

        // given
        Assertions.assertThat(codaDocumentLineRepository.listAll()).isEmpty();
        final CodaDocument document = codaDocumentRepository
                .create(CodaDocumentType.INITIAL_COVID_AMORTISATION, cmpCode, docCode,
                        atPath);

        // when
        final CodaDocumentLine codaDocumentLine = codaDocumentLineRepository.create(document, lineNumber,
                lineType);

        // then
        Assertions.assertThat(codaDocumentLine.getDocument()).isEqualTo(document);
        Assertions.assertThat(codaDocumentLine.getLineNumber()).isEqualTo(lineNumber);
        Assertions.assertThat(codaDocumentLine.getLineType()).isEqualTo(lineType);
        final CodaDocumentLine unique = codaDocumentLineRepository.findUnique(document, lineNumber);
        Assertions.assertThat(unique).isEqualTo(codaDocumentLine);

    }

    @Inject CodaDocumentRepository codaDocumentRepository;

    @Inject CodaDocumentLineRepository codaDocumentLineRepository;


}
