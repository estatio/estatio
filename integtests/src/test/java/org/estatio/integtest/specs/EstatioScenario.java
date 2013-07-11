/*
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.integtest.specs;

import org.estatio.integtest.AbstractScenario;


public class EstatioScenario extends AbstractScenario<EstatioApp>  {

    /**
     * For instantiation by Cucumber-JVM only.
     * 
     * <p>
     * Annotated as deprecated to discourage accidental instantiation.
     */
    @Deprecated
    public EstatioScenario(EstatioApp app) {
        super(app);
    }

    public static EstatioApp currentApp() {
        return (EstatioApp) current().getApp();
    }

}