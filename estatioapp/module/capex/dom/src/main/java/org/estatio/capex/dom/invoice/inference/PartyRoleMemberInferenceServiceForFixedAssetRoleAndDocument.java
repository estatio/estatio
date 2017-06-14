package org.estatio.capex.dom.invoice.inference;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.role.FixedAssetRole;
import org.estatio.dom.asset.role.FixedAssetRoleRepository;
import org.estatio.dom.asset.role.FixedAssetRoleTypeEnum;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.role.PartyRoleMemberInferenceServiceAbstract;

public class PartyRoleMemberInferenceServiceForFixedAssetRoleAndDocument
        extends PartyRoleMemberInferenceServiceAbstract<FixedAssetRoleTypeEnum, Document> {

    public PartyRoleMemberInferenceServiceForFixedAssetRoleAndDocument() {
        super(Document.class, FixedAssetRoleTypeEnum.PROPERTY_MANAGER);
    }

    @Override
    protected List<Person> doInfer(
            final FixedAssetRoleTypeEnum partyRoleType,
            final Document document) {

        final FixedAsset fixedAsset = inferFixedAsset(document);
        if(fixedAsset == null) {
            // can't go any further
            return null;
        }

        List<FixedAssetRole> fixedAssetRoles =
                fixedAssetRoleRepository.findByAssetAndType(fixedAsset, partyRoleType);
        return fixedAssetRoles.stream()
                .map(FixedAssetRole::getParty)
                .filter(Person.class::isInstance)
                .map(Person.class::cast)
                .collect(Collectors.toList());
    }

    private FixedAsset inferFixedAsset(final Document document) {

        List<Paperclip> paperclips = paperclipRepository.findByDocument(document);
        for (Paperclip paperclip : paperclips) {
            Object attachedTo = paperclip.getAttachedTo();
            if(attachedTo instanceof FixedAsset) {
                return (FixedAsset) attachedTo;
            }
        }

        return null;
    }


    @Inject
    FixedAssetRoleRepository fixedAssetRoleRepository;

    @Inject
    PaperclipRepository paperclipRepository;

}
