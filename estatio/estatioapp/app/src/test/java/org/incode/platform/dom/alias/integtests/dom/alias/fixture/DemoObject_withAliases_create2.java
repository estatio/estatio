package org.incode.platform.dom.alias.integtests.dom.alias.fixture;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.alias.dom.impl.T_addAlias;
import org.incode.platform.dom.alias.integtests.demo.dom.demo.DemoObject;
import org.incode.platform.dom.alias.integtests.demo.fixture.data.DemoObjectData;
import org.incode.platform.dom.alias.integtests.dom.alias.dom.AliasForDemoObject;
import org.incode.platform.dom.alias.integtests.dom.alias.dom.spiimpl.aliastype.AliasTypeDemoEnum;

public class DemoObject_withAliases_create2 extends FixtureScript {

    T_addAlias mixinAddAlias(final Object aliased) {
        return factoryService.mixin(AliasForDemoObject._addAlias.class, aliased);
    }

    @Override
    protected void execute(final ExecutionContext executionContext) {

        final DemoObject foo = DemoObjectData.Foo.findUsing(serviceRegistry);
        final DemoObject bar = DemoObjectData.Bar.findUsing(serviceRegistry);

        wrap(mixinAddAlias(foo)).$$("/uk", AliasTypeDemoEnum.GENERAL_LEDGER, "12345");
        wrap(mixinAddAlias(foo)).$$("/uk", AliasTypeDemoEnum.DOCUMENT_MANAGEMENT, "http://docserver.mycompany/url/12345");
        wrap(mixinAddAlias(foo)).$$("/uk", AliasTypeDemoEnum.PERSONNEL_SYSTEM, "12345");

        wrap(mixinAddAlias(bar)).$$("/uk", AliasTypeDemoEnum.GENERAL_LEDGER, "98765");
        wrap(mixinAddAlias(bar)).$$("/nl", AliasTypeDemoEnum.GENERAL_LEDGER, "12345");
        wrap(mixinAddAlias(foo)).$$("/nl", AliasTypeDemoEnum.DOCUMENT_MANAGEMENT, "http://docserver.mycompany/url/12345");
    }


}
