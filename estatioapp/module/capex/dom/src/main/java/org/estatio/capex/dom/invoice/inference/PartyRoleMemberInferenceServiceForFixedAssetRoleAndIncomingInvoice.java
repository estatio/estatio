package org.estatio.capex.dom.invoice.inference;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.role.FixedAssetRole;
import org.estatio.dom.asset.role.FixedAssetRoleRepository;
import org.estatio.dom.asset.role.FixedAssetRoleTypeEnum;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.role.PartyRoleMemberInferenceServiceAbstract;

@DomainService(nature = NatureOfService.DOMAIN)
public class PartyRoleMemberInferenceServiceForFixedAssetRoleAndIncomingInvoice
        extends PartyRoleMemberInferenceServiceAbstract<FixedAssetRoleTypeEnum, IncomingInvoice> {

    public PartyRoleMemberInferenceServiceForFixedAssetRoleAndIncomingInvoice() {
        super(IncomingInvoice.class,
                FixedAssetRoleTypeEnum.PROPERTY_MANAGER,
                FixedAssetRoleTypeEnum.ASSET_MANAGER
        );
    }

    @Override
    protected List<Person> doInfer(
            final FixedAssetRoleTypeEnum partyRoleType,
            final IncomingInvoice incomingInvoice) {

        final FixedAsset fixedAsset = inferFixedAsset(incomingInvoice);
        if(fixedAsset == null) {
            // can't go any further
            return null;
        }

        final List<FixedAssetRole> fixedAssetRoles =
                fixedAssetRoleRepository.findByAssetAndType(fixedAsset, partyRoleType);

        return fixedAssetRoles.stream()
                .map(FixedAssetRole::getParty)
                .filter(Person.class::isInstance)
                .map(Person.class::cast)
                .collect(Collectors.toList());
    }

    private FixedAsset inferFixedAsset(final IncomingInvoice incomingInvoice) {

        final Collection<IncomingInvoiceItem> items =
                incomingInvoice.getItems()
                        .stream()
                        .filter(IncomingInvoiceItem.class::isInstance)
                        .map(IncomingInvoiceItem.class::cast)
                        .collect(Collectors.toList());

        for (IncomingInvoiceItem item : items) {
            return item.getFixedAsset();
        }
        return null;
    }


    @Inject
    FixedAssetRoleRepository fixedAssetRoleRepository;

}
