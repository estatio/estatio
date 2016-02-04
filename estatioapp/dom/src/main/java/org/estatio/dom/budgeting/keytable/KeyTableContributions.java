/*
 * Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.dom.budgeting.keytable;

import org.apache.isis.applib.annotation.*;
import org.estatio.dom.budgeting.budget.Budget;

import javax.inject.Inject;
import java.util.List;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class KeyTableContributions {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<KeyTable> keyTables(final Budget budget){
        return keyTableRepository.findByBudget(budget);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public KeyTable createKeyTable(final Budget budget,
                                   final String name,
                                   final FoundationValueType foundationValueType,
                                   final KeyValueMethod keyValueMethod,
                                   final Integer numberOfDigits) {
        return keyTableRepository.newKeyTable(budget, name, foundationValueType, keyValueMethod, numberOfDigits);
    }

    public String validateCreateKeyTable(final Budget budget,
                                         final String name,
                                         final FoundationValueType foundationValueType,
                                         final KeyValueMethod keyValueMethod,
                                         final Integer numberOfDigits) {
        return keyTableRepository.validateNewKeyTable(budget, name, foundationValueType, keyValueMethod, numberOfDigits);
    }


    @Inject
    private KeyTableRepository keyTableRepository;

}
