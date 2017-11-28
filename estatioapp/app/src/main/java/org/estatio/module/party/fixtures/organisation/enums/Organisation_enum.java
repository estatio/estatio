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

import org.isisaddons.module.base.platform.fixturesupport.DataEnum;

import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.fixtures.organisation.builders.OrganisationAndCommsBuilder;

import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum.Fr;
import static org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum.Gb;
import static org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum.It;
import static org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum.Nl;
import static org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum.Se;

@Getter
@Accessors(chain = true)
public enum Organisation_enum implements DataEnum<Organisation, OrganisationAndCommsBuilder> {

    AcmeNl          ("ACME_NL", "ACME Properties International", Nl,
            new OrganisationComms_enum[] { OrganisationComms_enum.AcmeNl }),

    DagoBankGb      ("DAGOBANK_GB", "DagoBank (GB)", Gb,
            new OrganisationComms_enum[] { OrganisationComms_enum.DagoBankGb }),

    DagoBankNl      ("DAGOBANK_NL", "DagoBank (NL)", Nl,
            new OrganisationComms_enum[] { OrganisationComms_enum.DagoBankNl}),

    HelloWorldFr    ("HELLOWORLD_FR", "Hello World Properties (France)", Fr),

    HelloWorldGb    ("HELLOWORLD_GB", "Hello World Properties", Gb,
                    new OrganisationComms_enum[] {
                        OrganisationComms_enum.HelloWorldGb,
                        OrganisationComms_enum.HelloWorldGb_2
                    }),

    HelloWorldIt    ("HELLOWORLD_IT", "Hello World Properties (Italy)", It),

    HelloWorldNl    ("HELLOWORLD_NL", "Hello World Properties (NL)", Nl),

    HelloWorldSe    ("HELLOWORLD_SE", "Hello World Properties (Sweden)", Se),

    HyperNl         ("HYPER_NL", "Hypermarkt (NL)", Nl,
                    new OrganisationComms_enum[] { OrganisationComms_enum.HyperNl}),

    MediaXGb        ("MEDIAX_GB", "Media Electronics (GB)", Gb,
                    new OrganisationComms_enum[] { OrganisationComms_enum.MediaXGb}),

    MediaXNl        ("MEDIAX_NL", "Mediax Electronics (NL)", Nl,
                    new OrganisationComms_enum[] { OrganisationComms_enum.MediaXNl}),

    MiracleGb       ("MIRACLE_GB", "Miracle Shoes", Gb),

    MiracleNl       ("MIRACLE_NL", "Miracle Shoes (NL)", Nl),

    NlBankNl        ("NLBANK_NL", "NlBank (NL)", Nl,
                    new OrganisationComms_enum[] { OrganisationComms_enum.NlBankNl}),

    PastaPapaItNl   ("PASTAPAPA", "Pasta Papa Food", It),

    PerdantFr       ("PERDANT", "Perdant Clothing", Fr),

    PoisonGb        ("POISON_GB", "Poison Perfumeries", Gb,
                    new OrganisationComms_enum[] { OrganisationComms_enum.PoisonGb}),

    PoisonNl        ("POISON_NL", "Poison Perfumeries B.V.", Nl,
                    new OrganisationComms_enum[] { OrganisationComms_enum.PoisonNl}),

    PretGb          ("PRET", "Pret-a-Partir", Gb),

    TopModelGb      ("TOPMODEL", "Topmodel Fashion", Gb,
                    new OrganisationComms_enum[] {
                        OrganisationComms_enum.TopModelGb,
                        OrganisationComms_enum.TopModelGb_2
                    }),

    YoukeaSe        ("YOUKEA", "Youkea Furniture", Se),

    ;

    private final String ref;
    private final String name;
    private final ApplicationTenancy_enum applicationTenancy;
    private final OrganisationComms_enum[] comms;

    Organisation_enum(
            final String ref,
            final String name,
            final ApplicationTenancy_enum applicationTenancy) {
        this(ref, name, applicationTenancy, new OrganisationComms_enum[0]);
    }

    Organisation_enum(
            final String ref,
            final String name,
            final ApplicationTenancy_enum applicationTenancy,
            final OrganisationComms_enum[] comms) {
        this.ref = ref;
        this.name = name;
        this.applicationTenancy = applicationTenancy;
        this.comms = comms;
    }

    @Override
    public OrganisationAndCommsBuilder toFixtureScript() {
        final OrganisationAndCommsBuilder organisationAndCommsBuilder = new OrganisationAndCommsBuilder();

        return organisationAndCommsBuilder
                .setAtPath(getApplicationTenancy().getPath())
                .setPartyName(getName())
                .setPartyReference(getRef())
                .setComms(getComms());
    }

}
