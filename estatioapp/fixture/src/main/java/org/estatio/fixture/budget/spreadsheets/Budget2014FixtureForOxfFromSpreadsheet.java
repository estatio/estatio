package org.estatio.fixture.budget.spreadsheets;

import org.estatio.fixture.EstatioFixtureScript;

/**
 * Created by jodo on 14/10/15.
 */
public class Budget2014FixtureForOxfFromSpreadsheet extends EstatioFixtureScript{

    @Override
    protected void execute(ExecutionContext ec) {

        ec.executeChild(this, "a", new CreateUsingSpreadsheet<>(ChargeImport.class, "chargeImport.xls"));
        ec.executeChild(this, "b", new CreateUsingSpreadsheet<>(KeyTableImport.class, "keyTablesImport.xls"));
        ec.executeChild(this, "c", new CreateUsingSpreadsheet<>(BudgetImport.class, "budgetImport.xls"));

    }

}

