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
package org.estatio.module.lease.fixtures.prolongation.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.breaks.prolongation.ProlongationOption;
import org.estatio.module.lease.dom.breaks.prolongation.ProlongationOptionRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"lease"}, callSuper = false)
@ToString(of={"lease"})
@Accessors(chain = true)
public class ProlongationOptionBuilder extends BuilderScriptAbstract<ProlongationOption, ProlongationOptionBuilder> {

    @Getter @Setter
    Lease lease;
    @Getter @Setter
    String prolongationPeriod;
    @Getter @Setter
    String notificationPeriod;
    @Getter @Setter
    String description;

    @Getter
    private ProlongationOption object;

    @Override
    protected void execute(ExecutionContext ec) {

        checkParam("lease", ec, Lease.class);
        checkParam("prolongationPeriod", ec, String.class);
        checkParam("notificationPeriod", ec, String.class);
        defaultParam("description", ec, "Some description");

        final ProlongationOption prolongationOption = prolongationOptionRepository
                .newProlongationOption(lease, prolongationPeriod, notificationPeriod, description);

        ec.addResult(this, prolongationOption);

        object = prolongationOption;
    }

    @Inject
    ProlongationOptionRepository prolongationOptionRepository;

}
