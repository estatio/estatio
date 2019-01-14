package org.incode.platform.dom.country.integtests;

import org.incode.module.country.CountryModule;

import org.estatio.module.base.integtests.BaseModuleIntegTestAbstract;

public abstract class CountryModuleIntegTestAbstract extends BaseModuleIntegTestAbstract {

    protected CountryModuleIntegTestAbstract() {
        super(new CountryModule());
    }

}
