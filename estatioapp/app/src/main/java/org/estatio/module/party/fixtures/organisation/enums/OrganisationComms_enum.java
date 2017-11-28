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
package org.estatio.module.party.fixtures.organisation.enums;

import org.estatio.module.country.fixtures.enums.Country_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.country.fixtures.enums.Country_enum.GBR;
import static org.estatio.module.country.fixtures.enums.Country_enum.NLD;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum OrganisationComms_enum {

    AcmeNl(Organisation_enum.AcmeNl,
            "Herengracht 100", null, "1010 AA", "Amsterdam", null, NLD, false,
            "+31202211333",
            "+312022211399",
            "info@acme.example.com"),
    DagoBankGb(Organisation_enum.DagoBankGb,
            null, null, null, null, null, null, false,
            null,
            null,
            "london.office@dagobank.example.com"),
    DagoBankNl(Organisation_enum.DagoBankNl,
            "Herengracht 333", null, "1016 BA", "Amsterdam", null, NLD, false,
            "+31202211333",
            "+312022211399",
            "amsterdam.office@dagobank.example.com"),
/*
    HelloWorldFr(Organisation_enum.HelloWorldFr,
            null, null, null, null, null, null, false,
            null,
            null,
            null),
*/
    HelloWorldGb(Organisation_enum.HelloWorldGb,
            "5 Covent Garden", null, "W1A1AA", "London", null, GBR, false,
            "+44202211333",
            "+442022211399",
            "info@hello.example.com"),
    HelloWorldGb_2(Organisation_enum.HelloWorldGb,
            "1 Circle Square", null, "W2AXXX", "London", null, GBR, false,
            null,
            null,
            null),
/*
    HelloWorldIt(Organisation_enum.HelloWorldIt,
            null, null, null, null, null, null, false,
            null,
            null,
            null),
    HelloWorldNl(Organisation_enum.HelloWorldNl,
            null, null, null, null, null, null, false,
            null,
            null,
            null),
    HelloWorldSe(Organisation_enum.HelloWorldSe,
            null, null, null, null, null, null, false,
            null,
            null,
            null),
*/
    HyperNl(Organisation_enum.HyperNl,
            "Javaplein", null, "1016 BA", "Amsterdam", null, NLD, false,
            "+31202211333",
            "+312022211399",
            null),
    MediaXGb(Organisation_enum.MediaXGb,
            "85 High St", null, "EN11 8TL", "Hoddesdon", null, GBR, false,
            "+442079897676",
            "+442079897677",
            "info@mediax.example.com"),
    MediaXNl(Organisation_enum.MediaXNl,
            "Herengracht 100", null, "1010 AA", "Amsterdam", null, GBR, false,
            "+31202211333",
            "+312022211399",
            "info@mediax.example.com"),
/*
    MiracleGb(Organisation_enum.MiracleGb,
            null, null, null, null, null, null, false,
            null,
            null,
            null),
    MiracleNl(Organisation_enum.MiracleNl,
            null, null, null, null, null, null, false,
            null,
            null,
            null),
*/
    NlBankNl(Organisation_enum.NlBankNl,
            "Single 2222", null, "1016 BA", "Amsterdam", null, NLD, false,
            "+31202211333",
            "+312022211399",
            "amsterdam.office@nlbank.example.com"),
/*
    PastaPapaItNl(Organisation_enum.PastaPapaItNl,
            null, null, null, null, null, null, false,
            null,
            null,
            null),
    PerdantFr(Organisation_enum.PerdantFr,
            null, null, null, null, null, null, false,
            null,
            null,
            null),
*/
    PoisonGb(Organisation_enum.PoisonGb,
            "46 Brewster Street", null, "W2D1PQ", "London", null, GBR, false,
            "+44202218888",
            "+44202218899",
            "info@poison-perfumeries.com"),
    PoisonNl(Organisation_enum.PoisonNl,
            "Herengracht 100", null, "1010 AA", "Amsterdam", null, GBR, false,
            "+31202211333",
            "+312022211399",
            "info@poison.example.com"),
/*
    PretGb(Organisation_enum.PretGb,
            null, null, null, null, null, null, false,
            null,
            null,
            null),
*/
    TopModelGb(Organisation_enum.TopModelGb,
            "2 Top Road", null, "W2AXXX", "London", null, GBR, false,
            "+31202211333",
            "+312022211399",
            "info@topmodel.example.com"),
    TopModelGb_2(Organisation_enum.TopModelGb,
            "1 Circle Square", null, "W2AXXX", "London", null, GBR, false,
            null,
            null,
            null),
/*
    YoukeaSe(Organisation_enum.YoukeaSe,
            null, null, null, null, null, null, false,
            null,
            null,
            null),
*/
    ;

    private final Organisation_enum organisation;
    private final String address1;
    private final String address2;
    private final String postalCode;
    private final String city;
    private final String stateReference;
    private final Country_enum country;
    private final Boolean legalAddress;

    private final String phone;
    private final String fax;
    private final String emailAddress;
}
