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
package org.estatio.module.asset.dom;

import org.incode.module.base.dom.TitledEnum;
import org.incode.module.base.dom.utils.StringUtils;

public enum UnitType implements TitledEnum {

    BOUTIQUE,
    CINEMA,
    DEHOR,
    EXTERNAL,
    HYPERMARKET,
    MEDIUM,
    OFFICE,
    SERVICES,
    STORAGE,
    TECHNICAL_ROOM,
    COMMON_AREA,
    VIRTUAL,
    LAND,
    PARKING,
    ELECTRICAL_SUBSTATION,
    RESIDENTIAL,
    HOTEL,
    KIOSK,
    OTHER;

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    public static class Meta {
        private Meta(){}

        public final static int MAX_LEN = 30;
    }

}
