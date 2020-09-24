package org.estatio.module.coda.integtests;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.coda.dom.CodaCurrency;
import org.estatio.module.coda.dom.CodaDocumentType;
import org.estatio.module.coda.dom.codadocument.CodaDocument;
import org.estatio.module.coda.dom.codadocument.CodaDocumentRepository;

public class CodaDocumentRepository_integtest extends CodaModuleIntegTestAbstract {

    @Test
    public void create_and_finder_works() throws Exception {

        final String cmpCode = "IT01";
        final String docCode = "IT00COV19";
        final String atPath = "/ITA";

        // given
        Assertions.assertThat(codaDocumentRepository.listAll()).isEmpty();

        // when
        codaDocumentRepository.create(CodaDocumentType.INITIAL_COVID_AMORTISATION, cmpCode, docCode,
                atPath);

        // then
        Assertions.assertThat(codaDocumentRepository.listAll()).hasSize(1);
        final List<CodaDocument> docs = codaDocumentRepository
                .findByCmpCodeAndDocCodeAndDocNum(cmpCode, docCode, null);
        Assertions.assertThat(docs).hasSize(1);
        final CodaDocument document = docs.get(0);
        Assertions.assertThat(document.getAtPath()).isEqualTo(atPath);
        Assertions.assertThat(document.getUuid()).isNotNull();
        Assertions.assertThat(document.getCurrency()).isEqualTo(CodaCurrency.EUR);
        Assertions.assertThat(document.getCreatedAt()).isNotNull();

    }

    @Inject CodaDocumentRepository codaDocumentRepository;


}
