package org.estatio.integtests.capex.coda;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.apache.isis.applib.value.Blob;

import org.estatio.capex.dom.coda.CodaMappingRepository;
import org.estatio.capex.dom.coda.contributions.CodaMappingManager;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CodaMappingManager_IntegTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
    }

    @Test
    public void upload() throws Exception {

        String fileName = "CODAMappings.xlsx";

        final byte[] pdfBytes = Resources.toByteArray(
                Resources.getResource(CodaMappingManager_IntegTest.class, fileName));
        final Blob blob = new Blob(fileName, "application/pdf", pdfBytes);


        // When
        wrapperFactory.wrap(new CodaMappingManager()).upload(blob);

        // Then
        assertThat(codaMappingRepository.all()).hasSize(46);

    }

    @Inject WrapperFactory wrapperFactory;

    @Inject CodaMappingRepository  codaMappingRepository;

}
