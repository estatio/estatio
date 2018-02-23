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
package org.estatio.module.application;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;

import org.estatio.module.budgetassignment.EstatioBudgetAssignmentModule;
import org.estatio.module.capex.EstatioCapexModule;
import org.estatio.module.guarantee.EstatioGuaranteeModule;
import org.estatio.module.link.EstatioLinkModule;
import org.estatio.module.registration.EstatioRegistrationModule;

/**
 * A "global" module for the entire app (=big ball of mud, stuff to decouple)
 */
@XmlRootElement(name = "module")
public final class EstatioApplicationModule extends ModuleAbstract {

    public EstatioApplicationModule(){}

    @Override
    public Set<Module> getDependencies(){
        return Sets.newHashSet(
                new EstatioGuaranteeModule(),
                new EstatioBudgetAssignmentModule(),
                new EstatioCapexModule(),
                new EstatioRegistrationModule(),
                new EstatioLinkModule()
        );
    }

}
