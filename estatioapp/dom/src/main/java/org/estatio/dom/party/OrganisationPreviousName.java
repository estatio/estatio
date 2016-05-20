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

package org.estatio.dom.party;

import java.io.Serializable;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Persistent;

import org.joda.time.LocalDate;

import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.utils.TitleBuilder;

import lombok.Getter;
import lombok.Setter;

public class OrganisationPreviousName implements Comparable<OrganisationPreviousName>, Serializable {

    public String title() {
        return TitleBuilder.start()
                .withName(getName())
                .withTupleElement(getEndDate())
                .toString();
    }

    // //////////////////////////////////////

    @Column(allowsNull = "false", length = JdoColumnLength.NAME)
    @Getter @Setter
    private String name;

    // //////////////////////////////////////

    @Persistent
    @Getter @Setter
    private LocalDate endDate;

    @Override public int compareTo(final OrganisationPreviousName o) {
        return getName().compareTo(o.getName());
    }
}
