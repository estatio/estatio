/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.dom.asset.registration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.asset.FixedAssetForTesting;

public class CadastralRegistrationTest_title {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DomainObjectContainer mockContainer;

    private CadastralRegistration cr;
    private FixedAssetForTesting subject;
    
    private FixedAssetRegistrationType type;

    @Before
    public void setUp() throws Exception {
        cr = new CadastralRegistration();
        cr.setContainer(mockContainer);
        
        subject = new FixedAssetForTesting();
        type = new FixedAssetRegistrationType();
        
        cr.setType(type);
        cr.setSubject(subject);
    }
    
    @Test
    public void whenEmpty() {
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).titleOf(type);
                will(returnValue("type"));
                
                oneOf(mockContainer).titleOf(subject);
                will(returnValue("subject"));
            }
        });
        assertThat(cr.title(), is("type: subject"));
    }

    @Test
    public void whenAnythingSet() {
        cr.setComuneAmministrativo("Comune Amministrativo");
        assertThat(cr.title(), is("Comune Amministrativo"));
    }

}
