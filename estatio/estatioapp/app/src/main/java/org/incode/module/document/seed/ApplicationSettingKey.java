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
package org.incode.module.document.seed;

import org.incode.module.document.spi.minio.Config;

import org.estatio.module.settings.dom.ApplicationSetting;
import org.estatio.module.settings.dom.ApplicationSettingCreator;
import org.estatio.module.settings.dom.ApplicationSettingsServiceRW;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApplicationSettingKey implements ApplicationSettingCreator {

    purgeBlobs(
            Boolean.class,
            "Whether to purge blobs",
            Boolean.FALSE,
            "org.incode.module.document"
    ),
    purgeBlobsAfterWeeks(
            Integer.class,
            "How many weeks to retain blobs if they are being purged",
            Config.PURGE_AFTER_IN_WEEKS_DEFAULT,
            "org.incode.module.document"
    );

    @Getter
    private final Class<?> dataType;
    @Getter
    private final String description;
    @Getter
    private final Object defaultValue;
    private final String prefix;

    @Override
    public void create(final ApplicationSettingsServiceRW appSettings) {
        Helper.create(this, appSettings);
    }
    @Override
    public ApplicationSetting find(final ApplicationSettingsServiceRW appSettings) {
        return Helper.find(this, appSettings);
    }
    @Override
    public String prefix() {
        return prefix;
    }

}

