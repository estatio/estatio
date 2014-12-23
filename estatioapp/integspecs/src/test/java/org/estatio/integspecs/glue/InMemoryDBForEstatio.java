/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.estatio.integspecs.glue;

import org.apache.isis.core.specsupport.scenarios.InMemoryDB;
import org.apache.isis.core.specsupport.scenarios.ScenarioExecution;

import org.estatio.dom.WithCodeComparable;
import org.estatio.dom.WithNameComparable;
import org.estatio.dom.WithReferenceComparable;
import org.estatio.dom.WithTitleComparable;

public class InMemoryDBForEstatio extends InMemoryDB {
    
    public InMemoryDBForEstatio(ScenarioExecution scenarioExecution) {
        super(scenarioExecution);
    }
    
    /**
     * Hook to initialize if possible.
     */
    @Override
    protected void init(Object obj, String id) {
        if(obj instanceof WithReferenceComparable) {
            WithReferenceComparable<?> withRef = (WithReferenceComparable<?>) obj;
            withRef.setReference(id);
        }
        if(obj instanceof WithNameComparable) {
            WithNameComparable<?> withName = (WithNameComparable<?>) obj;
            withName.setName(id);
        }
        if(obj instanceof WithCodeComparable) {
            WithCodeComparable<?> withCode = (WithCodeComparable<?>) obj;
            withCode.setCode(id);
        }
        if(obj instanceof WithTitleComparable) {
            WithTitleComparable<?> withTitle = (WithTitleComparable<?>) obj;
            withTitle.setTitle(id);
        }
    }
}