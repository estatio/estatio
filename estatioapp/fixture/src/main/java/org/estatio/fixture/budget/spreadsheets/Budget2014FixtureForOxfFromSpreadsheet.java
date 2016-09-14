package org.estatio.fixture.budget.spreadsheets;

import java.net.URL;

import com.google.common.io.Resources;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;

/**
 * Created by jodo on 14/10/15.
 */
public class Budget2014FixtureForOxfFromSpreadsheet extends FixtureScript {

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

