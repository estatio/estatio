package org.estatio.dom.asset;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class FixedAssetRoleTest_beanProperties extends AbstractBeanPropertiesTest {

    @Test
    public void test() {
        final FixedAssetRole pojo = new FixedAssetRole();
        newPojoTester()
            .withFixture(pojos(FixedAsset.class, FixedAssetForTesting.class))
            .withFixture(pojos(Party.class, PartyForTesting.class))
            .exercise(pojo);
    }

}
