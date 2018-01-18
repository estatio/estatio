/**
 * Copyright 2015-2016 Eurocommercial Properties NV
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.estatio.module.party.app.services.siren;

import java.util.Comparator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SirenResult implements Comparable<SirenResult> {
    @Getter
    private String chamberOfCommerceCode;

    @Getter
    private String companyName;

    @Override
    public int compareTo(final SirenResult o) {
        return Comparator
                .comparing(SirenResult::getChamberOfCommerceCode)
                .thenComparing(SirenResult::getCompanyName)
                .compare(this, o);
    }
}
