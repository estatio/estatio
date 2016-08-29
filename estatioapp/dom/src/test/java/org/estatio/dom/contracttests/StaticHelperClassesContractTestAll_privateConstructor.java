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
package org.estatio.dom.contracttests;

import org.junit.Test;

import org.estatio.dom.PrivateConstructorTester;
import org.estatio.dom.WithCodeGetter;
import org.estatio.dom.WithDescriptionGetter;
import org.estatio.dom.WithNameGetter;
import org.estatio.dom.WithReferenceGetter;
import org.estatio.dom.WithTitleGetter;
import org.estatio.domsettings.ApplicationSettingCreator;

public class StaticHelperClassesContractTestAll_privateConstructor {

    @Test
    public void cover() throws Exception {
        exercise(ApplicationSettingCreator.Helper.class);
        exercise(WithCodeGetter.ToString.class);
        exercise(WithDescriptionGetter.ToString.class);
        exercise(WithNameGetter.ToString.class);
        exercise(WithReferenceGetter.ToString.class);
        exercise(WithTitleGetter.ToString.class);
    }

    private static void exercise(final Class<?> cls) throws Exception {
        new PrivateConstructorTester(cls).exercise();
    }
}
