package org.estatio.fixture.budget.spreadsheets;

import org.apache.isis.applib.fixturescripts.FixtureScript;

/**
 * Created by jodo on 14/10/15.
 */
public class _ChargeFixtureForGbrFromSpreadsheet extends FixtureScript {


    @Override
    protected void execute(ExecutionContext ec) {

        ec.executeChild(this, "chargeImport", new CreateUsingSpreadsheet<>(ChargeImport.class, "chargeImport.xls"));

    }

}

