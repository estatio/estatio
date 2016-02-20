package org.estatio.app.services.properties;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

@DomainService(nature = NatureOfService.DOMAIN)
public class ApplicationPropertyService {

    private Map<String, String> properties;

    @Programmatic
    @PostConstruct
    public void init(final Map<String,String> properties) {
        this.properties = properties;
    }

    public Set<ApplicationProperty> allApplicationProperties() {
        Set<ApplicationProperty> kv = new TreeSet<>();
        properties.forEach((k, v) -> kv.add(new ApplicationProperty(k,v)));
        return kv;
    }

}
