package org.estatio.module.asset.fixtures.property.enums;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForAgnethaFaltskogSe;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForFleuretteRenaudFr;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForGinoVannelliGb;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForJeanneDarcFr;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForJohnDoeNl;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForLucianoPavarottiIt;
import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForFr;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForGb;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForIt;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForNl;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForSe;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForAcmeNl;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldFr;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldGb;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldIt;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldSe;
import org.estatio.module.party.fixtures.organisation.personas.Organisation_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Property_enum {

    BudNl   ("BUD", ApplicationTenancyForNl.data, OrganisationForAcmeNl.data, PersonAndRolesForJohnDoeNl.data),
    CARTEST ("CAR", ApplicationTenancyForIt.data, OrganisationForHelloWorldIt.data, PersonAndRolesForLucianoPavarottiIt.data),
    GraIt   ("GRA", ApplicationTenancyForIt.data, OrganisationForHelloWorldIt.data, PersonAndRolesForLucianoPavarottiIt.data),
    HanSe   ("HAN", ApplicationTenancyForSe.data, OrganisationForHelloWorldSe.data, PersonAndRolesForAgnethaFaltskogSe.data),
    KalNl   ("KAL", ApplicationTenancyForNl.data, OrganisationForAcmeNl.data, PersonAndRolesForJohnDoeNl.data),
    MacFr   ("MAC", ApplicationTenancyForFr.data, OrganisationForHelloWorldFr.data, PersonAndRolesForJeanneDarcFr.data),
    MnsFr   ("MNS", ApplicationTenancyForFr.data, OrganisationForHelloWorldFr.data, PersonAndRolesForFleuretteRenaudFr.data),
    OxfGb   ("OXF", ApplicationTenancyForGb.data, OrganisationForHelloWorldGb.data, PersonAndRolesForGinoVannelliGb.data),
    VivFr   ("VIV", ApplicationTenancyForFr.data, OrganisationForHelloWorldFr.data, PersonAndRolesForJeanneDarcFr.data);

    private final String ref;
    private final ApplicationTenancy_enum applicationTenancy;
    private final Organisation_enum owner;
    private final Person_enum manager;

}
