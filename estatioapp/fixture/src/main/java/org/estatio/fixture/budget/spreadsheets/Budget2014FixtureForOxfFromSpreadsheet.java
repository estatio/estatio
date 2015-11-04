package org.estatio.fixture.budget.spreadsheets;

import com.google.common.io.Resources;
import org.estatio.fixture.EstatioFixtureScript;
import org.isisaddons.module.excel.dom.ExcelFixture;

import java.net.URL;

/**
 * Created by jodo on 14/10/15.
 */
public class Budget2014FixtureForOxfFromSpreadsheet extends EstatioFixtureScript{

    @Override
    protected void execute(ExecutionContext ec) {

        final URL excelResource = Resources.getResource(getClass(), "budgetOxf2014Import.xls");
        final ExcelFixture excelFixture = new ExcelFixture(
                excelResource,
                ChargeImport.class,
                KeyTableImport.class,
                BudgetImport.class
        );
        ec.executeChild(this, excelFixture);
    }

}

