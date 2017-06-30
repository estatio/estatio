package org.estatio.capex.dom.documents;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.incode.module.document.dom.impl.docs.Document;

public class IncomingDocumentRepository_Test {


    @Test
    public void matchAllIncomingDocumentsByName_works() throws Exception {

        // given
        String name1 = "3010100012.pdf";
        String name2 = "3010100013.pdf";
        String name3 = "3020100013.pdf";
        Document doc1 = new Document(null, null, name1, null, null);
        Document doc2 = new Document(null, null, name2, null, null);
        Document doc3 = new Document(null, null, name3, null, null);

        IncomingDocumentRepository incomingDocumentRepository = new IncomingDocumentRepository(){
                @Override
                public List<Document> findAllIncomingDocuments() {
                    return Arrays.asList(doc1, doc2, doc3);
                }
            };

        // when
        List<Document> docsFound = incomingDocumentRepository.matchAllIncomingDocumentsByName(".pDf");
        // then
        Assertions.assertThat(docsFound.size()).isEqualTo(3);

        // when
        docsFound = incomingDocumentRepository.matchAllIncomingDocumentsByName("30101");
        // then
        Assertions.assertThat(docsFound.size()).isEqualTo(2);

        // when
        docsFound = incomingDocumentRepository.matchAllIncomingDocumentsByName("201");
        // then
        Assertions.assertThat(docsFound.size()).isEqualTo(1);

        // when
        docsFound = incomingDocumentRepository.matchAllIncomingDocumentsByName("202");
        // then
        Assertions.assertThat(docsFound.size()).isEqualTo(0);

    }

}