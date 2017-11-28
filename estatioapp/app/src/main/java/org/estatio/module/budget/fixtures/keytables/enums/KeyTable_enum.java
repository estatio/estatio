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

package org.estatio.module.budget.fixtures.keytables.enums;

import org.joda.time.LocalDate;

import org.estatio.module.budget.dom.keytable.FoundationValueType;
import org.estatio.module.budget.dom.keytable.KeyValueMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum KeyTable_enum {

    Oxf2015Area ("Service Charges By Area year 2015", FoundationValueType.AREA, KeyValueMethod.PROMILLE, new LocalDate(2015, 1, 1), 3),
    Oxf2015Count("Service Charges By Count year 2015", FoundationValueType.COUNT, KeyValueMethod.PROMILLE, new LocalDate(2015, 1, 1), 3),
    ;

    private final String name;
    private final FoundationValueType foundationValueType;
    private final KeyValueMethod keyValueMethod;
    private final LocalDate startDate;
    private final int numberOfDigits;
}
