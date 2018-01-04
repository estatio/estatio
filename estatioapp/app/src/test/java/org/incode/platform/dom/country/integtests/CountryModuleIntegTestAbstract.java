package org.incode.platform.dom.country.integtests;

import org.apache.isis.core.integtestsupport.IntegrationTestAbstract3;

import org.incode.module.country.CountryModule;

public abstract class CountryModuleIntegTestAbstract extends IntegrationTestAbstract3 {

    protected CountryModuleIntegTestAbstract() {
        super(new CountryModule());
    }

}
