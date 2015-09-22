/*
 * Copyright 2015 Yodo Int. Projects and Consultancy
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.estatio.fixture.budget;

import org.joda.time.LocalDate;

import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.keytable.FoundationValueType;
import org.estatio.dom.budgeting.keytable.KeyValueMethod;
import org.estatio.fixture.asset.PropertyForOxfGb;

/**
 * Created by jodo on 22/04/15.
 */
public class KeyTablesForOxf extends KeyTableAbstact {

    public static final String NAME = "Service Charges By Area year 2015";
    public static final String NAME2 = "Service Charges By Count year 2015";
    public static final LocalDate STARTDATE = new LocalDate(2015, 01, 01);
    public static final LocalDate ENDDATE = new LocalDate(2015, 12, 31);
    public static final FoundationValueType BUDGET_FOUNDATION_VALUE_TYPE = FoundationValueType.AREA;
    public static final FoundationValueType BUDGET_FOUNDATION_VALUE_TYPE2 = FoundationValueType.COUNT;
    public static final KeyValueMethod BUDGET_KEY_VALUE_METHOD = KeyValueMethod.PROMILLE;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        if (isExecutePrereqs()) {
            executionContext.executeChild(this, new PropertyForOxfGb());
        }

        // exec
        Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);

        createKeyTable(property, NAME, STARTDATE, ENDDATE, BUDGET_FOUNDATION_VALUE_TYPE, BUDGET_KEY_VALUE_METHOD, 3, executionContext);
        createKeyTable(property, NAME2, STARTDATE, ENDDATE, BUDGET_FOUNDATION_VALUE_TYPE2, BUDGET_KEY_VALUE_METHOD, 3, executionContext);
    }
}
