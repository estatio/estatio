/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.domsettings;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.isisaddons.module.settings.dom.ApplicationSetting;

import org.estatio.dom.appsettings.ApplicationSettingKey;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class EstatioApplicationSettingsServiceTest extends EstatioIntegrationTest {

    @Inject
    ApplicationSettingsServiceForEstatio applicationSettingsServiceForEstatio;

    @Before
    public void setupData() {
    }

    @Before
    public void setUp() throws Exception {
    }

    public static class ListAll extends EstatioApplicationSettingsServiceTest {

        @Test
        public void happyCase() throws Exception {
            // Given, when
            final List<ApplicationSetting> applicationSettings = applicationSettingsServiceForEstatio.listAll();
            // Then
            assertThat(applicationSettings.size()).isEqualTo(3);
        }
    }

    public static class Find extends EstatioApplicationSettingsServiceTest {

        @Test
        public void happyCase() throws Exception {
            // Given
            final ApplicationSettingKey key = ApplicationSettingKey.reportServerBaseUrl;
            // when
            final ApplicationSetting applicationSetting = ApplicationSettingCreator.Helper.find(key, applicationSettingsServiceForEstatio);
            // Then
            assertThat(applicationSetting.getValueRaw()).isEqualTo(key.getDefaultValue());
        }

        @Test
        public void updated() throws Exception {
            // Given
            final ApplicationSettingKey key = ApplicationSettingKey.reportServerBaseUrl;
            // when
            final ApplicationSettingForEstatio applicationSetting = (ApplicationSettingForEstatio) ApplicationSettingCreator.Helper.find(key, applicationSettingsServiceForEstatio);
            applicationSetting.setValueRaw("Changed");
            // Then
            assertThat(applicationSettingsServiceForEstatio.find(key).getValueRaw()).isEqualTo("Changed");
        }
    }

}
