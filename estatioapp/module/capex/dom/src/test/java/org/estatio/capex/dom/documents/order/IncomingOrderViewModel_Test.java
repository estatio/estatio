package org.estatio.capex.dom.documents.order;

import java.math.BigDecimal;

import org.junit.Test;

import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.OwnershipType;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.ownership.FixedAssetOwnership;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingOrderViewModel_Test {

    @Test
    public void minimalRequiredDataToComplete() throws Exception {

        // given
        IncomingOrderViewModel vm = new IncomingOrderViewModel();

        // when
        String result = vm.minimalRequiredDataToComplete();

        // then
        assertThat(result).isEqualTo("order number, buyer, seller, description, net amount, gross amount, charge, period required");

        // and when
        vm.setOrderNumber("123");
        vm.setNetAmount(new BigDecimal("100"));
        result = vm.minimalRequiredDataToComplete();

        // then
        assertThat(result).isEqualTo("buyer, seller, description, gross amount, charge, period required");

        // and when
        vm.setBuyer(new Organisation());
        vm.setSeller(new Organisation());
        vm.setDescription("blah");
        vm.setGrossAmount(BigDecimal.ZERO);
        vm.setCharge(new Charge());
        vm.setPeriod("blah");
        result = vm.minimalRequiredDataToComplete();

        // then
        assertThat(result).isNull();

    }

    @Test
    public void deriveBuyer_works_when_fixedAsset_has_owner(){

        // given
        Party owner = new Organisation();
        FixedAsset asset = new Property();
        FixedAssetOwnership ownership = new FixedAssetOwnership();
        ownership.setOwnershipType(OwnershipType.FULL);
        ownership.setOwner(owner);
        asset.getOwners().add(ownership);

        // when
        IncomingOrderViewModel vm = new IncomingOrderViewModel(null);

        // then
        assertThat(vm.getBuyer()).isEqualTo(owner);
    }

}