package org.estatio.app.services.togglz;

import org.isisaddons.module.settings.dom.ApplicationSetting;
import org.isisaddons.module.togglz.glue.spi.FeatureState;

import org.estatio.module.settings.dom.ApplicationSettingForEstatio;

class FeatureStateForEstatio implements FeatureState {

    static FeatureState from(final ApplicationSetting applicationSetting) {
        return applicationSetting != null ? new FeatureStateForEstatio(applicationSetting) : null;
    }

    private final ApplicationSettingForEstatio applicationSetting;

    private FeatureStateForEstatio(final ApplicationSetting applicationSetting) {
        this.applicationSetting = (ApplicationSettingForEstatio) applicationSetting;
    }

    @Override
    public String getValue() {
        return applicationSetting.valueAsString();
    }

    @Override
    public void setValue(final String value) {
        applicationSetting.updateAsString(value);
    }

}
