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
package org.estatio.module.capex.fixtures.project.personas;

import org.estatio.module.capex.fixtures.project.ProjectAbstract;
import org.estatio.module.capex.fixtures.project.ProjectBuilder;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;

public class Project1ForKal extends ProjectAbstract {

    public static final Project_enum data = Project_enum.KalProject1;

    @Override
    protected void execute(ExecutionContext executionContext) {

        final ProjectBuilder projectBuilder = new ProjectBuilder();
        projectBuilder.setReference(data.getRef())
                .setName(data.getName())
                .setStartDate(data.getStartDate())
                .setEndDate(data.getEndDate())
                .setEstimatedCost(null)
                .setAtPath(data.getApplicationTenancy().getPath())
                .setParent(null)
                .build(this, executionContext);

    }

}
