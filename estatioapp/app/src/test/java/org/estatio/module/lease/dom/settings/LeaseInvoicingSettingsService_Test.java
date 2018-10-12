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
package org.estatio.module.lease.dom.settings;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.ClassUnderTest;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.module.settings.dom.ApplicationSetting;
import org.estatio.module.settings.dom.ApplicationSettingsServiceForEstatio;
import org.estatio.module.settings.dom.SettingAbstract;
import org.estatio.module.settings.dom.SettingType;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseInvoicingSettingsService_Test {

    public static class EstatioSettingsServiceForTesting extends LeaseInvoicingSettingsService {

        @Override
        public String getId() {
            return getClass().getName();
        }

        @Override
        public void updateEpochDate(LocalDate epochDate) {
        }
    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private ApplicationSettingsServiceForEstatio mockApplicationSettingsService;
    @Mock
    private DomainObjectContainer mockDomainObjectContainer;

    @ClassUnderTest
    private EstatioSettingsServiceForTesting estatioSettingsService;

    @Programmatic
    static class ApplicationSettingForTesting extends SettingAbstract implements ApplicationSetting {
        private String valueRaw;
        private SettingType type;

        public ApplicationSettingForTesting(String valueRaw, SettingType type) {
            this.valueRaw = valueRaw;
            this.type = type;
        }

        public String getKey() {
            return null;
        }

        public String getDescription() {
            return null;
        }

        public SettingType getType() {
            return type;
        }

        public String getValueRaw() {
            return valueRaw;
        }
    }

    @Before
    public void setUp() throws Exception {
        estatioSettingsService.applicationSettingsService = mockApplicationSettingsService;
    }

    @Test
    public void happyCase() {
        final LocalDate date = new LocalDate(2013, 4, 1);
        context.checking(new Expectations() {
            {
                oneOf(mockApplicationSettingsService).find(LeaseInvoicingSettingKey.epochDate);
                will(returnValue(new ApplicationSettingForTesting(date.toString(SettingAbstract.DATE_FORMATTER), SettingType.LOCAL_DATE)));
            }
        });
        final LocalDate fetchEpochDate = estatioSettingsService.fetchEpochDate();
        assertThat(fetchEpochDate).isEqualTo(date);
    }

    @Test
    public void whenNull() {
        context.checking(new Expectations() {
            {
                oneOf(mockApplicationSettingsService).find(LeaseInvoicingSettingKey.epochDate);
                will(returnValue(null));
            }
        });
        final LocalDate fetchEpochDate = estatioSettingsService.fetchEpochDate();
        assertThat(fetchEpochDate).isNull();
    }

}
