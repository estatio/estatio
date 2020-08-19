package org.estatio.module.lease.fixtures.imports;

import java.io.IOException;

import com.google.common.io.Resources;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.value.Blob;

@Programmatic
public class SalesAreaLicencesImportFixture extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        final byte[] excelBytes;
        try {
            excelBytes = Resources.toByteArray(Resources.getResource(getClass(), "sales_area_licences_OXF.xlsx"));
            final Blob blob = new Blob("sales_area_licences_OXF.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelBytes);
            executionContext.addResult(this, blob);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
