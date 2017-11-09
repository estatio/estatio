package org.estatio.module.application.spiimpl.togglz;

import org.isisaddons.module.togglz.glue.spi.TogglzModuleFeatureManagerProviderAbstract;

import org.estatio.module.base.spiimpl.togglz.EstatioTogglzFeature;

/**
 * Registered in META-INF/services, as per http://www.togglz.org/documentation/advanced-config.html
 */
public class TogglzModuleFeatureManagerProviderForEstatio extends TogglzModuleFeatureManagerProviderAbstract {
    public TogglzModuleFeatureManagerProviderForEstatio() {
        super(EstatioTogglzFeature.class);
    }
}

