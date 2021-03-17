package org.estatio.module.capex.dom.order.inference;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRoleMemberInferenceServiceAbstract;

@DomainService(nature = NatureOfService.DOMAIN)
public class PartyRoleMemberInferenceServiceForFixedAssetRoleAndOrder
        extends PartyRoleMemberInferenceServiceAbstract<FixedAssetRoleTypeEnum, Order> {

    public PartyRoleMemberInferenceServiceForFixedAssetRoleAndOrder() {
        super(Order.class, FixedAssetRoleTypeEnum.class);
    }

    @Override
    protected List<Person> doInferMembersOf(
            final FixedAssetRoleTypeEnum partyRoleType,
            final Order order) {

        final FixedAsset fixedAsset = order.getProperty();
        if(fixedAsset == null) {
            // can't go any further
            return null;
        }

        final List<FixedAssetRole> fixedAssetRoles =
                fixedAssetRoleRepository.findByAssetAndType(fixedAsset, partyRoleType);

        return currentPersonsFrom(fixedAssetRoles);
    }

    @Override
    public List<Person> doInferMembersOf(final FixedAssetRoleTypeEnum partyRoleType) {
        final List<FixedAssetRole> fixedAssetRoles =
                fixedAssetRoleRepository.findByType(partyRoleType);

        return currentPersonsFrom(fixedAssetRoles);
    }

    private List<Person> currentPersonsFrom(final List<FixedAssetRole> fixedAssetRoles) {
        return fixedAssetRoles.stream()
                .filter(FixedAssetRole::isCurrent)
                .map(FixedAssetRole::getParty)
                .filter(Person.class::isInstance)
                .map(Person.class::cast)
                .collect(Collectors.toList());
    }

    @Inject
    FixedAssetRoleRepository fixedAssetRoleRepository;

}
