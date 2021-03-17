package org.estatio.module.lease.fixtures.imports;

import com.google.common.io.Resources;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.value.Blob;

import java.io.IOException;

@Programmatic
public class SectorAndActivityImportFixture extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        final byte[] excelBytes1;
        final byte[] excelBytes2;
        try {
            excelBytes1 = Resources.toByteArray(Resources.getResource(getClass(), "sector_and_activity_import_correct.xlsx"));
            final Blob blob1 = new Blob("sector_and_activity_import_correct.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelBytes1);
            executionContext.addResult(this, blob1);

            excelBytes2 = Resources.toByteArray(Resources.getResource(getClass(), "sector_and_activity_import_wrong.xlsx"));
            final Blob blob2 = new Blob("sector_and_activity_import_wrong.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelBytes2);
            executionContext.addResult(this, blob2);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
