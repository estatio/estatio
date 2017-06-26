package org.estatio.capex.dom.invoice.inference;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.dom.tenancy.HasAtPath;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.dom.party.PartyRoleTypeEnum;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonRepository;
import org.estatio.dom.party.role.PartyRoleMemberInferenceServiceAbstract;

/**
 * Identical to {@link PartyRoleMemberInferenceServiceForPartyRoleAndHasAtPath}, however {@link Document} doesn't
 * implement {@link HasAtPath}.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class PartyRoleMemberInferenceServiceForPartyRoleAndDocument
        extends PartyRoleMemberInferenceServiceAbstract<PartyRoleTypeEnum, Document> {

    public PartyRoleMemberInferenceServiceForPartyRoleAndDocument() {
        super(Document.class,
                PartyRoleTypeEnum.OFFICE_ADMINISTRATOR,
                PartyRoleTypeEnum.LEGAL_MANAGER,
                PartyRoleTypeEnum.COUNTRY_DIRECTOR,
                PartyRoleTypeEnum.TREASURER
                );
    }

    protected final List<Person> doInfer(
            final PartyRoleTypeEnum partyRoleType,
            final Document document) {

        // infer the country / "org unit" from the document
        String atPath = document.getAtPath();

        return personRepository.findByRoleTypeAndAtPath(partyRoleType, atPath);
    }


    @Inject
    PersonRepository personRepository;

}
