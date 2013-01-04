package org.estatio.audit;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.objectstore.jdo.applib.AuditService;


@Named("Audit")
@Hidden
public class EstatioAuditService extends AbstractFactoryAndRepository implements AuditService {

    public List<AuditEntryForEstatio> list() {
        return allInstances(AuditEntryForEstatio.class);
    }

    @Override
    @Hidden
    public void audit(String user, long currentTimestampEpoch, String objectType, String identifier, String preValue, String postValue) {
        AuditEntryForEstatio auditEntry = newTransientInstance(AuditEntryForEstatio.class);
        auditEntry.setTimestampEpoch(currentTimestampEpoch);
        auditEntry.setUser(user);
        auditEntry.setObjectType(objectType);
        auditEntry.setIdentifier(identifier);
        auditEntry.setPreValue(preValue);
        auditEntry.setPostValue(postValue);
        persist(auditEntry);
    }
}
