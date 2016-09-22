/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.dom.project;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;

import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.TitledEnum;
import org.estatio.dom.utils.StringUtils;

public enum ProjectRoleType implements TitledEnum {

    PROJECT_EXECUTIVE,
    SENIOR_SUPPLIER,
    SENIOR_USER,
    PROJECT_MANAGER;
    

    public String title() {
        return StringUtils.enumTitle(this.toString());
    }
    
    @Programmatic
    public Predicate<? super ProjectRole> matchingRole() {
        return new Predicate<ProjectRole>() {
            @Override
            public boolean apply(final ProjectRole pr) {
                return pr != null && Objects.equal(pr.getType(), this) ? true : false;
            }
        };
    }

}
