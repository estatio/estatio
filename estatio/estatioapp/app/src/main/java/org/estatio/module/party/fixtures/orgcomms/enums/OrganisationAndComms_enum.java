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
package org.estatio.module.party.fixtures.orgcomms.enums;

import java.util.Arrays;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;
import org.estatio.module.party.fixtures.orgcomms.builders.OrganisationAndCommsBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.country.fixtures.enums.Country_enum.BEL;
import static org.incode.module.country.fixtures.enums.Country_enum.FRA;
import static org.incode.module.country.fixtures.enums.Country_enum.GBR;
import static org.incode.module.country.fixtures.enums.Country_enum.ITA;
import static org.incode.module.country.fixtures.enums.Country_enum.NLD;
import static org.incode.module.country.fixtures.enums.Country_enum.SWE;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum OrganisationAndComms_enum
        implements PersonaWithBuilderScript<Organisation, OrganisationAndCommsBuilder>,
        PersonaWithFinder<Organisation> {

    AcmeNl(Organisation_enum.AcmeNl,
        new OrganisationAndCommsBuilder.CommsSpec[] {
            new OrganisationAndCommsBuilder.CommsSpec(
                    "Herengracht 100", null, "1010 AA", "Amsterdam", null, NLD, true,
                    "+31202211333",
                    "+312022211399",
                    "info@acme.example.com")
    }),
    DagoBankGb(Organisation_enum.DagoBankGb,
        new OrganisationAndCommsBuilder.CommsSpec[] {
            new OrganisationAndCommsBuilder.CommsSpec(
                    null, null, null, null, null, null, true,
                    null,
                    null,
                    "london.office@dagobank.example.com")
    }),
    DagoBankNl(Organisation_enum.DagoBankNl,
        new OrganisationAndCommsBuilder.CommsSpec[] {
            new OrganisationAndCommsBuilder.CommsSpec("Herengracht 333", null, "1016 BA", "Amsterdam", null, NLD, true,
                "+31202211333",
                "+312022211399",
                "amsterdam.office@dagobank.example.com")
    }),
    HelloWorldGb(Organisation_enum.HelloWorldGb,
        new OrganisationAndCommsBuilder.CommsSpec[] {
            new OrganisationAndCommsBuilder.CommsSpec("5 Covent Garden", null, "W1A1AA", "London", null, GBR, true,
                    "+44202211333",
                    "+442022211399",
                    "info@hello.example.com"),
            new OrganisationAndCommsBuilder.CommsSpec("1 Circle Square", null, "W2AXXX", "London", null, GBR, true,
                    null,
                    null,
                    null)
    }),
    HelloWorldBe(Organisation_enum.HelloWorldBe,
            new OrganisationAndCommsBuilder.CommsSpec[] {
                    new OrganisationAndCommsBuilder.CommsSpec("5 Covent Garden", null, "W1A1AA", "Brussels", null, BEL, true,
                            "+44202211333",
                            "+442022211399",
                            "info@hello.example.com"),
                    new OrganisationAndCommsBuilder.CommsSpec("1 Circle Square", null, "W2AXXX", "Brussels", null, BEL, true,
                            null,
                            null,
                            null)
            }),
    HelloWorldFr(Organisation_enum.HelloWorldFr,
            new OrganisationAndCommsBuilder.CommsSpec[] {
                    new OrganisationAndCommsBuilder.CommsSpec("5 Jardin du Convent", null, "W1A1AA", "Paris", null, FRA, true,
                            "+33202211333",
                            "+332022211399",
                            "info@hello.example.com"),
                    new OrganisationAndCommsBuilder.CommsSpec("1 Place Ronde", null, "W2AXXX", "Paris", null, FRA, true,
                            null,
                            null,
                            null)
    }),
    HyperNl(Organisation_enum.HyperNl,
        new OrganisationAndCommsBuilder.CommsSpec[] {
            new OrganisationAndCommsBuilder.CommsSpec(
                    "Javaplein", null, "1016 BA", "Amsterdam", null, NLD, true,
                    "+31202211333",
                    "+312022211399",
                    null)
    }),
    MediaXGb(Organisation_enum.MediaXGb,
        new OrganisationAndCommsBuilder.CommsSpec[] {
            new OrganisationAndCommsBuilder.CommsSpec(
                    "85 High St", null, "EN11 8TL", "Hoddesdon", null, GBR, true,
                    "+442079897676",
                    "+442079897677",
                    "info@mediax.example.com")
    }),
    MediaXNl(Organisation_enum.MediaXNl,
        new OrganisationAndCommsBuilder.CommsSpec[] {
            new OrganisationAndCommsBuilder.CommsSpec(
                    "Herengracht 100", null, "1010 AA", "Amsterdam", null, GBR, true,
                    "+31202211333",
                    "+312022211399",
                    "info@mediax.example.com")
    }),
    NlBankNl(Organisation_enum.NlBankNl,
        new OrganisationAndCommsBuilder.CommsSpec[] {
            new OrganisationAndCommsBuilder.CommsSpec(
                    "Single 2222", null, "1016 BA", "Amsterdam", null, NLD, true,
                    "+31202211333",
                    "+312022211399",
                    "amsterdam.office@nlbank.example.com")
    }),
    PoisonGb(Organisation_enum.PoisonGb,
        new OrganisationAndCommsBuilder.CommsSpec[] {
            new OrganisationAndCommsBuilder.CommsSpec(
                    "46 Brewster Street", null, "W2D1PQ", "London", null, GBR, true,
                    "+44202218888",
                    "+44202218899",
                    "info@poison-perfumeries.com")
    }),
    PoisonNl(Organisation_enum.PoisonNl,
        new OrganisationAndCommsBuilder.CommsSpec[] {
            new OrganisationAndCommsBuilder.CommsSpec(
                    "Herengracht 100", null, "1010 AA", "Amsterdam", null, GBR, true,
                    "+31202211333",
                    "+312022211399",
                    "info@poison.example.com")
    }),
    TopModelGb(Organisation_enum.TopModelGb,
        new OrganisationAndCommsBuilder.CommsSpec[] {
            new OrganisationAndCommsBuilder.CommsSpec(
                    "2 Top Road", null, "W2AXXX", "London", null, GBR, true,
                    "+31202211333",
                    "+312022211399",
                    "info@topmodel.example.com"),
            new OrganisationAndCommsBuilder.CommsSpec(
                    "1 Circle Square", null, "W2AXXX", "London", null, GBR, true,
                    null,
                    null,
                    null)
    }),
    TopModelFr(Organisation_enum.TopModelFr,
            new OrganisationAndCommsBuilder.CommsSpec[] {
                    new OrganisationAndCommsBuilder.CommsSpec(
                            "2 Top Road", null, "W2AXXX", "London", null, FRA, true,
                            "+31202211333",
                            "+312022211399",
                            "info@topmodel.example.com"),
                    new OrganisationAndCommsBuilder.CommsSpec(
                            "1 Circle Square", null, "W2AXXX", "London", null, FRA, true,
                            null,
                            null,
                            null)
            }),
    HelloWorldSe(Organisation_enum.HelloWorldSe,
            new OrganisationAndCommsBuilder.CommsSpec[] {
                    new OrganisationAndCommsBuilder.CommsSpec(
                            "SomeGatan 11", null, "111 22", "Stockholm", null, SWE, true,
                            "+46082211333",
                            "+460822211399",
                            "info@helloworldse.example.com")
            }),
    HelloWorldIt(Organisation_enum.HelloWorldIt,
            new OrganisationAndCommsBuilder.CommsSpec[] {
                    new OrganisationAndCommsBuilder.CommsSpec("Via Melchiorre Gioia 100", null, "20124", "Milan", null, ITA, true,
                            "+39202211333",
                            "+392022211399",
                            "info@helloita.example.com")
            }),
    ;

    private final Organisation_enum organisation_d;
    private final OrganisationAndCommsBuilder.CommsSpec[] comms;

    @Override
    public OrganisationAndCommsBuilder builder() {
        return new OrganisationAndCommsBuilder()
                .setPrereq((f,ec) -> f.setOrganisation(f.objectFor(organisation_d,ec)))
                .setComms(Arrays.asList(getComms()));
    }

    @Override
    public Organisation findUsing(final ServiceRegistry2 serviceRegistry) {
        return organisation_d.findUsing(serviceRegistry);
    }

}
