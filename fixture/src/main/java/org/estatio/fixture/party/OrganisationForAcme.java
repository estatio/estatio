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
package org.estatio.fixture.party;

public class OrganisationForAcme extends OrganisationAbstract {

    public static final String PARTY_REFERENCE = "ACME";

    @Override
    protected void execute(ExecutionContext executionContext) {
        createOrganisation(
                PARTY_REFERENCE +
                ";ACME Properties International;Herengracht 100;null;1010 AA;Amsterdam;null;NLD;+31202211333;+312022211399;info@acme.example.com", executionContext);
    }

}
