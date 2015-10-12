/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.fixturescripts;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;
import org.estatio.fixture.budget.spreadsheets.CreateUsingSpreadsheet;
import org.estatio.fixture.budget.spreadsheets.KeyTableImport;

public class CreateKeyTablesFromSpreadsheet extends DiscoverableFixtureScript {

    @Override
    protected void execute(final ExecutionContext ec) {

        // defaults
        final int number = defaultParam("number", ec, 3);

        //
        // execute
        //
        ec.executeChild(this, new CreateUsingSpreadsheet<>(KeyTableImport.class, "keyTableImportCar2014" +
                ".xls"));

//        ec.executeChild(this, new CreateUsingSpreadsheet<>(BudgetImport.class, "budgetImportCar2014" +
//                ".xls"));


    }

    private <T> CreateUsingSpreadsheet<T> execute(final ExecutionContext ec, final Class<T> aClass) {
        CreateUsingSpreadsheet fs1 = new CreateUsingSpreadsheet<>(aClass);
        ec.executeChild(this, fs1);
        return fs1;
    }

}
