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

package org.estatio.module.budget.fixtures.keytables.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.keytable.FoundationValueType;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.keytable.KeyTableRepository;
import org.estatio.module.budget.dom.keytable.KeyValueMethod;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"budget", "name"}, callSuper = false)
@ToString(of={"budget", "name"})
@Accessors(chain = true)
public class KeyTableBuilder extends BuilderScriptAbstract<KeyTable, KeyTableBuilder> {

    @Getter @Setter
    Budget budget;
    @Getter @Setter
    String name;
    @Getter @Setter
    FoundationValueType foundationValueType;
    @Getter @Setter
    KeyValueMethod keyValueMethod;
    @Getter @Setter
    Integer numberOfDigits;

    @Getter
    KeyTable object;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        checkParam("budget", executionContext, Budget.class);
        checkParam("name", executionContext, String.class);

        defaultParam("foundationValueType", executionContext, fakeDataService.enums().anyOf(FoundationValueType.class));
        defaultParam("keyValueMethod", executionContext, fakeDataService.enums().anyOf(KeyValueMethod.class));
        defaultParam("numberOfDigits", executionContext, 3);

        final KeyTable keyTable =
                keyTableRepository.newKeyTable(
                        budget, name, foundationValueType, keyValueMethod, numberOfDigits);
        keyTable.generateItems();

        executionContext.addResult(this, name, keyTable);

        object = keyTable;
    }

    @Inject
    KeyTableRepository keyTableRepository;
    @Inject
    FakeDataService fakeDataService;


}
