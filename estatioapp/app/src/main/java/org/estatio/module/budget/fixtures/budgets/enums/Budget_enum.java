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

package org.estatio.module.budget.fixtures.budgets.enums;

import org.joda.time.LocalDate;

import org.estatio.module.asset.fixtures.property.enums.Property_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Budget_enum {

    OxfBudget2015(Property_enum.OxfGb, new LocalDate(2015, 1, 1)),
    OxfBudget2016(Property_enum.OxfGb, new LocalDate(2016, 1, 1)),
    ;

    private final Property_enum property;
    private final LocalDate startDate;

}
