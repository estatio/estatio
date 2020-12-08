package org.estatio.module.coda.integtests;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import org.estatio.module.coda.dom.CodaCurrency;
import org.estatio.module.coda.dom.CodaDocumentType;
import org.estatio.module.coda.dom.codadocument.CodaDocument;
import org.estatio.module.coda.dom.codadocument.CodaDocumentRepository;

public class CodaDocumentRepository_integtest extends CodaModuleIntegTestAbstract {

    @Test
    public void find_or_create_and_finders_work() throws Exception {

        final String cmpCode = "IT01";
        final String docCode = "IT00COV19";
        final String codaPeriod = "2020/13";
        final String atPath = "/ITA";
        final LocalDate docDate = new LocalDate(2020,10,1);

        // given
        Assertions.assertThat(codaDocumentRepository.listAll()).isEmpty();

        // when
        codaDocumentRepository.findOrCreateForAmortisation(
                CodaDocumentType.INITIAL_COVID_AMORTISATION,
                cmpCode,
                docCode,
                codaPeriod,
                docDate,
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
        Assertions.assertThat(document.getDocDate()).isEqualTo(docDate);

        final List<CodaDocument> byDocumentTypeAndCmpCodeAndDocCodeAndDocDate = codaDocumentRepository
                .findByDocumentTypeAndCmpCodeAndDocCodeAndDocDate(CodaDocumentType.INITIAL_COVID_AMORTISATION, cmpCode,
                        docCode, docDate);
        Assertions.assertThat(byDocumentTypeAndCmpCodeAndDocCodeAndDocDate).hasSize(1);
        final CodaDocument codaDocument = byDocumentTypeAndCmpCodeAndDocCodeAndDocDate.get(0);
        Assertions.assertThat(codaDocument).isEqualTo(document);

        List<CodaDocument> unpostedByAtPathIta = codaDocumentRepository.findUnpostedByAtPath(atPath);
        final List<CodaDocument> unpostedByAtPathFra = codaDocumentRepository.findUnpostedByAtPath("/FRA");
        Assertions.assertThat(unpostedByAtPathIta).hasSize(1);
        Assertions.assertThat(unpostedByAtPathFra).isEmpty();

        // and when again
        codaDocumentRepository.findOrCreateForAmortisation(
                CodaDocumentType.INITIAL_COVID_AMORTISATION,
                cmpCode,
                docCode,
                codaPeriod,
                docDate,
                atPath);
        // then still ( = idempotent)
        Assertions.assertThat(codaDocumentRepository.listAll()).hasSize(1);

        // and when posted
        codaDocument.setPostedAt(new LocalDateTime());
        // then
        unpostedByAtPathIta = codaDocumentRepository.findUnpostedByAtPath(atPath);
        Assertions.assertThat(unpostedByAtPathIta).isEmpty();


    }

    @Inject CodaDocumentRepository codaDocumentRepository;


}
