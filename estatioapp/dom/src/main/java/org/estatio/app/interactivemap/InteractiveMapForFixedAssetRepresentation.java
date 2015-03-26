/*
 *  Copyright 2015 Eurocommercial Properties NV
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
package org.estatio.app.interactivemap;

import org.estatio.dom.utils.StringUtils;

public enum InteractiveMapForFixedAssetRepresentation {
    DEFAULT(InteractiveMapForFixedAssetColorServiceDefault.class),
    VACANT(InteractiveMapForFixedAssetColorServiceVacant.class),
    EXPIRY(InteractiveMapForFixedAssetColorServiceExpiry.class);

    private Class<? extends InteractiveMapForFixedAssetColorService> cls;

    private InteractiveMapForFixedAssetRepresentation(Class<? extends InteractiveMapForFixedAssetColorService> cls) {
        this.cls = cls;
    }

    public Class<? extends InteractiveMapForFixedAssetColorService> getCls() {
        return cls;
    }

    public InteractiveMapForFixedAssetColorService getColorService()
    {
        try {
            return cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String title() {
        return StringUtils.enumTitle(this.name());
    }
}
