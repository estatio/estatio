/*
 *  Copyright 2016 Eurocommercial Properties NV
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
package org.estatio.module.capex.seed;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.estatio.module.capex.seed.ordertmplt.DocumentTemplateFSForOrderConfirm;
import org.estatio.module.capex.seed.ordertmplt.DocumentTypeFSForOrderConfirm;

import lombok.Getter;

@DomainObject(
        objectType = "org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture"
)
public class DocumentTypesAndTemplatesForCapexFixture extends DiscoverableFixtureScript {

    @Getter
    private final LocalDate templateDateIfAny;

    public DocumentTypesAndTemplatesForCapexFixture() {
        this(null);
    }

    public DocumentTypesAndTemplatesForCapexFixture(final LocalDate templateDateIfAny) {
        this.templateDateIfAny = templateDateIfAny;
    }

    @Override
    protected void execute(ExecutionContext ec) {

        ec.executeChild(this, new DocumentTypeFSForIncoming());
        ec.executeChild(this, new DocumentTypeFSForIbanProof());

        ec.executeChild(this, new DocumentTypeFSForOrderConfirm());
        ec.executeChild(this, new DocumentTemplateFSForOrderConfirm(templateDateIfAny));
    }
}
