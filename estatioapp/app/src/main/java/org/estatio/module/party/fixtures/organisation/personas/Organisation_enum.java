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
package org.estatio.module.party.fixtures.organisation.personas;

import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum.Fr;
import static org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum.Gb;
import static org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum.It;
import static org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum.Nl;
import static org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum.Se;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Organisation_enum  {

    AcmeNl("ACME_NL", "ACME Properties International", Nl),
    DagoBankGb("DAGOBANK_GB", "DagoBank (GB)", Gb),
    DagoBankNl("DAGOBANK_NL", "DagoBank (NL)", Nl),
    HelloWorldFr("HELLOWORLD_FR", "Hello World Properties (France)", Fr),
    HelloWorldGb("HELLOWORLD_GB", "Hello World Properties", Gb),
    HelloWorldIt("HELLOWORLD_IT", "Hello World Properties (Italy)", It),
    HelloWorldNl("HELLOWORLD_NL", "Hello World Properties (NL)", Nl),
    HelloWorldSe("HELLOWORLD_SE", "Hello World Properties (Sweden)", Se),
    HyperNl("HYPER_NL", "Hypermarkt (NL)", Nl),
    MediaXGb("MEDIAX_GB", "Media Electronics (GB)", Gb),
    MediaXNl("MEDIAX_NL", "Mediax Electronics (NL)", Nl),
    MiracleGb("MIRACLE_GB", "Miracle Shoes", Gb),
    MiracleNl("MIRACLE_NL", "Miracle Shoes (NL)", Nl),
    NlBankNl("NLBANK_NL", "NlBank (NL)", Nl),
    PastaPapaItNl("PASTAPAPA", "Pasta Papa Food", It),
    PerdantFr("PERDANT", "Perdant Clothing", Fr),
    PoisonGb("POISON_GB", "Poison Perfumeries", Gb),
    PoisonNl("POISON_NL", "Poison Perfumeries B.V.", Nl),
    PretGb("PRET", "Pret-a-Partir", Gb),
    TopModelGb("TOPMODEL", "Topmodel Fashion", Gb),
    YoukeaSe("YOUKEA", "Youkea Furniture", Se),
    ;

    String ref;
    String name;
    ApplicationTenancy_enum applicationTenancy;

}
