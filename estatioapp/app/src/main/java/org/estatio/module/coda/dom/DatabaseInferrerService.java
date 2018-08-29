package org.estatio.module.coda.dom;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class DatabaseInferrerService {

    static final String KEY = "isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionURL";

    public enum Driver {
        SQLSERVER,
        HSQLDB,
        UNKNOWN {
            @Override
            boolean matches(final String url) {
                // fallback
                return true;
            }
        };

        boolean matches(final String url) {
            return url != null && url.startsWith("jdbc:" + name().toLowerCase());
        }

        static Driver inferFrom(final String url) {
            for (final Driver driver : values()) {
                if(driver.matches(url)) {
                    return driver;
                }
            }
            return null;
        }

        public String lowerCaseName() {
            return name().toLowerCase();
        }
    }

    Driver driver;

    @PostConstruct
    public void init(Map<String,String> configMap) {
        final String url = configMap.get(KEY);
        driver = Driver.inferFrom(url);
    }

    @Programmatic
    public Driver getDriver() { return driver;}

}
