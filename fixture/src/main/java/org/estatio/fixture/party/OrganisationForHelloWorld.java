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

import org.estatio.fixture.EstatioBaseLineFixture;

public class OrganisationForHelloWorld extends OrganisationAbstract {

    public static final String PARTY_REFERENCE = "HELLOWORLD";

    @Override
    protected void execute(ExecutionContext executionContext) {
        createOrganisation(
                PARTY_REFERENCE +
                ";Hello World Properties;5 Covent Garden;;W1A1AA;London;;GBR;+44202211333;+442022211399;info@hello.example.com", executionContext);
    }


}
