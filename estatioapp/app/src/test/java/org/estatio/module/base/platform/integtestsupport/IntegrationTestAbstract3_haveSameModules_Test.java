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
package org.estatio.module.base.platform.integtestsupport;

import org.junit.Test;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.AppManifestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTestAbstract3_haveSameModules_Test {

    public static class SomeModule{}
    public static class OtherModule{}

    final AppManifest m1 = new AppManifestAbstract(AppManifestAbstract.Builder.forModules(SomeModule.class, OtherModule.class)) {
    };
    final AppManifest m1_different_order = new AppManifestAbstract(AppManifestAbstract.Builder.forModules(OtherModule.class, SomeModule.class)) {
    };
    final AppManifest m2 = new AppManifestAbstract(AppManifestAbstract.Builder.forModules(SomeModule.class, OtherModule.class)) {
    };
    final AppManifest m3 = new AppManifestAbstract(AppManifestAbstract.Builder.forModules(SomeModule.class)) {
    };

    @Test
    public void when_they_do() throws Exception {

        assertThat(IntegrationTestAbstract3.haveSameModules(m1, m2)).isTrue();
        assertThat(IntegrationTestAbstract3.haveSameModules(m1, m1_different_order)).isTrue();
    }
    @Test
    public void when_they_dont() throws Exception {

        assertThat(IntegrationTestAbstract3.haveSameModules(m1, m3)).isFalse();
    }
}