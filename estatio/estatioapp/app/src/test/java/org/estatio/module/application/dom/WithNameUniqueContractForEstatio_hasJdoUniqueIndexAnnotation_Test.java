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
package org.estatio.module.application.dom;

import org.incode.module.base.dom.with.WithNameUnique;
import org.incode.module.unittestsupport.dom.with.WithFieldUniqueContractTestAllAbstract;

public class WithNameUniqueContractForEstatio_hasJdoUniqueIndexAnnotation_Test extends
        WithFieldUniqueContractTestAllAbstract<WithNameUnique> {

    public WithNameUniqueContractForEstatio_hasJdoUniqueIndexAnnotation_Test() {
        super("org.estatio", "name", WithNameUnique.class);
    }

}
