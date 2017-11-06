package org.estatio.capex.dom.invoice.inference;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.role.PartyRoleMemberInferenceServiceAbstract;

@DomainService(nature = NatureOfService.DOMAIN)
public class PartyRoleMemberInferenceServiceForPartyRoleAndDocument
        extends PartyRoleMemberInferenceServiceAbstract<PartyRoleTypeEnum, Document> {

    public PartyRoleMemberInferenceServiceForPartyRoleAndDocument() {
        super(Document.class, PartyRoleTypeEnum.class);
    }

    protected final List<Person> doInferMembersOf(
            final PartyRoleTypeEnum partyRoleType,
            final Document document) {

        // infer the country / "org unit" from the document
        String atPath = document.getAtPath();

        return personRepository.findByRoleTypeAndAtPath(partyRoleType, atPath);
    }

    @Override
    protected List<Person> doInferMembersOf(final PartyRoleTypeEnum partyRoleType) {
        return personRepository.findByRoleType(partyRoleType);
    }

    @Inject
    PersonRepository personRepository;

}
