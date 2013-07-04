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
package org.estatio.fixturescripts;

import java.util.concurrent.Callable;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Named;

@Named("Script")
public enum FixtureScript {

    GenerateTopModelInvoice(GenerateTopModelInvoice.class);
    
    private Class<? extends Callable<Object>> cls;

    private FixtureScript(Class<? extends Callable<Object>> cls) {
        this.cls = cls;
    }
    
    public Object run(DomainObjectContainer container) {
        final Callable<Object> callable = (Callable<Object>) container.newTransientInstance(cls);
        try {
            return callable.call();
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }
    
}
