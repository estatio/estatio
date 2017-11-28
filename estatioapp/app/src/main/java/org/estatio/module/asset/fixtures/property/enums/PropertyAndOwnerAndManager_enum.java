package org.estatio.module.asset.fixtures.property.enums;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import lombok.Getter;
import lombok.experimental.Accessors;

//@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum PropertyAndOwnerAndManager_enum {

    BudNl   (Property_enum.BudNl, Organisation_enum.AcmeNl, Person_enum.JohnDoeNl),
    CARTEST (Property_enum.CARTEST, Organisation_enum.HelloWorldIt, Person_enum.LucianoPavarottiIt),
    GraIt   (Property_enum.GraIt, Organisation_enum.HelloWorldIt, Person_enum.LucianoPavarottiIt),
    HanSe   (Property_enum.HanSe, Organisation_enum.HelloWorldSe, Person_enum.AgnethaFaltskogSe),
    KalNl   (Property_enum.KalNl, Organisation_enum.AcmeNl, Person_enum.JohnDoeNl),
    MacFr   (Property_enum.MacFr, Organisation_enum.HelloWorldFr, Person_enum.JeanneDarcFr),
    MnsFr   (Property_enum.MnsFr, Organisation_enum.HelloWorldFr, Person_enum.FleuretteRenaudFr),
    OxfGb   (Property_enum.OxfGb, Organisation_enum.HelloWorldGb, Person_enum.GinoVannelliGb),
    VivFr   (Property_enum.VivFr, Organisation_enum.HelloWorldFr, Person_enum.JeanneDarcFr);

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
