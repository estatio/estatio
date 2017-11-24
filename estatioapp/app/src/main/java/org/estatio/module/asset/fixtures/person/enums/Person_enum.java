package org.estatio.module.asset.fixtures.person.enums;

import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.party.fixtures.organisation.personas.Organisation_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum.*;
import static org.estatio.module.party.fixtures.organisation.personas.Organisation_enum.*;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum Person_enum {
    AgnethaFaltskogSe               ("AFALTSKOG", Se, YoukeaSe),
    BrunoTreasurerFr                ("BJEREMIE", Fr, null),
    DylanOfficeAdministratorGb      ("DCLAYTON", Gb, null),
    EmmaTreasurerGb                 ("EFARMER", Gb, null),
    FaithConwayGb                   ("FCONWAY", Gb, null ),
    FifineLacroixFr                 ("FLACROIX", Fr, null),
    FleuretteRenaudFr               ("FRENAUD", Fr, null),
    FloellaAssetManagerGb           ("FBEAUTIFUL", Gb, null),
    GabrielHerveFr                  ("GHERVE", Fr, null),
    GinoVannelliGb                  ("GVANNELLI", Gb, TopModelGb),
    JeanneDarcFr                    ("JDARC", Fr, PerdantFr),
    JohnDoeNl                       ("JDOE", Nl, null),
    JohnSmithGb                     ("JSMTH", Gb, null),
    JonathanPropertyManagerGb       ("JRICE", Gb, null),
    LinusTorvaldsNl                 ("LTORVALDS", Nl, null),
    LucianoPavarottiIt              ("LPAVAROTTI", It, PastaPapaItNl),
    OlivePropertyManagerFr          ("OBEAUSOLIEL", Fr, null),
    OscarCountryDirectorGb          ("OPRITCHARD", Gb, null),
    RosaireEvrardFr                 ("REVRARD", Fr, null),
    ThibaultOfficerAdministratorFr  ("TJOSUE", Fr, null),
    ;

    private String ref;
    private ApplicationTenancy_enum applicationTenancy;
    private Organisation_enum partyFrom;
}
