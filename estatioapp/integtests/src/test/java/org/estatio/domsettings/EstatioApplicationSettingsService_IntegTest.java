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

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.isisaddons.module.settings.dom.ApplicationSetting;

import org.estatio.module.lease.dom.settings.LeaseInvoicingSettingKey;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.module.settings.dom.ApplicationSettingCreator;
import org.estatio.module.settings.dom.ApplicationSettingForEstatio;
import org.estatio.module.settings.dom.ApplicationSettingsServiceForEstatio;

import static org.assertj.core.api.Assertions.assertThat;

public class EstatioApplicationSettingsService_IntegTest extends EstatioIntegrationTest {

    @Inject
    ApplicationSettingsServiceForEstatio applicationSettingsServiceForEstatio;

    @Before
    public void setupData() {
    }

    @Before
    public void setUp() throws Exception {
    }

    public static class ListAll extends EstatioApplicationSettingsService_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // Given, when
            final List<ApplicationSetting> applicationSettings = applicationSettingsServiceForEstatio.listAll();
            // Then
            assertThat(applicationSettings.size()).isEqualTo(2);
        }
    }

    public static class Find extends EstatioApplicationSettingsService_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // Given
            final LeaseInvoicingSettingKey key = LeaseInvoicingSettingKey.epochDate;
            // when
            final ApplicationSetting applicationSetting = ApplicationSettingCreator.Helper.find(key, applicationSettingsServiceForEstatio);
            // Then
            assertThat(applicationSetting.getValueRaw()).isEqualTo(((LocalDate)key.getDefaultValue()).toString("yyyy-MM-dd"));
        }

        @Test
        public void updated() throws Exception {
            // given
            final LeaseInvoicingSettingKey key = LeaseInvoicingSettingKey.epochDate;
            // when
            final ApplicationSettingForEstatio applicationSetting = (ApplicationSettingForEstatio) ApplicationSettingCreator.Helper.find(key, applicationSettingsServiceForEstatio);
            final LocalDate localDate = new LocalDate(2010, 1, 1);
            applicationSetting.setValueRaw(localDate.toString("yyyy-MM-dd"));
            // then
            assertThat(applicationSettingsServiceForEstatio.find(key).getValueRaw()).isEqualTo(
                    localDate.toString("yyyy-MM-dd"));
        }
    }

}
