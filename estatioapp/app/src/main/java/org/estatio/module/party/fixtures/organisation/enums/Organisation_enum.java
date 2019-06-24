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

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;

import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.builders.OrganisationBuilder;
import org.estatio.module.party.fixtures.orgcomms.builders.OrganisationAndCommsBuilder;

import lombok.Getter;
import lombok.experimental.Accessors;
import static org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum.Fr;
import static org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum.Gb;
import static org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum.It;
import static org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum.Nl;
import static org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum.Se;

@Getter
@Accessors(chain = true)
public enum Organisation_enum
        implements PersonaWithBuilderScript<Organisation, OrganisationBuilder>,
        PersonaWithFinder<Organisation> {

    AcmeNl("ACME_NL", "ACME Properties International", null, Nl),
    DagoBankGb("DAGOBANK_GB", "DagoBank (GB)", null, Gb),
    DagoBankNl("DAGOBANK_NL", "DagoBank (NL)", null, Nl),
    HelloWorldFr("HELLOWORLD_FR", "Hello World Properties (France)", "HELLOWORLD code", Fr),
    HelloWorldGb("HELLOWORLD_GB", "Hello World Properties", null, Gb),
    HelloWorldIt("HW_IT", "Hello World Properties (Italy)", null, It),
    HelloWorldIt01("IT01", "Hello World Properties IT01 (Italy)", null, It),
    HelloWorldNl("HELLOWORLD_NL", "Hello World Properties (NL)", null, Nl),
    HelloWorldSe("HELLOWORLD_SE", "Hello World Properties (Sweden)", null, Se),
    HyperNl("HYPER_NL", "Hypermarkt (NL)", null, Nl),
    MediaXGb("MEDIAX_GB", "Media Electronics (GB)", null, Gb),
    MediaXNl("MEDIAX_NL", "Mediax Electronics (NL)", null, Nl),
    MiracleGb("MIRACLE_GB", "Miracle Shoes", null, Gb),
    MiracleNl("MIRACLE_NL", "Miracle Shoes (NL)", null, Nl),
    NlBankNl("NLBANK_NL", "NlBank (NL)", null, Nl),
    PastaPapaItNl("PASTAPAPA", "Pasta Papa Food", null, It),
    TopModelIt("TOPMODEL_IT", "Topmodel Fashion", null, It),
    PerdantFr("PERDANT", "Perdant Clothing", "PERDANT code", Fr),
    TopModelFr("TOPMODEL_FR", "Topmodel Fashion", "TOPMODEL code", Fr),
    TopSellerFr("TOPSELLER_FR", "Topseller goods", "TOPSELLER code", Fr),
    PoisonGb("POISON_GB", "Poison Perfumeries", null, Gb),
    PoisonNl("POISON_NL", "Poison Perfumeries B.V.", null, Nl),
    PretGb("PRET", "Pret-a-Partir", null, Gb),
    TopModelGb("TOPMODEL", "Topmodel Fashion", null, Gb),
    TopSellerGb("TOPSELLER", "Topseller goods", null, Gb),
    YoukeaSe("YOUKEA", "Youkea Furniture", null, Se),
    PoisonSe("POISON_SE", "Poison Perfumeries", null, Se),
    TopModelSe("TOPMODEL_SE", "Topmodel Fashion", null, Se),
    OmsHyraSe("OMSHYRA", "Omshyra rentals", null, Se),
    IncomingBuyerIt("IT01", "Buyer Organisation", null, It),
    ;

    private final String ref;
    private final String name;
    private final String chamberOfCommerceCode;
    private final ApplicationTenancy_enum applicationTenancy;

    Organisation_enum(
            final String ref,
            final String name,
            final String chamberOfCommerceCode,
            final ApplicationTenancy_enum applicationTenancy) {
        this(ref, name, chamberOfCommerceCode, applicationTenancy, new OrganisationAndCommsBuilder.CommsSpec[0]);
    }

    Organisation_enum(
            final String ref,
            final String name,
            final String chamberOfCommerceCode,
            final ApplicationTenancy_enum applicationTenancy,
            final OrganisationAndCommsBuilder.CommsSpec[] comms) {
        this.ref = ref;
        this.name = name;
        this.chamberOfCommerceCode = chamberOfCommerceCode;
        this.applicationTenancy = applicationTenancy;
    }

    @Override
    public OrganisationBuilder builder() {
        return new OrganisationBuilder()
                .setAtPath(getApplicationTenancy().getPath())
                .setName(getName())
                .setChamberOfCommerceCode(getChamberOfCommerceCode())
                .setReference(getRef());
    }

    @Override
    public Organisation findUsing(final ServiceRegistry2 serviceRegistry) {
        final PartyRepository partyRepository = serviceRegistry
                .lookupService(PartyRepository.class);
        final Party party = partyRepository.findPartyByReference(ref);
        return (Organisation) party;
    }

}
