package com.eurocommercialproperties.estatio.audit;

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.runtimes.dflt.objectstores.jdo.applib.AuditEntry;
import org.apache.isis.runtimes.dflt.objectstores.jdo.applib.AuditService;
import org.apache.isis.runtimes.dflt.objectstores.jdo.applib.AuditServiceAbstract;

public class AuditServiceForEstatio extends AbstractFactoryAndRepository implements AuditService {

    public List<AuditEntryForEstatio> list() {
        return allInstances(AuditEntryForEstatio.class);
    }

    @Hidden
    public AuditEntry audit(String user, long currentTimestampEpoch, String objectType, String identifier, String preValue, String postValue) {
        AuditEntry auditEntry = newTransientInstance(AuditEntryForEstatio.class);
        auditEntry.setTimestampEpoch(currentTimestampEpoch);
        auditEntry.setUser(user);
        auditEntry.setObjectType(objectType);
        auditEntry.setIdentifier(identifier);
        auditEntry.setPreValue(preValue);
        auditEntry.setPostValue(postValue);
        return persist(auditEntry);
    }
    
}
