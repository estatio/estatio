package org.estatio.module.coda.integtests;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.doc.CodaDocHeadRepository;

public class CodaDocHeadRepository_integtest extends CodaModuleIntegTestAbstract {

    @Test
    public void find_available_works() throws Exception {

        // given
        CodaDocHead cdh1 = new CodaDocHead("X", "Y", "Z", Short.valueOf("1"), LocalDate.now(), LocalDate.now(), "", "", "", "");
        repositoryService.persistAndFlush(cdh1);
        // when
        // then
        Assertions.assertThat(codaDocHeadRepository.findAvailable()).isEmpty();

        // and when
        cdh1.setStatPay("available");
        // then
        Assertions.assertThat(codaDocHeadRepository.findAvailable()).contains(cdh1);

    }

    @Inject CodaDocHeadRepository codaDocHeadRepository;

    @Inject RepositoryService repositoryService;


}
