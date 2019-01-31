package org.incode.module.document.spi.minio;

import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass
class Config {

    static final int ARCHIVE_AFTER_IN_WEEKS_DEFAULT = 0;  // 0 means archive immediately
    static final int PURGE_AFTER_IN_WEEKS_DEFAULT = 12;   // quartz job

    static int read(final Map<String, String> properties, final String key, final int fallback) {
        final String value = properties.get(key);
        try {
            int n = Integer.parseInt(value);
            if (n < 0) {
                return fallback;
            }
            return n;
        } catch (Exception ex) {
            return fallback;
        }
    }
}
