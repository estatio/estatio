package org.estatio.module.asset.fixtures.property.enums;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForAgnethaFaltskogSe;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForFleuretteRenaudFr;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForGinoVannelliGb;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForJeanneDarcFr;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForJohnDoeNl;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForLucianoPavarottiIt;
import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForAcmeNl;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldFr;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldGb;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldIt;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldSe;
import org.estatio.module.party.fixtures.organisation.personas.Organisation_enum;

import lombok.Getter;
import lombok.experimental.Accessors;

//@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum PropertyAndOwnerAndManager_enum {

    BudNl   (Property_enum.BudNl, OrganisationForAcmeNl.data, PersonAndRolesForJohnDoeNl.data),
    CARTEST (Property_enum.CARTEST, OrganisationForHelloWorldIt.data, PersonAndRolesForLucianoPavarottiIt.data),
    GraIt   (Property_enum.GraIt, OrganisationForHelloWorldIt.data, PersonAndRolesForLucianoPavarottiIt.data),
    HanSe   (Property_enum.HanSe, OrganisationForHelloWorldSe.data, PersonAndRolesForAgnethaFaltskogSe.data),
    KalNl   (Property_enum.KalNl, OrganisationForAcmeNl.data, PersonAndRolesForJohnDoeNl.data),
    MacFr   (Property_enum.MacFr, OrganisationForHelloWorldFr.data, PersonAndRolesForJeanneDarcFr.data),
    MnsFr   (Property_enum.MnsFr, OrganisationForHelloWorldFr.data, PersonAndRolesForFleuretteRenaudFr.data),
    OxfGb   (Property_enum.OxfGb, OrganisationForHelloWorldGb.data, PersonAndRolesForGinoVannelliGb.data),
    VivFr   (Property_enum.VivFr, OrganisationForHelloWorldFr.data, PersonAndRolesForJeanneDarcFr.data);

    private final Property_enum property;
    public String getRef() { return property.getRef(); }
    public ApplicationTenancy_enum getApplicationTenancy() { return property.getApplicationTenancy(); }
    private final Organisation_enum owner;
    private final Person_enum manager;

    PropertyAndOwnerAndManager_enum(
            final Property_enum property,
            final Organisation_enum owner, final Person_enum manager) {
        this.property = property;
        this.owner = owner;
        this.manager = manager;
    }
}
