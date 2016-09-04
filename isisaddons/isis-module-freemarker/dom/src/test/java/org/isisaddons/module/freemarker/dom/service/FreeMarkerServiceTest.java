/*
 *  Copyright 2013~2014 Dan Haywood
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
package org.isisaddons.module.freemarker.dom.service;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.services.config.ConfigurationService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FreeMarkerServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @JUnitRuleMockery2.Ignoring
    @Mock
    ConfigurationService mockConfigurationService;

    FreeMarkerService service;

    @Before
    public void setUp() throws Exception {
        service = new FreeMarkerService();

        service.configurationService = mockConfigurationService;

        service.init();
    }

    @Test
    public void usingProperties() throws Exception {

        // given
        Map<String, String> properties = ImmutableMap.of("user", "John Doe");

        // when
        String merged = service.render("a", "/", 1, "<h1>Welcome ${user}!</h1>",  properties);

        // then
        assertThat(merged, is("<h1>Welcome John Doe!</h1>"));
    }

    public static class UserDataModel {
        private String user;

        public String getUser() {
            return user;
        }

        public void setUser(final String user) {
            this.user = user;
        }
    }

    @Test
    public void usingDataModel() throws Exception {

        // given
        final UserDataModel userDataModel = new UserDataModel();
        userDataModel.setUser("John Doe");

        // when
        String merged = service.render("a", "/", 1, "<h1>Welcome ${user}!</h1>", userDataModel);

        // then
        assertThat(merged, is("<h1>Welcome John Doe!</h1>"));
    }

}
