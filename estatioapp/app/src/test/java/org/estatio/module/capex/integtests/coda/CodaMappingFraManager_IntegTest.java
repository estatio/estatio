package org.estatio.module.capex.integtests.coda;

import javax.inject.Inject;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.value.Blob;

import org.incode.module.base.dom.MimeTypeData;

import org.estatio.module.capex.dom.coda.CodaMappingRepository;
import org.estatio.module.capex.imports.CodaMappingFraManager;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CodaMappingFraManager_IntegTest extends CapexModuleIntegTestAbstract {

    @Before
    public void setupData() {
    }

    @Test
    public void upload() throws Exception {

        try {

            String fileName = "CODAMappings.xlsx";

            final byte[] pdfBytes = Resources.toByteArray(
                    Resources.getResource(CodaMappingFraManager_IntegTest.class, fileName));
            final Blob blob = new Blob(fileName, MimeTypeData.APPLICATION_PDF.asStr(), pdfBytes);


            // When
            wrap(new CodaMappingFraManager()).upload(blob);

            // Then
            assertThat(codaMappingRepository.all()).hasSize(46);

        } catch(Exception ex) {

            // getting a hard-to-diagnose exception in CI; so this is an attempt to get more detail as to the issue.
            /*
-------------------------------------------------------------------------------
Test set: org.estatio.module.capex.integtests.coda.CodaMappingFraManager_IntegTest
-------------------------------------------------------------------------------
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 33.389 sec <<< FAILURE! - in org.estatio.module.capex.integtests.coda.CodaMappingFraManager_
upload(org.estatio.module.capex.integtests.coda.CodaMappingFraManager_IntegTest)  Time elapsed: 32.998 sec  <<< ERROR!
java.lang.RuntimeException: java.lang.RuntimeException: java.util.concurrent.ExecutionException: java.lang.RuntimeException: java.util.concurrent.ExecutionE
Caused by: java.lang.RuntimeException: java.util.concurrent.ExecutionException: java.lang.RuntimeException: java.util.concurrent.ExecutionException: java.la
Caused by: java.util.concurrent.ExecutionException: java.lang.RuntimeException: java.util.concurrent.ExecutionException: java.lang.NullPointerException
Caused by: java.lang.RuntimeException: java.util.concurrent.ExecutionException: java.lang.NullPointerException
Caused by: java.util.concurrent.ExecutionException: java.lang.NullPointerException
Caused by: java.lang.NullPointerException

and

<testcase name="upload" classname="org.estatio.module.capex.integtests.coda.CodaMappingFraManager_IntegTest" time="32.998">
    <error message="java.lang.RuntimeException: java.util.concurrent.ExecutionException: java.lang.RuntimeException: java.util.concurrent.ExecutionException
Caused by: java.lang.RuntimeException: java.util.concurrent.ExecutionException: java.lang.RuntimeException: java.util.concurrent.ExecutionException: java.la
Caused by: java.util.concurrent.ExecutionException: java.lang.RuntimeException: java.util.concurrent.ExecutionException: java.lang.NullPointerException
Caused by: java.lang.RuntimeException: java.util.concurrent.ExecutionException: java.lang.NullPointerException
Caused by: java.util.concurrent.ExecutionException: java.lang.NullPointerException
Caused by: java.lang.NullPointerException
</error>
             */

            Assertions.fail(Throwables.getStackTraceAsString(ex));
        }

    }

    @Inject CodaMappingRepository  codaMappingRepository;

}
