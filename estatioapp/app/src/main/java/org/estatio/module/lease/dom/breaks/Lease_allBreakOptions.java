package org.estatio.module.lease.dom.breaks;

import org.apache.isis.applib.annotation.Mixin;

import javax.inject.Inject;
import java.util.List;

@Mixin(method = "coll")
public class Lease_allBreakOptions {

    public List<BreakOption> coll() {
        return ccoBreakOptionContributions.allBreakOptions();
    }

    @Inject
    LeaseService ccoBreakOptionContributions;
}
