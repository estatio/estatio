/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.dom;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;
import org.reflections.Reflections;

import org.apache.isis.applib.services.settings.ApplicationSettingsServiceRW;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;


public abstract class ApplicationSettingCreatorTestAbstract_create {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private ApplicationSettingsServiceRW mockAppSettings;
    
    private ApplicationSettingCreator[] creators;

    protected ApplicationSettingCreatorTestAbstract_create(ApplicationSettingCreator[] creators) {
        this.creators = creators;
    }
    
    @Test
    public void test() throws Exception {
        for (final ApplicationSettingCreator creator : creators) {
            final String name = creator.getClass().getPackage().getName() + "." + creator.name();
            final String description = creator.getDescription();
            final Object defaultValue = creator.getDefaultValue();
            context.checking(new Expectations() {
                {
                    if(creator.getDataType() == LocalDate.class) {
                        oneOf(mockAppSettings).newLocalDate(name, description, (LocalDate)defaultValue);
                    }
                    if(creator.getDataType() == Boolean.class) {
                        oneOf(mockAppSettings).newBoolean(name, description, (Boolean)defaultValue);
                    }
                    if(creator.getDataType() == Integer.class) {
                        oneOf(mockAppSettings).newInt(name, description, (Integer)defaultValue);
                    }
                    if(creator.getDataType() == Long.class) {
                        oneOf(mockAppSettings).newLong(name, description, (Long)defaultValue);
                    }
                    if(creator.getDataType() == String.class) {
                        oneOf(mockAppSettings).newString(name, description, (String)defaultValue);
                    }
                }
            });
            creator.create(mockAppSettings);
        }
    }

}
