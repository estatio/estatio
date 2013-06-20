package org.estatio.services.audit;

import java.util.List;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.audit.AuditingService;

import org.estatio.dom.EstatioDomainService;


@Named("Audit")
@Hidden
public class EstatioAuditService extends EstatioDomainService<AuditEntryForEstatio> implements AuditingService {

    public EstatioAuditService() {
        super(EstatioAuditService.class, AuditEntryForEstatio.class);
    }
    
    public List<AuditEntryForEstatio> list() {
        return allInstances();
    }

    @Override
    @Programmatic
    public void audit(String user, long currentTimestampEpoch, String objectType, String identifier, String preValue, String postValue) {
        AuditEntryForEstatio auditEntry = newTransientInstance();
        auditEntry.setTimestampEpoch(currentTimestampEpoch);
        auditEntry.setUser(user);
        auditEntry.setObjectType(objectType);
        auditEntry.setIdentifier(identifier);
        auditEntry.setPreValue(preValue);
        auditEntry.setPostValue(postValue);
        persist(auditEntry);
    }
}
