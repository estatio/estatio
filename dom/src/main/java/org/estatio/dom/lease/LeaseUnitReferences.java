package org.estatio.dom.lease;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Prototype;

@Hidden
@Named("Lease Unit References")
public class LeaseUnitReferences extends AbstractFactoryAndRepository {

    @Override
    public String getId() {
        return "leaseUnitReferences";
    }

    public String iconName() {
        return "LeaseUnit";
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
    @Hidden
    public LeaseUnitReference newReference(final LeaseUnitReferenceType type, String reference, String name) {
        LeaseUnitReference leaseUnitReference = type.create(getContainer());
        leaseUnitReference.setReference(reference);
        leaseUnitReference.setName(name == null ? reference : name);
        persist(leaseUnitReference);
        return leaseUnitReference;
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
    @Hidden
    public LeaseUnitBrand newBrand(String reference, String name) {
        return (LeaseUnitBrand) newReference(LeaseUnitReferenceType.BRAND, reference, name);
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
    @Hidden
    public LeaseUnitSector newSector(String reference, String name) {
        return (LeaseUnitSector) newReference(LeaseUnitReferenceType.SECTOR, reference, name);
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
    @Hidden
    public LeaseUnitActivity newActivity(String reference, String name) {
        return (LeaseUnitActivity) newReference(LeaseUnitReferenceType.ACTIVITY, reference, name);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    @NotContributed
    @Hidden
    public LeaseUnitReference find(LeaseUnitReferenceType type, String reference) {
        throw new NotImplementedException();
    }

    @ActionSemantics(Of.IDEMPOTENT)
    @NotContributed
    @Hidden
    public LeaseUnitReference findOrCreate(LeaseUnitReferenceType type, String reference) {
        if (reference != null && reference.length() > 0) {
            LeaseUnitReference instance = find(type, reference);
            if (instance == null)
                instance = newReference(type, reference, null);
            return instance;
        }
        return null;
    }

    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<LeaseUnitReference> allReferences() {
        return allInstances(LeaseUnitReference.class);
    }

}
