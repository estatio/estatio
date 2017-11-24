package org.estatio.module.application.spiimpl.togglz;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.settings.dom.ApplicationSetting;
import org.isisaddons.module.settings.dom.ApplicationSettingsServiceRW;
import org.isisaddons.module.togglz.glue.spi.FeatureState;
import org.isisaddons.module.togglz.glue.spi.FeatureStateRepository;

/**
 * Implements the togglz SPI and delegates to estatio's own wrapper around the settings service.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class FeatureStateRepositoryForApplicationSettings implements FeatureStateRepository {

    @Override
    public FeatureState find(final String key) {
        final ApplicationSetting applicationSetting = applicationSettingsService.find(key);
        return FeatureStateForEstatio.from(applicationSetting);
    }

    @Override
    public FeatureState create(final String key) {
        final ApplicationSetting applicationSetting = applicationSettingsService.newString(key, "", "");
        return FeatureStateForEstatio.from(applicationSetting);
    }

    @Inject
    ApplicationSettingsServiceRW applicationSettingsService;

}

